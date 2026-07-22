package com.pyf.bazaar.trade.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pyf.bazaar.trade.common.enums.BizCodeEnum;
import com.pyf.bazaar.trade.common.exception.BizException;
import com.pyf.bazaar.trade.common.security.JwtTokenUtil;
import com.pyf.bazaar.trade.user.dto.UserLoginDTO;
import com.pyf.bazaar.trade.user.dto.UserRegisterDTO;
import com.pyf.bazaar.trade.user.entity.User;
import com.pyf.bazaar.trade.user.mapper.UserMapper;
import com.pyf.bazaar.trade.user.service.IUserService;
import com.pyf.bazaar.trade.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor  // 构造器注入，推荐方式
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder; // Spring Security提供
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public UserVO register(UserRegisterDTO dto) {
        // 第一步：查热数据，用户是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, dto.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BizException(BizCodeEnum.USER_PHONE_EXISTS);
        }

        // 第二步：查冷数据，用户是否已注销
        User deletedUser = userMapper.selectDeletedByPhone(dto.getPhone());
        if (deletedUser != null) {
            throw new BizException(BizCodeEnum.USER_ACCOUNT_DELETED);
        }
        // 第三步：新用户注册...
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // BCrypt加密
        user.setNickname("用户_" + System.currentTimeMillis()); // 默认昵称
        userMapper.insert(user);

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public String login(UserLoginDTO dto) {
        // 1. 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, dto.getPhone());
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException(BizCodeEnum.USER_LOGIN_FAIL);
        }

        // 2. 生成JWT Token
        return jwtTokenUtil.generateToken(user.getId().toString());
    }

    @Transactional
    public void restoreAccount(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("手机号未注册");
        }
        if (user.getDeleted() == 0) {
            throw new RuntimeException("账号状态正常，无需恢复");
        }
        user.setDeleted(0);
        userMapper.updateById(user);
    }
}
