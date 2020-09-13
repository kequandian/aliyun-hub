package com.jfeat.upload.api;



import com.jfeat.crud.base.exception.BusinessCode;
import com.jfeat.crud.base.exception.BusinessException;
import com.jfeat.crud.base.tips.SuccessTip;
import com.jfeat.crud.base.tips.Tip;
import com.jfeat.crud.base.util.StrKit;
import com.jfeat.upload.config.IOProperties;
import com.jfeat.upload.services.LoadFileCodeService;
import com.jfeat.upload.util.FileInfo;
import com.jfeat.upload.util.ImageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by jackyhuang on 2017/7/4.
 */
@Api(value = "Service")
@RestController
public class FileServiceEndpoint {


    protected final static Logger logger = LoggerFactory.getLogger(FileServiceEndpoint.class);

    @Autowired
    IOProperties ioProperties;

    @Autowired
    LoadFileCodeService loadFileCodeService;

    @ApiOperation(value = "获取下载码", response = String.class, notes = "登陆后自动生成的一个下载码")
    @ApiParam(name = "name", value = "文件名称")
    @GetMapping("/api/fs/dlcode")
    public Tip getCode(@RequestParam String name) {
        String code = loadFileCodeService.genAndGetCode(name);
        return SuccessTip.create(code);
    }

    /**
     * form-data 方式上传图片
     *
     * @param picture
     * @return
     */
    @ApiOperation(value = "multipart方式上传图片", response = FileInfo.class)
    @PostMapping("/api/fs/uploadx")
    @ResponseBody
    public Tip formUpload(@RequestHeader("authorization") String token,
                          @RequestParam(name = "blur", defaultValue = "false") Boolean blur,
                          @RequestPart("file") MultipartFile picture) {
        String originalFileName = picture.getOriginalFilename();
        String extensionName = getExtensionName(originalFileName);
        String pictureName = UUID.randomUUID().toString() + "." + extensionName;
        String blurryName = "";
        try {
            String fileSavePath = getFileUploadPath();
            File target = new File(fileSavePath + pictureName);
            target.setReadable(true);
            FileUtils.copyInputStreamToFile(picture.getInputStream(), target);
            logger.info("file uploaded to: {}", target.getAbsolutePath());
            File reducedFile = ImageUtil.reduce(target);
            logger.info("file reduced to: {}", reducedFile.getAbsolutePath());
            pictureName = reducedFile.getName();
            if (blur) {
                File blurryFile = ImageUtil.reduce(ImageUtil.gaos(target));
                blurryFile.setReadable(true);
                blurryName = blurryFile.getName();
            }
        } catch (Exception e) {
            throw new BusinessException(BusinessCode.UploadFileError);
        }
        return SuccessTip.create(FileInfo.create(getFileHost(), pictureName, blurryName));
    }

