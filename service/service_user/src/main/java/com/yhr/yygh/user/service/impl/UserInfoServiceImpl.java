package com.yhr.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhr.yygh.commom.exception.YyghException;
import com.yhr.yygh.commom.helper.JwtHelper;
import com.yhr.yygh.commom.result.ResultCodeEnum;
import com.yhr.yygh.enums.AuthStatusEnum;
import com.yhr.yygh.model.user.Patient;
import com.yhr.yygh.model.user.UserInfo;
import com.yhr.yygh.user.mapper.UserInfoMapper;
import com.yhr.yygh.user.service.PatientService;
import com.yhr.yygh.user.service.UserInfoService;
import com.yhr.yygh.vo.user.LoginVo;
import com.yhr.yygh.vo.user.UserAuthVo;
import com.yhr.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {

        //从loginVo获取手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //判断手机号和验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //判断手机验证码和输入验证码是否一致
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = selectWxInfoOpenId(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        //如果userinfo为空，进行正常手机登陆
        if (userInfo == null){
            //判断是否第一次登陆：根据手机号查询数据库，如果不存在相同手机号就是第一次登录
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        //校验是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //不是第一次就直接登陆
        //返回登录信息
        //返回登录用户名
        //返回token
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //Jwt生成token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token",token);
        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> page1, UserInfoQueryVo userInfoQueryVo) {
        //userInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword();
        Integer status = userInfoQueryVo.getStatus();
        Integer authStatus = userInfoQueryVo.getAuthStatus();
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)){
            wrapper.like("name",name);
        }
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        if (!StringUtils.isEmpty(authStatus)){
            wrapper.eq("auth_status",authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge("create_time",createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le("create_time",createTimeEnd);
        }
        Page<UserInfo> userInfoPage = baseMapper.selectPage(page1, wrapper);
        userInfoPage.getRecords().stream().forEach(item -> {
            packageUserInfo(item);
        });
        return userInfoPage;
    }

    private void packageUserInfo(UserInfo userInfo) {
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        String statusString = userInfo.getStatus() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
    }

    @Override
    public void lock(Long userId, Integer status) {
        if (status == 0 || status == 1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userId查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        packageUserInfo(userInfo);
        map.put("userInfo",userInfo);

        //根据userId查询就诊人信息
        List<Patient> list = patientService.findAllUserId(userId);
        map.put("patientList",list);
        return map;
    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus == 2 || authStatus == -1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }
}
