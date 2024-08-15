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
    public List<LocalTime> createTimeSets(LocalTime startTime, LocalTime endTime) {
        List<LocalTime> timeSets = new ArrayList<>();
        LocalTime currentTime = startTime;

        while (!currentTime.isAfter(endTime.minusMinutes(30))) {
            timeSets.add(currentTime);
            currentTime = currentTime.plusMinutes(30);
        }

        return timeSets;
    }
}