    /**
     * 数据格式
     * data:image/jpeg;base64,/9j/4AAQSkZJRgABAQ
     *
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "Base64格式上传图片", response = FileInfo.class)
    @PostMapping("/api/fs/upload64")
    @ResponseBody
    public Tip base64Upload(@RequestHeader("authorization") String token,
                            @RequestParam(name = "blur", defaultValue = "false") Boolean blur, HttpServletRequest request) throws IOException {
        String base64Data = IOUtils.toString(request.getInputStream(), "UTF-8");
        if (StrKit.isBlank(base64Data)) {
            throw new BusinessException(BusinessCode.UploadFileError);
        }

        String dataPrix = "";
        String data = "";
        String[] d = base64Data.split("base64,");
        if (d != null && d.length == 2) {
            dataPrix = d[0];
            data = d[1];
        } else {
            throw new BusinessException(BusinessCode.UploadFileError);
        }

        String suffix = "";
        if ("data:image/jpeg;".equalsIgnoreCase(dataPrix)) {
            suffix = ".jpg";
        } else if ("data:image/x-icon;".equalsIgnoreCase(dataPrix)) {
            suffix = ".ico";
        } else if ("data:image/gif;".equalsIgnoreCase(dataPrix)) {
            suffix = ".gif";
        } else if ("data:image/png;".equalsIgnoreCase(dataPrix)) {
            suffix = ".png";
        } else {
            throw new BusinessException(BusinessCode.UploadFileError);
        }

        byte[] dataBytes = Base64Utils.decodeFromString(data);

        String pictureName = UUID.randomUUID().toString() + suffix;
        String blurryName = "";
        try {
            String fileSavePath = getFileUploadPath();
            File target = new File(fileSavePath, pictureName);
            target.setReadable(true);
            FileUtils.writeByteArrayToFile(target, dataBytes);
            logger.info("file uploaded to: {}", target.getAbsolutePath());
            File reducedFile = ImageUtil.reduce(target);
            logger.info("file reduced to: {}", reducedFile.getAbsolutePath());
            pictureName = reducedFile.getName();

            File thumbFile = thumb(reducedFile);
            logger.info("file thumb to: {}", thumbFile.getAbsoluteFile());

            if (blur) {
                File blurryFile = ImageUtil.reduce(ImageUtil.gaos(reducedFile));
                blurryFile.setReadable(true);
                blurryName = blurryFile.getName();

                File blurryThumbFile = thumb(blurryFile);
                logger.info("blurry file thumb to: {}", blurryThumbFile.getAbsoluteFile());
            }
        } catch (Exception ee) {
            throw new BusinessException(BusinessCode.UploadFileError);
        }

        return SuccessTip.create(FileInfo.create(getFileHost(), pictureName, blurryName));
    }

    @ApiOperation(value = "上传文件", response = FileInfo.class)
    @PostMapping("/api/fs/uploadfile")
    @ResponseBody
    public Tip FileUpload(@RequestHeader("authorization") String token,
                          @RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("file is empty");
        }
        String originalFileName = file.getOriginalFilename();
        String extensionName = getExtensionName(originalFileName);
        Long fileSize = file.getSize();
        String fileName = UUID.randomUUID().toString() + "." + extensionName;
        String path = null;
        try {
            String fileSavePath = getFileUploadPath();
            File target = new File(fileSavePath + fileName);
            path = target.getCanonicalPath();
            target.setReadable(true);
            FileUtils.copyInputStreamToFile(file.getInputStream(), target);
            logger.info("file uploaded to: {}", target.getAbsolutePath());
        } catch (Exception e) {
            throw new BusinessException(BusinessCode.UploadFileError);
        }
        return SuccessTip.create(FileInfo.create(getFileHost(), fileName, extensionName, originalFileName, fileSize, path));
    }

    @ApiOperation(value = "下载文件")
    @GetMapping("/api/pub/fs/loadfile")
    @ResponseBody
    public void loadFile(@RequestParam(required = true) String name,
                         @RequestParam(required = true) String code, HttpServletResponse response) throws IOException {
        if (loadFileCodeService.checkCode(code) == false) {
            throw new BusinessException(BusinessCode.BadRequest);
        }

        //获得rootPath
        String rootPath = getFileUploadPath();
        //拼接绝对路径(目录)
        String absolutePath = rootPath + name;
        //获得资源对象
        FileSystemResource fsr = new FileSystemResource(absolutePath);

        //输出文件
        final int buffInt = 1024;
        byte[] buff = new byte[buffInt];
        OutputStream os = null;
        BufferedInputStream bis = null;
        try { //NOSONAR
//            HttpServletResponse response = getHttpServletResponse();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; " + name);
            os = response.getOutputStream();
            bis = new BufferedInputStream(fsr.getInputStream());
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    private String getFileHost() {
        String fileHost = ioProperties.getFileHost();
        String host = fileHost;
//        String tenant = JWTKit.getTenantId(getHttpServletRequest()) + "";
        // TODO 租户
        String tenant = "TODO";
        if (!fileHost.endsWith("/")) {
            host = host + "/" + tenant;
        } else {
            host = host + tenant;
        }
        return host;
    }

    private String getFileUploadPath() {
        String fileSavePath = ioProperties.getFileUploadPath();
        String uploadPath = fileSavePath;
//        String tenant = JWTKit.getTenantId(getHttpServletRequest()) + "";
        // TODO 租户
        String tenant = "TODO";
        if (!fileSavePath.endsWith(File.separator)) {
            uploadPath = uploadPath + File.separator + tenant + File.separator;
        } else {
            uploadPath = uploadPath + tenant + File.separator;
        }

        File file = new File(uploadPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return uploadPath;
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


    public static File thumb(File sourceFile) throws IOException {
        int width, height;
        int[] originalSize = getImgWidth(sourceFile);
        int originalWidth = originalSize[0];
        int originalHeight = originalSize[1];
        double rate1 = (double) originalWidth / (double) 160;
        double rate2 = (double) originalHeight / (double) 160;
        double rate = rate1 < rate2 ? rate1 : rate2;
        width = (int) (originalWidth / rate);
        height = (int) (originalHeight / rate);

        // 开始读取文件并进行压缩
        // https://blog.csdn.net/kobejayandy/article/details/44346809
        // fix 图片红色问题
        Image image = Toolkit.getDefaultToolkit().createImage(sourceFile.getPath());
        BufferedImage src = toBufferedImage(image);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D reducedG = tag.createGraphics();
        String format = "jpeg";
        List<Integer> pngTypes = new ArrayList<>(Arrays.asList(
                BufferedImage.TYPE_INT_ARGB, BufferedImage.TYPE_4BYTE_ABGR, BufferedImage.TYPE_4BYTE_ABGR_PRE, BufferedImage.TYPE_INT_ARGB_PRE
        ));
        if (pngTypes.contains(src.getType())) {
            tag = reducedG.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            reducedG.dispose();
            reducedG = tag.createGraphics();
            format = "jpeg";
        }
        reducedG.drawImage(src.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        reducedG.dispose();

        String extension = FilenameUtils.getExtension(sourceFile.getName());
        String baseName = FilenameUtils.getBaseName(sourceFile.getName());
        String fileName = baseName + "-thumb." + extension;

        File targetFile = new File(sourceFile.getParent(), fileName);
        ImageIO.write(tag, format, targetFile);
        return targetFile;
    }

    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    public static int[] getImgWidth(File file) throws IOException {
        InputStream is = null;
        BufferedImage src = null;
        int[] result = new int[]{0, 0};
        is = new FileInputStream(file);
        src = ImageIO.read(is);
        result[0] = src.getWidth((ImageObserver) null);
        result[1] = src.getHeight((ImageObserver) null);
        is.close();
        return result;
    }
}
