package com.yhr.yygh.hosp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhr.yygh.model.hosp.HospitalSet;
import com.yhr.yygh.model.hosp.Schedule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {
}
