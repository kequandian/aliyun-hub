package com.jfeat.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 2020/6/28.
 *
 * @author Wen Hao
 */

@Data
@Component
@ConfigurationProperties(prefix = "io")
public class IOProperties {

    private String fileUploadPath;

    private String fileHost;

    private SSOProperties sso = new SSOProperties();

    @Data
    public class SSOProperties {
        private String endpoint = "";
        private String accessKeyId = "";
        private String accessKeySecret = "";
        private String bucketName = "";
        private String username = "";
        /**
         * 图片压缩参数
         **/
        private String thumbParam = "x-oss-process=image/resize,m_fill,h_750,w_750";
        /**
         * 访问域名
         **/
        private String accessUrl = "";
    }

}
