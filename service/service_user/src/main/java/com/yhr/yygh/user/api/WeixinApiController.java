package com.yhr.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.yhr.yygh.commom.helper.JwtHelper;
import com.yhr.yygh.commom.result.Result;
import com.yhr.yygh.model.user.UserInfo;
import com.yhr.yygh.user.service.UserInfoService;
import com.yhr.yygh.user.utils.ConstantPropertiesUtil;
import com.yhr.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    //微信扫码后回调的方法
    @GetMapping("callback")
    public String callback(String code,String state){
        System.out.println("code:"+code);
        //拿着code和微信id和密钥，请求微信固定地址，得到两个值
        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        //使用httpclient请求这个地址
        try {
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessTokenInfo:"+accessTokenInfo);
            JSONObject jsonObject = JSONObject.parseObject(accessTokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            //判断数据库中是否已经存在此人信息
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            if (userInfo == null){
                //拿着openid和access_token请求微信地址，得到扫码人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println(resultInfo);
                JSONObject resultObject = JSONObject.parseObject(resultInfo);
                //解析用户信息
                String nickname = resultObject.getString("nickname");//用户昵称
                String headimgurl = resultObject.getString("headimgurl");//用户头像

                //将信息添加到数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }
            //返回name和token字符串
            Map<String,String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            return "redirect:" + ConstantPropertiesUtil.YYGH_BASE_URL +
                    "/weixin/callback?token="+map.get("token")+"&openid="
                    +map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取微信登录参数
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
        return Result.ok(map);
    }
}