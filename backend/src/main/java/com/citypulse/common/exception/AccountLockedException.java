package com.citypulse.common.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }

    public AccountLockedException() {
        super("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
    }
}
