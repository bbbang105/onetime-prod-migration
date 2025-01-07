package side.onetime.dto.event.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetEventQrCodeResponse(
        String qrCodeImgUrl
) {
    public static GetEventQrCodeResponse from(String qrCodeImgUrl) {
        return new GetEventQrCodeResponse(
                qrCodeImgUrl
        );
    }
}
