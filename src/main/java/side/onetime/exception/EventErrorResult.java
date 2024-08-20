package side.onetime.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum EventErrorResult implements BaseErrorCode {
    _NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, "404", "이벤트를 찾을 수 없습니다."),
    _IS_NOT_DATE_FORMAT(HttpStatus.NOT_FOUND, "404", "날짜 이벤트에 요일을 입력할 수 없습니다."),
    _IS_NOT_DAY_FORMAT(HttpStatus.NOT_FOUND, "404", "요일 이벤트에 날짜를 입력할 수 없습니다.")
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