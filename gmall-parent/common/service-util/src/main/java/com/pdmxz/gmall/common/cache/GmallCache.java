package com.pdmxz.gmall.common.cache;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) //此注解的使用范围  方法级别的注解   此注解打在方法上
@Retention(RetentionPolicy.RUNTIME) //此注解的使用状态 CLASS RUNTIME SOURCE
@Documented
public @interface GmallCache {


    String prefix() default "cache:";

}
