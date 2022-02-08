package com.yhr.yygh.hosp.controller;

import com.yhr.yygh.commom.result.Result;
import com.yhr.yygh.hosp.service.HospitalService;
import com.yhr.yygh.model.hosp.Hospital;
import com.yhr.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    //医院列表
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel = hospitalService.selectHospSet(page,limit,hospitalQueryVo);
        return Result.ok(pageModel);
    }

    //更新医院上线状态
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,
                                   @PathVariable Integer status){
        hospitalService.updateHospStatus(id,status);
        return Result.ok();
    }

    //医院详情信息
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        Map<String,Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }
}
