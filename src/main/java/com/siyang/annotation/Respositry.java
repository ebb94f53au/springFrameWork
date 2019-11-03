package com.siyang.annotation;

import java.lang.annotation.*;

/**
 * @author study
 * @create 2019-11-01 19:53
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Respositry {
    String value() default "";
}
