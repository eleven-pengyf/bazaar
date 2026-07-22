package com.pyf.bazaar.trade.user.service;

import com.pyf.bazaar.trade.user.vo.UserVO;

public interface IUserQueryService {
    UserVO getUserInfo(Long userId);
}
