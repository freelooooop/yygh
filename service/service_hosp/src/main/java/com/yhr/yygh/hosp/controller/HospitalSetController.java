package com.yhr.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhr.yygh.commom.exception.YyghException;
import com.yhr.yygh.commom.result.Result;
import com.yhr.yygh.commom.result.ResultCodeEnum;
import com.yhr.yygh.common.utils.MD5;
import com.yhr.yygh.hosp.service.HospitalSetService;
import com.yhr.yygh.model.hosp.HospitalSet;
import com.yhr.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //查找所有医院信息
    @GetMapping("/findAll")
    public Result findAllHospSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //逻辑删除
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if (flag){
            return Result.ok(flag);
        }else {
            return Result.fail();
        }
    }

    //条件查询带分页
    @PostMapping("findPage/{current}/{limit}")
    public Result findPageHospSet(@PathVariable Long current,
                                  @PathVariable Long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        //创建page对象
        Page<HospitalSet> page = new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if (!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        if (!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);
        return Result.ok(pageHospitalSet);
    }

    //添加医院设置
    @PostMapping("saveHospSet")
    public Result saveHospSet(@RequestBody HospitalSet hospitalSet){
        //设置状态 1使用 0不能使用
        hospitalSet.setStatus(1);
        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        //调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if (save){
            return Result.ok();
        }else
            return Result.fail();
    }

    //根据id获取医院设置
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
        try {

        }catch (Exception e){
            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
        }
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    //修改医院设置
    @PostMapping("updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet){
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update){
            return Result.ok();
        }else
            return Result.fail();
    }

    //批量删除医院设置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospSet(@RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    //医院设置锁定和解锁，锁定不能对接接口
    @PutMapping("lockHospSet/{id}/{status}")
    public Result lockHospSet(@PathVariable Long id,
                              @PathVariable Integer status){
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    //发送签名密钥key
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        //TODO 发送短信
        return Result.ok();
    }
}
