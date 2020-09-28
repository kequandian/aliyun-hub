package com.jfeat.properties;

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
@ConfigurationProperties(prefix = "fs")
public class FSProperties {

    private String fileUploadPath;

    private String fileHost;

}
