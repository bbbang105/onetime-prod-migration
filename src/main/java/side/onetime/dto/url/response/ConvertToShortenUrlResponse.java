package side.onetime.dto.url.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConvertToShortenUrlResponse(
        String shortenUrl
) {
    public static ConvertToShortenUrlResponse of(String shortenUrl) {
        return new ConvertToShortenUrlResponse(shortenUrl);
    }
}
