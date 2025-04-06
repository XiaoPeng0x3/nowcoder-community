package com.zxp.nowcodercommunity.mapper;

import com.zxp.nowcodercommunity.annotation.AutoCreateTime;
import com.zxp.nowcodercommunity.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    User getUserById(int id);

    @Select("select * from user where username = #{username}")
    User selectUserByUsername(String username);

    @Select("select * from user where email = #{email}")
    User selectUserByEmail(String email);

    @AutoCreateTime
    void insertUser(User user);

    Integer updateUserHeaderUrl(@Param("userId") Integer userId,
                                @Param("headerUrl") String headerUrl);

}
