package com.pyf.bazaar.trade.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyf.bazaar.trade.common.web.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
/*
* 处理已登录但权限不足的场景
* （为未来 @PreAuthorize 预留）*/
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(@NonNull HttpServletRequest request,
                       HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(403, "权限不足，无法访问该资源");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
