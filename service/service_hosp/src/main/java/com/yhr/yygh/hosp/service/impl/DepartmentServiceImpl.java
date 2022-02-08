package com.yhr.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yhr.yygh.hosp.repository.DepartmentRepository;
import com.yhr.yygh.hosp.service.DepartmentService;
import com.yhr.yygh.model.hosp.Department;
import com.yhr.yygh.vo.hosp.DepartmentQueryVo;
import com.yhr.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    //上传科室接口
    @Override
    public void save(Map<String, Object> switchMap) {
        String mapString = JSONObject.toJSONString(switchMap);
        Department department = JSONObject.parseObject(mapString, Department.class);
        //根据医院编号和科室编号查询
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        if (departmentExist != null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageable对象，设置当前页和每页记录数,0是第一页
        Pageable pageable = PageRequest.of(page - 1,limit);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        Example<Department> example = Example.of(department,matcher);
        Page<Department> all = departmentRepository.findAll(example,pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null){
            departmentRepository.deleteById(department.getId());
        }
    }

    //根据医院编号，查询医院所有科室编号
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合用于数据封装
        ArrayList<DepartmentVo> result = new ArrayList<>();
        //根据医院编号，查询所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        //所有科室列表信息
        List<Department> departmentList = departmentRepository.findAll(example);
        //根据大科室分组，根据大科室编号bigcode,获取大科室下的下级子科室
        Map<String, List<Department>> map = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map
        for (Map.Entry<String, List<Department>> entry : map.entrySet()) {
            //大科室编号
            String key = entry.getKey();
            //大科室编号对应的全部数据
            List<Department> value = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(key);
            departmentVo.setDepname(value.get(0).getBigname());
            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department : value) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(department.getDepcode());
                departmentVo1.setDepname(department.getDepname());
                children.add(departmentVo1);
            }
            //把小科室list放到大科室children中
            departmentVo.setChildren(children);
            //放到最终的result中
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);
        if (department != null){
            return department.getDepname();
        }
        return null;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);
    }
}
