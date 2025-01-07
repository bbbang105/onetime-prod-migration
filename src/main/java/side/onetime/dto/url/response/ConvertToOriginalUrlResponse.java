package side.onetime.dto.url.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConvertToOriginalUrlResponse(
        String originalUrl
) {
    public static ConvertToOriginalUrlResponse of(String originalUrl) {
        return new ConvertToOriginalUrlResponse(originalUrl);
    }
}
