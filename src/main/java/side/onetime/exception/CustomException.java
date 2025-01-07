package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final BaseErrorCode errorCode;

    @Override
    public String getMessage() {
        return errorCode.getReasonHttpStatus().getMessage();
    }

    public String getCode() {
        return errorCode.getReasonHttpStatus().getCode();
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getReasonHttpStatus().getHttpStatus();
    }
}
