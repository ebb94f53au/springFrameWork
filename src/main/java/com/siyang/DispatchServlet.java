package com.siyang;

import com.siyang.Service.UserService;
import com.siyang.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author study
 * @create 2019-11-01 20:03
 */
@WebServlet(urlPatterns = "/*",//匹配所有访问路径
        loadOnStartup = 0,//是否启动时加载 默认-1：否 0：开启
        initParams = {@WebInitParam(name="base-package",value = "com.siyang")})//设置默认参数
public class DispatchServlet extends HttpServlet {
    private static final String EMPTY = "";

    /** 扫描的基础包*/
    private String basePackage = EMPTY;
    /**
     * 扫描后的包名
     */
    private List<String> packagesName = new ArrayList<String>();
    /**
     * 实例名称-实例 Map集合
     */
    private Map<String, Object> instanceMap = new HashMap<String, Object>();
    /**
     * 类路径地址-实例名称 Map集合
     */
    private Map<String, String> nameMap = new HashMap<>();
    /**
     * URL接口-controller映射方法 Map集合
     */
    private Map<String, Method> urlMethodMap = new HashMap<>();
    /**
     * controller映射方法-类路径地址 Map集合
     */
    private Map<Method, String> methodPackageMap = new HashMap<>();

    /**
     * 容器启动时调用
     * 需要完成的功能：
     *  1.扫描包获取所有类的地址
     *  2.实例化对象
     *  3.ioc容器autowired
     *  4.配置映射map
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        basePackage = config.getInitParameter("base-package");
        try {
            //  1.扫描包获取所有类的地址
            scanBasePackage(basePackage);
            //  2.实例化对象
            instance(packagesName);
            //  3.ioc容器autowired
            ioc();
            //  4.配置映射map
            handlerUrlMethod();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("框架加载失败");
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * 获得请求路径，通过反射机制调用对应的controller类的对应方法
     * 需要controller的实例 和 方法本身
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI().replace(contextPath,EMPTY);
        Optional<Method> method = Optional.ofNullable(urlMethodMap.get(requestURI));
        method.ifPresent(m->{
            String pName = methodPackageMap.get(m);
            String obName = nameMap.get(pName);
            Object o = instanceMap.get(obName);
            m.setAccessible(true);
            try {
                m.invoke(o);
            }catch (Exception e){
                e.printStackTrace();

            }

        });



    }



    /**
     * 扫描路径下的所有文件，获取所有的类路径
     * @param basePackage
     */
    public void  scanBasePackage(String basePackage){
        //1.根据类路径扫描下面的文件
        URL url = this.getClass().getClassLoader().getResource(basePackage.replace(".","/"));
        Optional<URL> op1 = Optional.ofNullable(url);
            //引用url中的方法，URL为空则为null
        String path = op1.map(URL::getPath).orElse("");
        File file = new File(path);
            //listFiles,返回的是File型的数组，后面可以判断是否为文件夹
        Optional<File[]> fileArr = Optional.ofNullable(file.listFiles());
            //如果不为空则遍历
        fileArr.ifPresent(files ->{
            for (File f : files) {
                if(f.isDirectory()){
                    //2.如果是文件夹则递归调用
                    scanBasePackage(basePackage+"."+f.getName());
               }else{
                    //3.如果是文件则将类路径地址加入packagesName
//                    System.out.println(f.getName().split("\\.")[1].equals("class"));
                    packagesName.add(basePackage+"."+f.getName().split("\\.")[0]);
                }
            }
        });




    }

