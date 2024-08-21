package side.onetime.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import side.onetime.dto.EventDto;
import side.onetime.global.common.constant.Category;

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

        boolean isEndTimeMidnight = end.equals("24:00");
        if (isEndTimeMidnight) {
            end = "23:59";
        }

        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        LocalTime currentTime = startTime;

        while (!currentTime.isAfter(endTime.minusMinutes(30))) {
            timeSets.add(currentTime);
            currentTime = currentTime.plusMinutes(30);
        }

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

    // 최적 시간대 리스트를 날짜 또는 요일별로 정렬
    public List<EventDto.GetMostPossibleTime> sortMostPossibleTimes(List<EventDto.GetMostPossibleTime> mostPossibleTimes, Category category) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        List<String> sortedList;
        if (category.equals(Category.DAY)) {
            // 요일로 정렬
            List<String> dayOrder = Arrays.asList("일", "월", "화", "수", "목", "금", "토");
            Map<String, Integer> dayOrderMap = IntStream.range(0, dayOrder.size())
                    .boxed()
                    .collect(Collectors.toMap(dayOrder::get, i -> i));

            sortedList = mostPossibleTimes.stream()
                    .map(EventDto.GetMostPossibleTime::getTimePoint)
                    .filter(dayOrderMap::containsKey)
                    .sorted(Comparator.comparingInt(dayOrderMap::get))
                    .distinct()
                    .toList();
        } else {
            // 날짜로 정렬
            sortedList = mostPossibleTimes.stream()
                    .map(EventDto.GetMostPossibleTime::getTimePoint)
                    .filter(timePoint -> {
                        try {
                            LocalDate.parse(timePoint, dateFormatter);
                            return true;
                        } catch (DateTimeParseException e) {
                            return false;
                        }
                    })
                    .sorted(Comparator.comparing(timePoint -> LocalDate.parse(timePoint, dateFormatter)))
                    .distinct()
                    .toList();
        }

        // 정렬된 리스트에 따라 `EventDto.GetMostPossibleTime` 객체 정렬
        return mostPossibleTimes.stream()
                .filter(mostPossibleTime -> sortedList.contains(mostPossibleTime.getTimePoint()))
                .sorted(Comparator.comparingInt(mostPossibleTime -> sortedList.indexOf(mostPossibleTime.getTimePoint())))
                .toList();
    }
}