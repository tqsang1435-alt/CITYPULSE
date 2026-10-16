package com.citypulse.common.exception;

import com.citypulse.common.response.ApiMessage;
import com.citypulse.trip.service.TripEmailException;
import com.citypulse.user.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Chuyển đổi exceptions thành HTTP response nhất quán cho toàn bộ API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Trip không tồn tại, user không tồn tại → 404 */
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiMessage> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiMessage(ex.getMessage()));
    }

    /** Không phải owner, không đủ quyền → 403 */
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiMessage> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiMessage(ex.getMessage()));
    }

    /** Duplicate pending invitation → 409 */
    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<ApiMessage> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiMessage(ex.getMessage()));
    }

    @ExceptionHandler({AuthService.InvalidCredentialsException.class, BadCredentialsException.class})
    ResponseEntity<ApiMessage> handleInvalidCredentials(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiMessage("Email hoặc mật khẩu không chính xác."));
    }

    /** Gửi email thất bại → 502 Bad Gateway (external service lỗi) */
    @ExceptionHandler(TripEmailException.class)
    ResponseEntity<ApiMessage> handleEmailFailure(TripEmailException ex) {
        log.warn("Email delivery failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiMessage(ex.getMessage()));
    }

    /** @Valid validation lỗi (email không hợp lệ, v.v.) → 400 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiMessage> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiMessage(detail));
    }

    /** Lỗi không dự kiến → 500, ghi log để debug */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiMessage> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiMessage("Đã xảy ra lỗi hệ thống, vui lòng thử lại sau."));
    }
}
