package side.onetime.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
}