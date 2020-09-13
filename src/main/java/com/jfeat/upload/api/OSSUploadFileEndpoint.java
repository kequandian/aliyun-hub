package com.jfeat.upload.api;

import com.jfeat.crud.base.tips.SuccessTip;
import com.jfeat.crud.base.tips.Tip;
import com.jfeat.upload.util.FileInfo;
import com.jfeat.upload.util.OSSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created on 2020/6/28.
 *
 * @author Wen Hao
 */


@Api(value = "Service")
@RestController
@RequestMapping("/api/cms/oss/upload")
public class OSSUploadFileEndpoint {

    /**
     * 访问URL 参考
     * http://muaskin-test.oss-cn-shenzhen.aliyuncs.com/10f2094d-534b-43c0-b843-b8a93a27b2a4/Pore.png
     **/

    public static final String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
    public static final String accessKeyId = "";
    public static final String accessKeySecret = "";
    public static final String bucketName = "";
    public static String username = "";

    /**
     * 图片压缩参数
     **/
    public static final String thumbParam = "x-oss-process=image/resize,m_fill,h_750,w_750";
    /**
     * 访问域名
     **/
    public static final String accessUrl = "http://muaskin.oss-cn-shenzhen.aliyuncs.com/";

    @ApiOperation(value = "上传文件",response = FileInfo.class)
    @PostMapping("/files")
    @ResponseBody
    public Tip FileUpload(@RequestHeader("authorization") String token,
                          @RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("file is empty");
        }
        String originalFileName = file.getOriginalFilename();
        String extensionName= FilenameUtils.getExtension(originalFileName);
        Long fileSize = file.getSize();

        String targetFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        OSSUtil util = OSSUtil.build(endpoint, accessKeyId, accessKeySecret);
        util.upload(bucketName, targetFileName, file.getInputStream());
        return SuccessTip.create(FileInfo.create(accessUrl, targetFileName,extensionName,originalFileName,fileSize));
    }
}
