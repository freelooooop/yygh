package com.yhr.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yhr.yygh.cmn.client.DictFeignClient;
import com.yhr.yygh.hosp.repository.HospitalRepository;
import com.yhr.yygh.hosp.service.HospitalService;
import com.yhr.yygh.model.hosp.Hospital;
import com.yhr.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> switchMap) {
        //map转换成对象Hospital
        String mapString = JSONObject.toJSONString(switchMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //查询是否有数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        if (hospitalExist != null){
            hospitalExist.setUpdateTime(new Date());
            hospitalExist.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    //医院列表(条件查询分页)
    @Override
    public Page<Hospital> selectHospSet(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建Pageable
        Pageable pageable = PageRequest.of(page - 1,limit);
        ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example = Example.of(hospital,matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        //获取list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach(item -> {
            setHospitalHosType(item);
        });
        return pages;
    }

    //更新医院上线状态
    @Override
    public void updateHospStatus(String id, Integer status) {
        //根据id查询
        Hospital hospital = hospitalRepository.findById(id).get();
        //更新
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    //获取医院详细信息
    @Override
    public Map<String,Object> getHospById(String id) {
        HashMap<String, Object> result = new HashMap<>();
        Hospital hospital = setHospitalHosType(hospitalRepository.findById(id).get());
        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null){
            return hospital.getHosname();
        }
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        HashMap<String, Object> result = new HashMap<>();
        Hospital hospital = setHospitalHosType(getByHoscode(hoscode));
        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());
        hospital.setBookingRule(null);
        return result;
    }

    //获取list集合，遍历进行医院等级封装
    private Hospital setHospitalHosType(Hospital hospital) {
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省市地区
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());
        hospital.getParam().put("fullAddr",provinceString + cityString + districtString);
        hospital.getParam().put("hostypeString",hostypeString);
        return hospital;
    }
}