    /**
     *  根据注解实例化对象
     *  可以尝试递归@Component
     * @param packagesNames
     */
    public void instance (List<String> packagesNames) {
        if(packagesNames.isEmpty()){
            return;
        }
        //1.遍历packagesNames
        packagesNames.forEach(pName->{
            //获得实例的开头小写的名字
            String firstN=pName.substring(pName.lastIndexOf(".")+1,pName.lastIndexOf(".")+2).toLowerCase();
            String obName = firstN+pName.substring(pName.lastIndexOf(".")+2);
            try {
                Class<?> clazz = Class.forName(pName);
                //2.判断类上是后带有注解
                if(clazz.isAnnotationPresent(Respositry.class)){
                    //如果为Respositry注解类
                    String value = clazz.getAnnotation(Respositry.class).value();
                    if(value.equals(EMPTY)){
                        //如果自定义名字为空，默认使用类名小写
                        value=obName;
                    }
                    instanceMap.put(value,clazz.newInstance());
                    nameMap.put(pName,value);

                }else if(clazz.isAnnotationPresent(Service.class)){
                    //如果为Service注解类
                    String value = clazz.getAnnotation(Service.class).value();
                    if(value.equals(EMPTY)){
                        //如果自定义名字为空，默认使用类名小写
                        value=obName;
                    }
                    instanceMap.put(value,clazz.newInstance());
                    nameMap.put(pName,value);

                }else if(clazz.isAnnotationPresent(Controller.class)){
                    //如果为Controller注解类
                    String value = clazz.getAnnotation(Controller.class).value();
                    if(value.equals(EMPTY)){
                        //如果自定义名字为空，默认使用类名小写
                        value=obName;
                    }
                    instanceMap.put(value,clazz.newInstance());
                    nameMap.put(pName,value);
                }
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println("类加载失败");
            }


        });
        //3.如果有加入instanceMap、nameMap

    }

    /**
     *  遍历instanceMap的实例化对象
     *  查找是否有 属性标有Autowired
     *  自动注入
     *
     */
    private void ioc () throws IllegalAccessException {
        //1.遍历instanceMap
        for (Map.Entry<String, Object> ob : instanceMap.entrySet()) {
            Field[] declaredFields = ob.getValue().getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                //2.判断实例类中的属性是否有AutoWired
                if(declaredField.isAnnotationPresent(Autowired.class)){
                    String value = declaredField.getAnnotation(Autowired.class).value();
                    declaredField.setAccessible(true);
                    if(value.equals(EMPTY)){
                        value=declaredField.getName();
                    }
                    //3.有就使用set方法进行注入   参数： 类实例，注入类实例
                    declaredField.set(ob.getValue(),instanceMap.get(value));

                }

            }

        }


    }

    /**
     * 遍历标有controller类的标有RequestMapping的方法进行接口与方法体的映射
     */
    public void handlerUrlMethod() {
        if(packagesName.isEmpty()){
            return;
        }
        //1.遍历packagesName的类名
        packagesName.forEach(pName->{
            try {
                Class<?> clazz = Class.forName(pName);
                //2.反射生成后过滤出标有controller的类
                if(clazz.isAnnotationPresent(Controller.class)){
                    String root= "";
                    //3.判断类上是否有RequestMapping
                    if(clazz.isAnnotationPresent(RequestMapping.class)){
                        //如果类上有requestMapping，将类url加入StringBuffer
                        root=clazz.getAnnotation(RequestMapping.class).value();

                    }
                    Method[] methods = clazz.getDeclaredMethods();
                    //4.判断方法体上是否有RequestMapping
                    for (Method method : methods) {
                        if(method.isAnnotationPresent(RequestMapping.class)){
                            String url = root +method.getAnnotation(RequestMapping.class).value();
                            //将url接口拼接后的结果 与 方法体存入
                            urlMethodMap.put(url.toString(),method);
                            //将方法体 与 类路径存入
                            methodPackageMap.put(method,pName);
                        }

                    }



                }

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("获取映射失败");
            }

        });



    }

    public Map<String, Object> getInstanceMap() {
        return instanceMap;
    }

    public Map<String, String> getNameMap() {
        return nameMap;
    }

    public List<String> getPackagesName() {
        return packagesName;
    }

    public Map<String, Method> getUrlMethodMap() {
        return urlMethodMap;
    }

    public Map<Method, String> getMethodPackageMap() {
        return methodPackageMap;
    }
}
