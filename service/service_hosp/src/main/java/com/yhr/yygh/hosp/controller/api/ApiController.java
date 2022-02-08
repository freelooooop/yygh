package com.yhr.yygh.hosp.controller.api;

import com.yhr.yygh.commom.exception.YyghException;
import com.yhr.yygh.commom.result.Result;
import com.yhr.yygh.commom.result.ResultCodeEnum;
import com.yhr.yygh.common.helper.HttpRequestHelper;
import com.yhr.yygh.common.utils.MD5;
import com.yhr.yygh.hosp.service.DepartmentService;
import com.yhr.yygh.hosp.service.HospitalService;
import com.yhr.yygh.hosp.service.HospitalSetService;
import com.yhr.yygh.hosp.service.ScheduleService;
import com.yhr.yygh.model.hosp.Department;
import com.yhr.yygh.model.hosp.Hospital;
import com.yhr.yygh.model.hosp.Schedule;
import com.yhr.yygh.vo.hosp.DepartmentQueryVo;
import com.yhr.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //上传科室接口
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取传递过来的科室信息
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);
        //获取科室编号
        String hoscode = (String) switchMap.get("hoscode");
        //1.获取科室系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的科室编号，查询数据库，看签名是否相同
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.save(switchMap);
        return Result.ok();
    }

    //展示科室接口
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取传递过来的科室信息
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);

        //医院编号
        String hoscode = (String) switchMap.get("hoscode");
        int page = StringUtils.isEmpty(switchMap.get("page")) ? 1 : Integer.parseInt((String)switchMap.get("page"));
        int limit = StringUtils.isEmpty(switchMap.get("limit")) ? 1 : Integer.parseInt((String)switchMap.get("limit"));

        //1.获取医院系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的医院编号，查询数据库，看签名是否相同
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
    }

    //删除科室接口
    @PostMapping("department/remove")
    public Result removeDep(HttpServletRequest request){
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);
        //获取医院编号
        String hoscode = (String) switchMap.get("hoscode");
        String depcode = (String) switchMap.get("depcode");

        //1.获取医院系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的医院编号，查询数据库，看签名是否相同
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }

    //展示医院接口
    @PostMapping("hospital/show")
    public Result getHosp(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);
        //获取医院编号
        String hoscode = (String) switchMap.get("hoscode");
        //1.获取医院系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的医院编号，查询数据库，看签名是否相同
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service方法实现查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    //上传医院接口
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);

        //1.获取医院系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的医院编号，查询数据库，看签名是否相同
        String hoscode = (String) switchMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //传输过程中+变成了空格，要转回来
        String logoData = (String) switchMap.get("logoData");
        logoData = logoData.replaceAll(" ","+");
        switchMap.put("logoData",logoData);

        //调用service的方法
        hospitalService.save(switchMap);
        return Result.ok();
    }

    //上传排班接口
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);

        //1.获取医院系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的医院编号，查询数据库，看签名是否相同
        String hoscode = (String) switchMap.get("hoscode");
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.save(switchMap);
            return Result.ok();
    }

    //查询排班接口
    @PostMapping("schedule/list")
    public Result findSschedule(HttpServletRequest request){
        //获取传递过来的科室信息
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);

        //医院编号
        String hoscode = (String) switchMap.get("hoscode");
        int page = StringUtils.isEmpty(switchMap.get("page")) ? 1 : Integer.parseInt((String)switchMap.get("page"));
        int limit = StringUtils.isEmpty(switchMap.get("limit")) ? 1 : Integer.parseInt((String)switchMap.get("limit"));
        //科室编号
        String depcode = (String) switchMap.get("depcode");

        //1.获取医院系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的医院编号，查询数据库，看签名是否相同
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);

        Page<Schedule> pageModel = scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }

    //删除排班接口
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);
        //获取医院编号
        String hoscode = (String) switchMap.get("hoscode");
        String hosScheduleId = (String) switchMap.get("hosScheduleId");

        //1.获取医院系统传递过来的签名
        String hospSign = (String) switchMap.get("sign");
        //2.根据传递过来的医院编号，查询数据库，看签名是否相同
        String signKey = hospitalSetService.getSignKey(hoscode);
        //3.把查出来的数据进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }

}
