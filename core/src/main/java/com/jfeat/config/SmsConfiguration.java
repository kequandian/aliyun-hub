package com.jfeat.config;

import com.jfeat.properties.AliyunProperties;
import com.jfeat.sms.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * Created on 2020/9/29 11:55 上午.
 *
 * @author Wen Hao
 */
@Configuration
@Slf4j
public class SmsConfiguration {

    @Resource
    AliyunProperties aliyunProperties;

    @PostConstruct
    public void setSmsUtilProperties() {
        log.info("start config sms properties ...");
        AliyunProperties.SmsProperties sms = aliyunProperties.getSms();
        if (sms == null) {
            log.warn("sms properties is empty!");
            return;
        }
        /* 是否全局禁止短信发送 */
        Boolean enable = Optional.ofNullable(sms.getEnable()).orElse(true);
        log.info("sms global enable : {}", enable);
        if (!enable) {
            log.warn("sms 短信设置为全局禁止！");
        }
        SmsUtil.init(sms.getAccessKeyId(), sms.getAccessSecret(), sms.getSignName(), enable);

        log.info("config sms properties end");
    }
}
