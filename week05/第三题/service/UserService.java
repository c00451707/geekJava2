package io.hust.pony.demoweb.service;

import io.hust.pony.demoweb.entity.User;
import io.hust.pony.demoweb.meta.ResultBean;
import io.hust.pony.demoweb.meta.UserMetaSpec;
import org.springframework.cache.annotation.CacheConfig;

import java.util.List;

@CacheConfig(cacheNames = "users")
public interface UserService {

    ResultBean insertUser(UserMetaSpec user);

    UserMetaSpec findById(int id);

    List<UserMetaSpec> findAllUser();

}
