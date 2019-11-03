package com.siyang.Controller;

import com.siyang.Service.UserService;
import com.siyang.annotation.Autowired;
import com.siyang.annotation.Controller;
import com.siyang.annotation.RequestMapping;

/**
 * @author study
 * @create 2019-11-01 20:00
 */
@Controller
public class UserController {
    @Autowired("userService")
    UserService userService;

    @RequestMapping(value = "/insert")
    public void insert(){
        userService.insert();
    }

    @RequestMapping(value = "/insert2")
    public void insert2(){
        userService.insert();
    }
}
