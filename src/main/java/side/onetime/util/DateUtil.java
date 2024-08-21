package side.onetime.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DateUtil {

    // 30분 단위 타임 셋을 만드는 메서드
    public List<LocalTime> createTimeSets(String start, String end) {
        List<LocalTime> timeSets = new ArrayList<>();

        // "24:00"을 처리하기 위해 endTime을 "23:59"로 변환
        boolean isEndTimeMidnight = end.equals("24:00");
        if (isEndTimeMidnight) {
            end = "23:59";
        }

        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        LocalTime currentTime = startTime;

        // 30분 단위로 시간을 추가
        while (!currentTime.isAfter(endTime.minusMinutes(30))) {
            timeSets.add(currentTime);
            currentTime = currentTime.plusMinutes(30);
        }

        // endTime이 "24:00"인 경우 마지막 "23:30" 추가
        if (isEndTimeMidnight) {
            timeSets.add(LocalTime.of(23, 30));
        }

        return timeSets;
    }

    // 날짜를 정렬된 문자열 리스트로 변환하는 메서드
    public List<String> getSortedDateRanges(List<String> dateStrings, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        return dateStrings.stream()
                .filter(dateStr -> dateStr != null && !dateStr.isEmpty())
                .map(dateStr -> {
                    try {
                        return LocalDate.parse(dateStr, formatter);
                    } catch (DateTimeParseException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .map(date -> date.format(formatter))
                .distinct()
                .toList();
    }

    // 요일을 정렬된 문자열 리스트로 변환하는 메서드
    public List<String> getSortedDayRanges(List<String> dayStrings) {
        List<String> dayOrder = Arrays.asList("일", "월", "화", "수", "목", "금", "토");
        Map<String, Integer> dayOrderMap = IntStream.range(0, dayOrder.size())
                .boxed()
                .collect(Collectors.toMap(dayOrder::get, i -> i));

        return dayStrings.stream()
                .filter(day -> day != null && !day.isEmpty())
                .distinct()
                .sorted(Comparator.comparingInt(dayOrderMap::get))
                .toList();
    }
}