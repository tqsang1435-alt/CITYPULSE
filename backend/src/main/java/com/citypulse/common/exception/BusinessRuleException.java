package com.citypulse.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ném ra khi yêu cầu vi phạm quy tắc nghiệp vụ.
 * Ví dụ: lời mời đã được gửi trước đó (duplicate pending invitation).
 * Ánh xạ tự động sang HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
