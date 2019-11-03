package com.siyang.annotation;

import java.lang.annotation.*;

/**
 * @author study
 * @create 2019-11-01 19:52
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
}
