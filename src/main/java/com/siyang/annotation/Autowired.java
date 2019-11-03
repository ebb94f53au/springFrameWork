package com.siyang.annotation;

import java.lang.annotation.*;

/**
 * @author study
 * @create 2019-11-01 19:46
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String value();
}
