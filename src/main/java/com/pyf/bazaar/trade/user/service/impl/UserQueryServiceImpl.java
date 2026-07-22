package com.pyf.bazaar.trade.user.service.impl;

import com.pyf.bazaar.trade.user.entity.User;
import com.pyf.bazaar.trade.user.mapper.UserMapper;
import com.pyf.bazaar.trade.user.service.IUserQueryService;
import com.pyf.bazaar.trade.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements IUserQueryService {

    private final UserMapper userMapper;

    @Override
    // @Cacheable(value = "user", key = "#userId") // 后续配合Caffeine开启
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
