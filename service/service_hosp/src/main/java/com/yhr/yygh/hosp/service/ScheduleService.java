package com.yhr.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhr.yygh.model.hosp.HospitalSet;
import com.yhr.yygh.model.hosp.Schedule;
import com.yhr.yygh.vo.hosp.ScheduleOrderVo;
import com.yhr.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService extends IService<Schedule> {
    void save(Map<String, Object> switchMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode);

    //根据医院编号，科室编号和工作日期，查询排班详细信息
    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);

    Map<String,Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getScheduleById(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班数据,用户mq
    void update(Schedule schedule);
}
