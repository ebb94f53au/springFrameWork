package com;

import com.siyang.DispatchServlet;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author study
 * @create 2019-11-03 11:45
 */
public class Test1 {

    @Test
    public void demo1 () throws ClassNotFoundException {
        DispatchServlet dispatchServlet =new DispatchServlet();
        dispatchServlet.scanBasePackage("com.siyang");
        List<String> packagesName = dispatchServlet.getPackagesName();
        dispatchServlet.handlerUrlMethod();
        for (Map.Entry<String, Method> stringObjectEntry : dispatchServlet.getUrlMethodMap().entrySet())
            System.out.println(stringObjectEntry);



    }
}
