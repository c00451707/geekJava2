package io.hust.pony.demoweb.mapper;

import io.hust.pony.demoweb.entity.User;
//import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//@Mapper
public interface UserMapper {

    User find(int id);

    List<User> list();

}
