package com.siyang.annotation;

import java.lang.annotation.*;

/**
 * @author study
 * @create 2019-11-01 19:55
 */
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value();
}
