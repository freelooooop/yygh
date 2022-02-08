package com.yhr.yygh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhr.yygh.model.order.OrderInfo;
import com.yhr.yygh.vo.order.OrderCountQueryVo;
import com.yhr.yygh.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends BaseMapper<OrderInfo> {
    //查询预约统计数据
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}