package com.zxp.nowcodercommunity.mapper;

import com.zxp.nowcodercommunity.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getUserById(int id);
}
