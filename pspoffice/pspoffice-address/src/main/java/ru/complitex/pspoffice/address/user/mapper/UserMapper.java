package ru.complitex.pspoffice.address.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.cdi.Mapper;

import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
@Mapper
public interface UserMapper {
    @Select("SELECT count(*) > 0 FROM user_view WHERE username = #{username} and password = #{password}")
    Boolean authenticate(@Param("username") String username, @Param("password") String password);

    @Select("SELECT role_name FROM role_view WHERE username = #{username}")
     List<String> roles(@Param("username") String username);
}
