package side.onetime.dto.admin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageInfo(
        int page,
        int size,
        int total_elements,
        int total_pages
) {
    public static PageInfo of(int page, int size, int totalElements, int totalPages) {
        return new PageInfo(
                page,
                size,
                totalElements,
                totalPages
        );
    }
}