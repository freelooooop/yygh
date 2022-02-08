package com.yhr.yygh.hosp.controller;

import com.yhr.yygh.commom.result.Result;
import com.yhr.yygh.hosp.service.ScheduleService;
import com.yhr.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    //根据医院编号和科室编号查询排班
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Long page,
                                  @PathVariable Long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode){
        Map<String,Object> map = scheduleService.getScheduleRule(page,limit,hoscode,depcode);
        return Result.ok(map);
    }

    //根据医院编号，科室编号和工作日期，查询排班详细信息
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> list = scheduleService.getScheduleDetail(hoscode,depcode,workDate);
        return Result.ok(list);
    }
}
