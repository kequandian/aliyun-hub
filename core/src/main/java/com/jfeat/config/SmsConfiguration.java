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
        AliyunProperties.SmsProperties sms = aliyunProperties.getSms();
        if (sms == null) {
            log.warn("sms properties is empty!");
            return;
        }
        SmsUtil.init(sms.getAccessKeyId(), sms.getAccessSecret(), sms.getSignName());

    }
}
