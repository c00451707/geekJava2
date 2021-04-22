package io.hust.pony.demoweb.service;

//import mybatics.cache.entity.User;
//import mybatics.cache.mapper.UserMapper;
import io.hust.pony.demoweb.entity.User;
import io.hust.pony.demoweb.jdbcdemo.JdbcDemo;
import io.hust.pony.demoweb.mapper.UserMapper;
import io.hust.pony.demoweb.meta.ResultBean;
import io.hust.pony.demoweb.meta.UserMetaSpec;
import io.hust.pony.demoweb.meta.repository.UserSpecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserSpecRepository userSpecRepository;
    @Autowired
    JdbcDemo jdbcDemo;

    public ResultBean insertUser(UserMetaSpec user) {
//        userSpecRepository.save(user);
//        jdbcDemo.addUser(user);
        jdbcDemo.addUserV2(user);
        return new ResultBean("0","insert success!");

    }

    public ResultBean updateUser(UserMetaSpec user) {
//        userSpecRepository.save(user);
//        jdbcDemo.addUser(user);
        jdbcDemo.updateUserV2(user);
        return new ResultBean("0","update success!");

    }


    public UserMetaSpec findById(int id) {
        List<UserMetaSpec> userMetaOptional = userSpecRepository.findById(id);
//        UserMetaSpec userMetaSpec = userMetaOptional.get(0);
//        int age = userMetaSpec.getAge();
//        System.out.println(age);
        return userMetaOptional.get(0);
    }

    public List<UserMetaSpec> findAllUser() {
//        userSpecRepository.findAll()
        return jdbcDemo.getUserList();
    }

//    @Autowired
//    UserMapper userMapper;
////
////    // 开启spring cache
////    @Cacheable(key="#id",value="userCache")
//    public User find(int id) {
//        System.out.println(" ==> find " + id);
//        return userMapper.find(id);
//    }
////
////    // 开启spring cache
////    @Cacheable //(key="methodName",value="userCache")
//    public List<User> list(){
//        return userMapper.list();
//    }

}
