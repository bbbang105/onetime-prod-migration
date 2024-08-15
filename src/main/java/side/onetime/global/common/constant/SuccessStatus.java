package side.onetime.global.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseCode;
import side.onetime.global.common.dto.ReasonDto;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    // Global
    _OK(HttpStatus.OK, "200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "201", "생성에 성공했습니다."),
    // Event
    _CREATED_EVENT(HttpStatus.CREATED, "201", "이벤트 생성에 성공했습니다."),
    _GET_EVENT(HttpStatus.OK, "200", "이벤트 조회에 성공했습니다."),
    // Member
    _LOGIN_MEMBER(HttpStatus.OK, "200", "멤버 로그인에 성공했습니다."),
    _IS_POSSIBLE_NAME(HttpStatus.OK, "200", "멤버 이름 중복 확인에 성공했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .isSuccess(true)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}