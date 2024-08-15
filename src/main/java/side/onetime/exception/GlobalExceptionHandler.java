package side.onetime.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.code.BaseErrorCode;

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
}