package com.pyf.bazaar.trade.user.controller;

import com.pyf.bazaar.trade.common.context.UserContext;
import com.pyf.bazaar.trade.common.web.Result;
import com.pyf.bazaar.trade.user.dto.UserLoginDTO;
import com.pyf.bazaar.trade.user.dto.UserRegisterDTO;
import com.pyf.bazaar.trade.user.service.IUserQueryService;
import com.pyf.bazaar.trade.user.service.IUserService;
import com.pyf.bazaar.trade.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userCommandService;
    private final IUserQueryService userQueryService;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return Result.success(userCommandService.register(dto));
    }

    @GetMapping("/info")
    public Result<UserVO> getCurrentUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        UserVO userVO = userQueryService.getUserInfo(userId);
        return Result.success(userVO);
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userCommandService.login(dto));
    }

    @PostMapping("/restore")
    public Result<Void> restoreAccount(@RequestParam String phone) {
        userCommandService.restoreAccount(phone);
        return Result.success("账号恢复成功，请重新登录");
    }

}