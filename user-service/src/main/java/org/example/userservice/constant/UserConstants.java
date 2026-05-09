package org.example.userservice.constant;

public final class UserConstants {

    public static final int USER_STATUS_DISABLED = 0;

    public static final int USER_STATUS_ENABLED = 1;

    public static final String TOKEN_TYPE_BEARER = "Bearer";

    private UserConstants() {
    }

    public static Integer resolveStatusOrDefault(Integer status) {
        return status == null ? USER_STATUS_ENABLED : status;
    }

    public static boolean isDisabledStatus(Integer status) {
        return Integer.valueOf(USER_STATUS_DISABLED).equals(status);
    }
}
