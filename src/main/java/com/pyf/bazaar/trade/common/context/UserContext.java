package com.pyf.bazaar.trade.common.context;

/**
 * 用户上下文持有者（基于 ThreadLocal）
 * 用途：在当前请求线程中保存登录用户 ID，避免在 Controller -> Service 层之间反复传递 userId 参数
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void clear() {
        USER_ID_HOLDER.remove();
    }
}
