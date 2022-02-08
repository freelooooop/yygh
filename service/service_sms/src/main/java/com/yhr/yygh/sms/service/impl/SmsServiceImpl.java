package com.yhr.yygh.sms.service.impl;

import com.yhr.yygh.sms.service.SmsService;
import com.yhr.yygh.sms.utils.HttpUtils;
import com.yhr.yygh.vo.msm.MsmVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean send(String phoneNum, String codeNum) {
        if (StringUtils.isEmpty(phoneNum)) {
            return false;
        }
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "9ce75df420cd4ab5b0048d30193792fe";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        bodys.put("content", "code:"+codeNum);
        bodys.put("phone_number", phoneNum);
        bodys.put("template_id", "TPL_0000");
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            System.out.println(EntityUtils.toString(response.getEntity()));
            return true;
            //获取response的body

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean send(MsmVo msmVo) {
        if (!StringUtils.isEmpty(msmVo.getPhone())){
            boolean isSend = send(msmVo.getPhone(), "nihao");
            return isSend;
        }
        return false;
    }
}


