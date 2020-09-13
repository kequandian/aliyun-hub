package com.jfeat.upload.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zy on 2019/1/17.
 * 阿里OSS api调用封装
 *
 * 开发文档
 * https://help.aliyun.com/document_detail/32008.html?spm=a2c4g.11174283.3.3.23667da2uyjWxs
 * Github
 * https://github.com/aliyun/aliyun-oss-java-sdk
 */
public class OSSUtil {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;

    private OSSUtil() {}

    /**
     * http://bucketName.endpoint/objectName
     **/
    public static OSSUtil build(String endpoint, String accessKeyId, String accessKeySecret) {
        OSSUtil instant = new OSSUtil();
        instant.endpoint = endpoint;
        instant.accessKeyId = accessKeyId;
        instant.accessKeySecret = accessKeySecret;
        return instant;
    }

    public static void main(String[] args) throws IOException {
        String endpoint = "your endpoint";
        String accessKeyId = "your accessKeyId";
        String accessKeySecret = "your accessKeySecret";
        String bucketName = "your bucketName";

        OSSUtil util = OSSUtil.build(endpoint, accessKeyId, accessKeySecret);
        String fileName = "tmp/upload-test.txt";
        String testFileName = "本地测试文件";
        /**
         * 文件上传后访问url为
         * http://${bucketName}.oss-cn-shenzhen.aliyuncs.com/${fileName}
         **/


        /**
         * 上传文件
         **/
        util.upload(bucketName, fileName, createSampleFile());

        String content = "Input Stream Test";
        util.upload(bucketName, fileName, new ByteArrayInputStream(content.getBytes()));

        /**
         * 获取bucket中文件列表
         **/
       System.out.println(util.getFileList(bucketName));

        /**
         * 获取一个小时的临时授权url
         **/
        Date expiration = new Date();
        expiration.setTime(new Date().getTime() + 60 * 60 * 1000l);
        System.out.println(util.generatePresignedUrl(bucketName, fileName, expiration));

        /**
         * 分配上传本地文件
         **/
        try {
            util.multipartUpload(new OSSUtil.UploadRequestBuilder().build(bucketName, fileName, testFileName));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private OSS buildOSSClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     *
     * @param bucketName
     * @return
     **/
    public List<String> getFileList(String bucketName) {
        List<String> result = new ArrayList<>();
        OSS ossClient = buildOSSClient();
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        result.addAll(objectListing.getObjectSummaries().stream().map(OSSObjectSummary::getKey).collect(Collectors.toList()));
        ossClient.shutdown();
        return result;
    }
    /**
     *
     * @param bucketName
     * @return
     **/
    public List<OSSObjectSummary> getFileInfoList(String bucketName) {
        OSS ossClient = buildOSSClient();
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        ossClient.shutdown();
        return objectListing == null ? null : objectListing.getObjectSummaries();
    }

    /**
     * 上传本地文件
     * @param bucketName
     * @param uploadName
     * @param localFileName
     * @return
     **/
    public void upload(String bucketName, String uploadName, String localFileName) {
        upload(bucketName, uploadName, new File(localFileName));
    }
    /**
     * 上传本地文件
     * @param bucketName
     * @param uploadName
     * @param localFile
     * @return
     **/
    public void upload(String bucketName, String uploadName, File localFile) {
        OSS ossClient = buildOSSClient();
        ossClient.putObject(new PutObjectRequest(bucketName, uploadName, localFile));
        ossClient.shutdown();
    }

    /**
     * 通过输入流上传文件
     * @param bucketName
     * @param uploadName
     * @param inputStream
     * @return
     **/
    public void upload(String bucketName, String uploadName, InputStream inputStream) {
        OSS ossClient = buildOSSClient();
        ossClient.putObject(new PutObjectRequest(bucketName, uploadName, inputStream));
        ossClient.shutdown();
    }

    /**
     * 分片上传本地文件
     * @param uploadFileRequest
     * @return
     **/
    public void multipartUpload(UploadFileRequest uploadFileRequest) throws Throwable {
        OSS ossClient = buildOSSClient();
        try {
            UploadFileResult uploadResult = ossClient.uploadFile(uploadFileRequest);
            CompleteMultipartUploadResult multipartUploadResult =
                    uploadResult.getMultipartUploadResult();
            System.out.println(multipartUploadResult.getETag());
        } finally {
            ossClient.shutdown();
        }
    }
    public static class UploadRequestBuilder {
        private int taskNum = 1;
        private int partSize = 1 * 1024 * 1024;
        private boolean enableCheckPoint = true;
        /**
         * 并行处理任务数
         * @param  taskNum
         * @return this
         **/
        public UploadRequestBuilder taskNum(int taskNum) {
            this.taskNum = taskNum;
            return this;
        }
        /**
         * 分片大小
         * @param partSize
         * @return this
         **/
        public UploadRequestBuilder partSize(int partSize) {
            this.partSize = partSize;
            return this;
        }

        public UploadRequestBuilder enableCheckPoint(boolean enableCheckPoint) {
            this.enableCheckPoint = enableCheckPoint;
            return this;
        }
        public UploadFileRequest build(String bucketName, String uploadName, String localFileName) {
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, uploadName);
            uploadFileRequest.setUploadFile(localFileName);
            uploadFileRequest.setTaskNum(taskNum);
            uploadFileRequest.setPartSize(partSize);
            uploadFileRequest.setEnableCheckpoint(enableCheckPoint);
            return uploadFileRequest;
        }
    }

    /**
     * 生成签名URL提供给访客进行临时访问
     * @param bucketName
     * @param targetName
     * @param expiration
     * @returntemporary
     **/
    public URL generatePresignedUrl(String bucketName, String targetName, Date expiration) {
        OSS ossClient = buildOSSClient();
        URL url = ossClient.generatePresignedUrl(bucketName, targetName, expiration);
        ossClient.shutdown();
        return url;
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * for test
     **/
    @Deprecated
    private static File createSampleFile() throws IOException {
        File file = File.createTempFile("oss-java-sdk-", ".txt");
        file.deleteOnExit();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("0123456789011234567890\n");
        writer.close();

        return file;
    }

}
