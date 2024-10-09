package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.EventDto;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    // 이벤트 생성 API
    @PostMapping
    public ResponseEntity<ApiResponse<EventDto.CreateEventResponse>> createEvent(
            @RequestBody EventDto.CreateEventRequest createEventRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        EventDto.CreateEventResponse createEventResponse;
        if (authorizationHeader != null) {
            createEventResponse = eventService.createEventForAuthenticatedUser(createEventRequest, authorizationHeader);
        } else {
            createEventResponse = eventService.createEventForAnonymousUser(createEventRequest);
        }

        return ApiResponse.onSuccess(SuccessStatus._CREATED_EVENT, createEventResponse);
    }

    // 이벤트 조회 API
    @GetMapping("/{event_id}")
    public ResponseEntity<ApiResponse<EventDto.GetEventResponse>> getEvent(
            @PathVariable("event_id") String eventId) {

        EventDto.GetEventResponse getEventResponse = eventService.getEvent(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_EVENT, getEventResponse);
    }

    // 참여자 조회 API
    @GetMapping("/{event_id}/participants")
    public ResponseEntity<ApiResponse<EventDto.GetParticipantsResponse>> getParticipants(
            @PathVariable("event_id") String eventId) {

        EventDto.GetParticipantsResponse getParticipantsResponse = eventService.getParticipants(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_PARTICIPANTS, getParticipantsResponse);
    }

    // 가장 많이 되는 시간 조회 API
    @GetMapping("/{event_id}/most")
    public ResponseEntity<ApiResponse<List<EventDto.GetMostPossibleTime>>> getMostPossibleTime(
            @PathVariable("event_id") String eventId) {

        List<EventDto.GetMostPossibleTime> getMostPossibleTimes = eventService.getMostPossibleTime(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_MOST_POSSIBLE_TIME, getMostPossibleTimes);
    }

    // 유저 참여 이벤트 목록 조회 API
    @GetMapping("/user/all")
    public ResponseEntity<ApiResponse<List<EventDto.GetUserParticipatedEventsResponse>>> getUserParticipatedEvents(
            @RequestHeader("Authorization") String authorizationHeader) {

        List<EventDto.GetUserParticipatedEventsResponse> getUserParticipatedEventsResponses = eventService.getUserParticipatedEvents(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_PARTICIPATED_EVENTS, getUserParticipatedEventsResponses);
    }

    // 유저가 생성한 이벤트 삭제 API
    @DeleteMapping("/{event_id}")
    public ResponseEntity<ApiResponse<SuccessStatus>> removeUserCreatedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("event_id") String eventId) {

        eventService.removeUserCreatedEvent(authorizationHeader, eventId);
        return ApiResponse.onSuccess(SuccessStatus._REMOVE_USER_CREATED_EVENT);
    }
}
