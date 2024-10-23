package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.FixedSchedule;
import side.onetime.domain.FixedSelection;
import side.onetime.dto.fixedEvent.request.CreateFixedEventRequest;
import side.onetime.dto.fixedEvent.response.FixedScheduleResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.FixedScheduleErrorStatus;
import side.onetime.repository.FixedScheduleRepository;
import side.onetime.repository.FixedSelectionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FixedScheduleService {
    private final FixedScheduleRepository fixedScheduleRepository;
    private final FixedSelectionRepository fixedSelectionRepository;

    // 고정 스케줄 등록 메서드
    @Transactional
    public void createFixedSchedules(CreateFixedEventRequest createFixedEventRequest, FixedEvent fixedEvent) {
        List<FixedScheduleResponse> fixedScheduleResponses = createFixedEventRequest.schedules();
        List<FixedSelection> fixedSelections = new ArrayList<>();

        for (FixedScheduleResponse fixedScheduleResponse : fixedScheduleResponses) {
            String day = fixedScheduleResponse.timePoint();
            List<String> times = fixedScheduleResponse.times();
            List<FixedSchedule> fixedSchedules = fixedScheduleRepository.findAllByDay(day)
                    .orElseThrow(() -> new CustomException(FixedScheduleErrorStatus._NOT_FOUND_FIXED_SCHEDULES));

            for (FixedSchedule fixedSchedule : fixedSchedules) {
                if (times.contains(fixedSchedule.getTime())) {
                    fixedSelections.add(FixedSelection.builder()
                            .fixedEvent(fixedEvent)
                            .fixedSchedule(fixedSchedule)
                            .build());
                }
            }
        }
        fixedSelectionRepository.saveAll(fixedSelections);
    }
}