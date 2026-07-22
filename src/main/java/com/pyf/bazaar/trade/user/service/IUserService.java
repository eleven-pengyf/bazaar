package com.pyf.bazaar.trade.user.service;

import com.pyf.bazaar.trade.user.dto.UserLoginDTO;
import com.pyf.bazaar.trade.user.dto.UserRegisterDTO;
import com.pyf.bazaar.trade.user.vo.UserVO;

public interface IUserService {
    UserVO register(UserRegisterDTO dto);
    String login(UserLoginDTO dto);  // 登录成功返回JWT Token

    void restoreAccount(String phone);
}
