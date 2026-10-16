package com.citypulse.common.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException() {
        super("Email đã được sử dụng. Vui lòng chọn email khác.");
    }
}
