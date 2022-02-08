package com.yhr.yygh.hosp.service;

import com.yhr.yygh.model.hosp.Hospital;
import com.yhr.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> switchMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospSet(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateHospStatus(String id, Integer status);

    Map<String,Object> getHospById(String id);

    String getHospName(String hoscode);

    //根据医院名称查询
    List<Hospital> findByHosname(String hosname);

    Map<String, Object> item(String hoscode);
}
