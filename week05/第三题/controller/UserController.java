package io.hust.pony.demoweb.controller;

import io.hust.pony.demoweb.entity.User;
import io.hust.pony.demoweb.meta.ResultBean;
import io.hust.pony.demoweb.meta.UserMetaSpec;
import io.hust.pony.demoweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/user/insert", method = RequestMethod.POST)
    ResultBean insert(@RequestBody UserMetaSpec user) {
//        return userService.find(id);
        return userService.insertUser(user);
    }

    @RequestMapping(value = "/user/update", method = RequestMethod.PUT)
    ResultBean update(@RequestBody UserMetaSpec user) {
//        return userService.find(id);
        return userService.updateUser(user);
    }

    @RequestMapping(value = "/user/find/{id}", method = RequestMethod.GET)
    UserMetaSpec find(@PathVariable("id") Integer id) {
//        return userService.find(id);
        return userService.findById(id);
    }

    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    List<UserMetaSpec> list() {
//        return userService.list();
        return userService.findAllUser();
    }
}
