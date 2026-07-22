package com.pyf.bazaar.trade.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pyf.bazaar.trade.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 查询已注销账户
    @Select("SELECT * FROM user WHERE phone = #{phone} AND deleted = 1")
    User selectDeletedByPhone(@Param("phone") String phone);
}
