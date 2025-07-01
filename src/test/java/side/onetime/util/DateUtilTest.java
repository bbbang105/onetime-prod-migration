package side.onetime.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import side.onetime.domain.enums.Category;
import side.onetime.dto.event.response.GetMostPossibleTime;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilTest {

    @Test
    @DisplayName("날짜 기준으로 정렬된다. - 참여자수 내림차순, 날짜 최신순")
    void sortMostPossibleTimesByDate() {
        // given
        List<GetMostPossibleTime> input = List.of(
                createMostPossibleTime("2025.05.21", 2),
                createMostPossibleTime("2025.02.22", 3),
                createMostPossibleTime("2025.02.22", 5),
                createMostPossibleTime("2025.04.05", 5)
        );

        // when
        List<GetMostPossibleTime> result = DateUtil.sortMostPossibleTimes(input, Category.DATE);

        List<GetMostPossibleTime> expected = List.of(
                createMostPossibleTime("2025.04.05", 5),
                createMostPossibleTime("2025.02.22", 5),
                createMostPossibleTime("2025.02.22", 3),
                createMostPossibleTime("2025.05.21", 2)
        );

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("요일 기준으로 정렬된다. - 참여자수 내림차순, 요일순 (일~토)")
    void sortMostPossibleTimesByDay() {
        // given
        List<GetMostPossibleTime> input = List.of(
                createMostPossibleTime("월", 1),
                createMostPossibleTime("금", 3),
                createMostPossibleTime("수", 3),
                createMostPossibleTime("일", 5)
        );

        // when
        List<GetMostPossibleTime> result = DateUtil.sortMostPossibleTimes(input, Category.DAY);

        List<GetMostPossibleTime> expected = List.of(
                createMostPossibleTime("일", 5),
                createMostPossibleTime("수", 3),
                createMostPossibleTime("금", 3),
                createMostPossibleTime("월", 1)
        );

        // then
        assertThat(result).isEqualTo(expected);
    }

    private GetMostPossibleTime createMostPossibleTime(String timePoint, int possibleCount) {
        return new GetMostPossibleTime(
                timePoint,
                "10:00", "10:30",
                possibleCount,
                List.of("User1", "User2"),
                List.of("User3")
        );
    }
}
