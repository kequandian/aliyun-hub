package com.jfeat.orc.alibaba.ocr.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jfeat.crud.base.tips.SuccessTip;
import com.jfeat.orc.enums.Method;
import com.jfeat.orc.Client;
import com.jfeat.orc.Request;
import com.jfeat.orc.Response;
import com.jfeat.orc.constant.Constants;
import com.jfeat.orc.constant.ContentType;
import com.jfeat.orc.constant.HttpHeader;
import com.jfeat.orc.util.MessageDigestUtil;
import com.jfeat.properties.AliyunProperties;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用api网关，通过app_key进行身份认证
 */
@Component
public class APPKeyUtil {

    @Resource
    AliyunProperties aliyunProperties;

    //自定义参与签名Header前缀（可选,默认只有"X-Ca-"开头的参与到Header签名）
    private final static List<String> CUSTOM_HEADERS_TO_SIGN_PREFIX = new ArrayList<String>();

    /*
     * 获取参数的json对象
     */
    public static JSONObject getParam(int type, String dataValue) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("dataType", type);
            obj.put("dataValue", dataValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


    public  JSONObject OrcOne (String imgFile){
        //请求path
        String host = "http://dm-58.data.aliyun.com";
        String path = "/rest/160601/ocr/ocr_business_license.json";
        String APP_KEY = aliyunProperties.getOrc().getAppKey();
        String APP_SECRET = aliyunProperties.getOrc().getAppSecret();

       /* String imgFile = "图片路径/图片URL";*/

        //请根据线上文档修改configure字段
        JSONObject configObj = new JSONObject();
        configObj.put("side", "face");
        String config_str = configObj.toString();
        //            configObj.put("min_size", 5);
        //            String config_str = "";

        // 对图像进行base64编码
        String imgBase64 = "";
        try {
            if(imgFile.startsWith("http")){
                imgBase64 = imgFile;
            }else {
                File file = new File(imgFile);
                byte[] content = new byte[(int) file.length()];
                FileInputStream finputstream = new FileInputStream(file);
                finputstream.read(content);
                finputstream.close();
                imgBase64 = new String(Base64.encodeBase64(content));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("image", imgBase64);
            if(config_str.length() > 0) {
                requestObj.put("configure", config_str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Body内容
        String body = requestObj.toString();


        Map<String, String> headers = new HashMap<String, String>();
        //（必填）根据期望的Response内容类型设置
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, "application/json");
        //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMD5(body));
        //（POST/PUT请求必选）请求Body内容格式
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_TEXT);


        Request request = new Request(Method.POST_STRING, host, path, APP_KEY, APP_SECRET, Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setSignHeaderPrefixList(CUSTOM_HEADERS_TO_SIGN_PREFIX);
        request.setStringBody(body);


        //调用服务端
        Response response = null;
        try {
            response = Client.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(response.getStatusCode() != 200){
            System.out.println("Http code: " + response.getStatusCode());
            System.out.println("Http header error: " + response.getHeader("X-Ca-Error-Message"));
            System.out.println("Http body error msg: " + response.getBody());
            return null;
        }
        String res = response.getBody();
        JSONObject res_obj = JSON.parseObject(res);
        System.out.println(res_obj.toJSONString());
        return res_obj;
    }
}
