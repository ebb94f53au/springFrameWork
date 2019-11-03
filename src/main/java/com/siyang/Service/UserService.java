package com.siyang.Service;

import com.siyang.annotation.Autowired;
import com.siyang.annotation.Service;
import com.siyang.dao.UserDao;

/**
 * @author study
 * @create 2019-11-01 19:58
 */
@Service
public class UserService {
    @Autowired("userDao")
    UserDao userDao;

    public void insert (){
        userDao.insert();
    
    }
}
