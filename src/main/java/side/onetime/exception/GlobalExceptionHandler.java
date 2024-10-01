package side.onetime.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.constant.ErrorStatus;
import side.onetime.global.common.dto.ErrorReasonDto;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Event
    @ExceptionHandler(EventException.class)
    public ResponseEntity<ApiResponse<BaseErrorCode>> handleTokenException(EventException e) {
        EventErrorResult errorResult = e.getEventErrorResult();
        return ApiResponse.onFailure(errorResult);
    }

    // Member
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiResponse<BaseErrorCode>> handleMemberException(MemberException e) {
        MemberErrorResult errorResult = e.getMemberErrorResult();
        return ApiResponse.onFailure(errorResult);
    }

    // Schedule
    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<ApiResponse<BaseErrorCode>> handleScheduleException(ScheduleException e) {
        ScheduleErrorResult errorResult = e.getScheduleErrorResult();
        return ApiResponse.onFailure(errorResult);
    }

    // Selection
    @ExceptionHandler(SelectionException.class)
    public ResponseEntity<ApiResponse<BaseErrorCode>> handleSelectionException(SelectionException e) {
        SelectionErrorResult errorResult = e.getSelectionErrorResult();
        return ApiResponse.onFailure(errorResult);
    }

    // User
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<BaseErrorCode>> handleUserException(UserException e) {
        UserErrorResult errorResult = e.getUserErrorResult();
        return ApiResponse.onFailure(errorResult);
    }

    // Token
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ApiResponse<BaseErrorCode>> handleTokenException(TokenException e) {
        TokenErrorResult errorResult = e.getTokenErrorResult();
        return ApiResponse.onFailure(errorResult);
    }

    // AccessDeniedException 등 보안 관련 에러 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorReasonDto> handleSecurityException(SecurityException e) {
        log.error("SecurityException: {}", e.getMessage());
        return ResponseEntity.status(ErrorStatus._UNAUTHORIZED.getHttpStatus())
                .body(ErrorStatus._UNAUTHORIZED.getReasonHttpStatus());
    }

    // 기타 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorReasonDto> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage());

        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus())
                    .body(ErrorStatus._BAD_REQUEST.getReasonHttpStatus());
        }

        // 그 외 내부 서버 오류로 처리
        return ResponseEntity.status(ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ErrorStatus._INTERNAL_SERVER_ERROR.getReasonHttpStatus());
    }
}