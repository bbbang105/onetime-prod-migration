package side.onetime.dto.admin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetAllBannersResponse(
        List<GetBannerResponse> banners,
        PageInfo pageInfo
) {
    public static GetAllBannersResponse of(List<GetBannerResponse> banners, PageInfo pageInfo) {
        return new GetAllBannersResponse(
                banners,
                pageInfo
        );
    }
}
