package side.onetime.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import side.onetime.domain.enums.Category;
import side.onetime.dto.event.response.GetMostPossibleTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class  DateUtil {

    /**
     * 30분 단위 타임 셋 생성 메서드.
     *
     * 주어진 시작 시간과 종료 시간 사이의 모든 30분 간격의 시간을 리스트로 반환합니다.
     * 종료 시간이 "24:00"인 경우 처리하여 23:30까지 포함합니다.
     *
     * @param start 시작 시간 (HH:mm 형식)
     * @param end 종료 시간 (HH:mm 형식)
     * @return 30분 간격의 시간 리스트
     */
    public static List<LocalTime> createTimeSets(String start, String end) {
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

    /**
     * 날짜 리스트 정렬 메서드.
     *
     * 주어진 날짜 문자열 리스트를 지정된 패턴에 따라 파싱 후 정렬하여 반환합니다.
     * 중복된 날짜는 제거합니다.
     *
     * @param dateStrings 날짜 문자열 리스트
     * @param pattern 날짜 형식 패턴 (예: yyyy.MM.dd)
     * @return 정렬된 날짜 문자열 리스트
     */
    public static List<String> getSortedDateRanges(List<String> dateStrings, String pattern) {
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

    /**
     * 요일 리스트 정렬 메서드.
     *
     * 주어진 요일 문자열 리스트를 일요일부터 토요일까지의 순서대로 정렬하여 반환합니다.
     * 중복된 요일은 제거합니다.
     *
     * @param dayStrings 요일 문자열 리스트
     * @return 정렬된 요일 문자열 리스트
     */
    public static List<String> getSortedDayRanges(List<String> dayStrings) {
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

    /**
     * 최적 시간대 정렬 메서드.
     *
     * 주어진 최적 시간대 리스트를 날짜 또는 요일 기준으로 정렬하여 반환합니다.
     * 카테고리에 따라 날짜 또는 요일로 정렬 방식을 구분합니다.
     *
     * @param mostPossibleTimes 정렬할 최적 시간대 리스트
     * @param category 정렬 기준 카테고리 (DAY 또는 DATE)
     * @return 정렬된 최적 시간대 리스트
     */
    public static List<GetMostPossibleTime> sortMostPossibleTimes(List<GetMostPossibleTime> mostPossibleTimes, Category category) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        List<String> sortedList;
        if (category.equals(Category.DAY)) {
            // 요일로 정렬
            List<String> dayOrder = Arrays.asList("일", "월", "화", "수", "목", "금", "토");
            Map<String, Integer> dayOrderMap = IntStream.range(0, dayOrder.size())
                    .boxed()
                    .collect(Collectors.toMap(dayOrder::get, i -> i));

            sortedList = mostPossibleTimes.stream()
                    .map(GetMostPossibleTime::timePoint)
                    .filter(dayOrderMap::containsKey)
                    .sorted(Comparator.comparingInt(dayOrderMap::get))
                    .distinct()
                    .toList();
        } else {
            // 날짜로 정렬
            sortedList = mostPossibleTimes.stream()
                    .map(GetMostPossibleTime::timePoint)
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
                .filter(mostPossibleTime -> sortedList.contains(mostPossibleTime.timePoint()))
                .sorted(Comparator.comparingInt(mostPossibleTime -> sortedList.indexOf(mostPossibleTime.timePoint())))
                .toList();
    }

    /**
     * yyyy.MM.dd 형식으로 날짜 변환 메서드.
     *
     * 주어진 LocalDateTime 객체를 yyyy.MM.dd 형식의 문자열로 변환합니다.
     * 변환 실패 시 원래 문자열을 반환합니다.
     *
     * @param dateTime 변환할 LocalDateTime 객체
     * @return 변환된 날짜 문자열
     */
    public static String formatDateToYearMonthDay(LocalDateTime dateTime) {
        String dateTimeString = String.valueOf(dateTime);
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        try {
            LocalDate parsedDate = LocalDate.parse(dateTimeString, originalFormatter);
            return parsedDate.format(targetFormatter);
        } catch (DateTimeParseException e) {
            return dateTimeString;
        }
    }

    /**
     * 30분 추가 메서드.
     *
     * 주어진 시간에 30분을 추가한 결과를 반환합니다.
     * 시간이 "00:00"이 될 경우 "24:00"으로 변환하여 반환합니다.
     *
     * @param time 추가할 시간 (HH:mm 형식)
     * @return 30분이 추가된 시간 문자열
     */
    public static String addThirtyMinutes(String time) {
        LocalTime parsedTime = LocalTime.parse(time);
        LocalTime updatedTime = parsedTime.plusMinutes(30);

        return updatedTime.toString().equals("00:00") ? "24:00" : updatedTime.toString();
    }
}
