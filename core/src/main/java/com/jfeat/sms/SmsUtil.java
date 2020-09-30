package com.jfeat.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2020/9/28 6:02 下午.
 *
 * @author Wen Hao
 */
@UtilityClass
@Slf4j
public class SmsUtil {

    /**
     * 是否发送短信
     */
    private boolean smsEnable;

    private final String domain = "dysmsapi.aliyuncs.com";

    private String accessKeyId;

    private String accessSecret;

    /**
     * 短信签名
     */
    private String signName;

    private IAcsClient client = null;

    public void init(String accessKeyId, String accessSecret, String signName, Boolean smsEnable) {
        SmsUtil.accessKeyId = accessKeyId;
        SmsUtil.accessSecret = accessSecret;
        SmsUtil.signName = signName;
        SmsUtil.smsEnable = smsEnable;
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        client = new DefaultAcsClient(profile);
    }

    public void sendSms(String phoneNumber, String templateCode, String templateParam) {
        if (!smsEnable) {
            return;
        }
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(domain);
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


    @SneakyThrows
    public void main(String[] args) {
        String phone = "";
        String templateCode = "";
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("code", "123");
        String templateParam = jsonParam.toString();
        log.info("templateParam : {}", templateCode);
        sendSms(phone,templateCode, templateParam);
    }
}
