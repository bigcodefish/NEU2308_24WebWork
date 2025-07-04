package com.example.webcarproject.mapper;

import com.example.webcarproject.entity.Auth;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AuthMapper {
    @Select("SELECT * FROM sysuser WHERE username = #{username}")
    Auth findByUsername(String username);

    @Insert("INSERT INTO sysuser(username, password, salt) " +
            "VALUES(#{username}, #{password}, #{salt})")
    int insert(Auth auth);

    @Select("SELECT COUNT(*) FROM sysuser WHERE username = #{username}")
    int countByUsername(String username);
}
