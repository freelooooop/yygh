package com.yhr.yygh.config;

import org.joda.time.DateTime;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Date;

@MapperScan("com.yhr.yygh.mapper")
@Configuration
public class OrderConfig {
}
