package com.jfeat.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.jfeat.properties.AliyunProperties;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Created on 2020/9/28 6:02 下午.
 *
 * @author Wen Hao
 */
@UtilityClass
@Slf4j
public class SmsUtil {

    private final String domain = "dysmsapi.aliyuncs.com";

    private AliyunProperties aliyunProperties = SpringContextUtil.getBean(AliyunProperties.class);

    private String accessKeyId = Optional.ofNullable(aliyunProperties.getSms())
            .map(AliyunProperties.SmsProperties::getAccessKeyId).orElse(null);

    private String accessSecret = Optional.ofNullable(aliyunProperties.getSms())
            .map(AliyunProperties.SmsProperties::getAccessSecret).orElse(null);

    private IAcsClient client = null;

    static {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        client = new DefaultAcsClient(profile);
    }

    public void sendSms(String phoneNumbers, String signName, String templateCode, String templateParam) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


    @SneakyThrows
    public void main(String[] args) {
        String phone = "";
        String signName = "";
        String templateCode = "";
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("code", "123");
        String templateParam = jsonParam.toString();
        log.info("templateParam : {}", templateCode);
        sendSms(phone,signName, templateCode, templateParam);
    }
}
