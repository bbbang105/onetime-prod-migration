package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum TokenErrorResult implements BaseErrorCode {
    _INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "401", "유효하지 않은 토큰입니다."),
    _INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "401", "유효하지 않은 리프레쉬 토큰입니다."),
    _NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "404", "리프레쉬 토큰을 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}