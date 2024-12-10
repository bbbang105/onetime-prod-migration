package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.event.request.CreateEventRequest;
import side.onetime.dto.event.request.ModifyUserCreatedEventRequest;
import side.onetime.dto.event.response.*;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * 이벤트 생성 API.
     *
     * 이 API는 새로운 이벤트를 생성합니다. 인증된 유저와 익명 유저 모두 이벤트를 생성할 수 있으며,
     * 인증된 유저의 경우 추가적인 정보가 저장됩니다.
     *
     * @param createEventRequest 생성할 이벤트에 대한 요청 데이터 (제목, 시작/종료 시간, 카테고리, 설문 범위 등)
     * @param authorizationHeader 인증된 유저의 토큰 (선택 사항)
     * @return 생성된 이벤트의 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateEventResponse>> createEvent(
            @Valid @RequestBody CreateEventRequest createEventRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        CreateEventResponse createEventResponse;
        if (authorizationHeader != null) {
            createEventResponse = eventService.createEventForAuthenticatedUser(createEventRequest, authorizationHeader);
        } else {
            createEventResponse = eventService.createEventForAnonymousUser(createEventRequest);
        }

        return ApiResponse.onSuccess(SuccessStatus._CREATED_EVENT, createEventResponse);
    }

    /**
     * 이벤트 조회 API.
     *
     * 이 API는 특정 이벤트의 세부 정보를 조회합니다. 이벤트의 제목, 시간, 카테고리 등의 정보를 제공하며
     * 인증된 유저일 경우 추가적인 정보가 포함될 수 있습니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰 (선택 사항)
     * @param eventId 조회할 이벤트의 ID
     * @return 조회한 이벤트의 세부 정보
     */
    @GetMapping("/{event_id}")
    public ResponseEntity<ApiResponse<GetEventResponse>> getEvent(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable("event_id") String eventId) {

        GetEventResponse getEventResponse = eventService.getEvent(eventId, authorizationHeader);

        return ApiResponse.onSuccess(SuccessStatus._GET_EVENT, getEventResponse);
    }

    /**
     * 참여자 조회 API.
     *
     * 이 API는 특정 이벤트에 참여한 모든 참여자의 이름 목록을 조회합니다.
     *
     * @param eventId 참여자 목록을 조회할 이벤트의 ID
     * @return 해당 이벤트에 참여한 참여자의 이름 목록
     */
    @GetMapping("/{event_id}/participants")
    public ResponseEntity<ApiResponse<GetParticipantsResponse>> getParticipants(
            @PathVariable("event_id") String eventId) {

        GetParticipantsResponse getParticipantsResponse = eventService.getParticipants(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_PARTICIPANTS, getParticipantsResponse);
    }

    /**
     * 가장 많이 되는 시간 조회 API.
     *
     * 이 API는 특정 이벤트에서 가장 많이 가능한 시간대를 조회하여, 가능 인원과 해당 시간대 정보를 제공합니다.
     *
     * @param eventId 조회할 이벤트의 ID
     * @return 가능 인원이 많은 시간대와 관련 세부 정보
     */
    @GetMapping("/{event_id}/most")
    public ResponseEntity<ApiResponse<List<GetMostPossibleTime>>> getMostPossibleTime(
            @PathVariable("event_id") String eventId) {

        List<GetMostPossibleTime> getMostPossibleTimes = eventService.getMostPossibleTime(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_MOST_POSSIBLE_TIME, getMostPossibleTimes);
    }

    /**
     * 유저 참여 이벤트 목록 조회 API.
     *
     * 이 API는 인증된 유저가 참여한 모든 이벤트 목록을 조회합니다. 유저의 참여 상태, 이벤트 정보 등이 포함됩니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @return 유저가 참여한 이벤트 목록
     */
    @GetMapping("/user/all")
    public ResponseEntity<ApiResponse<List<GetUserParticipatedEventsResponse>>> getUserParticipatedEvents(
            @RequestHeader("Authorization") String authorizationHeader) {

        List<GetUserParticipatedEventsResponse> getUserParticipatedEventsResponses = eventService.getUserParticipatedEvents(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_PARTICIPATED_EVENTS, getUserParticipatedEventsResponses);
    }

    /**
     * 유저가 생성한 이벤트 삭제 API.
     *
     * 이 API는 인증된 유저가 생성한 특정 이벤트를 삭제합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param eventId 삭제할 이벤트의 ID
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/{event_id}")
    public ResponseEntity<ApiResponse<SuccessStatus>> removeUserCreatedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("event_id") String eventId) {

        eventService.removeUserCreatedEvent(authorizationHeader, eventId);
        return ApiResponse.onSuccess(SuccessStatus._REMOVE_USER_CREATED_EVENT);
    }

    /**
     * 유저가 생성한 이벤트 수정 API.
     *
     * 이 API는 인증된 유저가 생성한 특정 이벤트의 제목, 시간, 설문 범위를 수정합니다.
     * 수정 가능한 항목은 다음과 같습니다:
     * - 이벤트 제목
     * - 시작 시간 및 종료 시간
     * - 날짜 또는 요일 범위
     *
     * 요청 데이터에 따라 변경 사항을 반영하며, 필요에 따라 기존 스케줄 데이터를 삭제하거나 새로운 스케줄을 생성합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param eventId 수정할 이벤트의 ID
     * @param modifyUserCreatedEventRequest 새로운 이벤트 정보가 담긴 요청 데이터 (제목, 시간, 범위 등)
     * @return 수정 성공 여부
     */
    @PatchMapping("/{event_id}")
    public ResponseEntity<ApiResponse<SuccessStatus>> modifyUserCreatedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("event_id") String eventId,
            @Valid @RequestBody ModifyUserCreatedEventRequest modifyUserCreatedEventRequest) {

        eventService.modifyUserCreatedEvent(authorizationHeader, eventId, modifyUserCreatedEventRequest);
        return ApiResponse.onSuccess(SuccessStatus._MODIFY_USER_CREATED_EVENT);
    }

    /**
     * 이벤트 QR Code 조회 API.
     *
     * 이 API는 이벤트로 이동할 수 있는 QR Code 이미지를 반환합니다.
     *
     * @param eventId QR Code를 조회할 이벤트의 ID
     * @return QR Code 이미지 URL
     */
    @GetMapping("/qr/{event_id}")
    public ResponseEntity<ApiResponse<GetEventQrCodeResponse>> getEventQrCode(
            @PathVariable("event_id") String eventId) {

        GetEventQrCodeResponse response = eventService.getEventQrCode(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_EVENT_QR_CODE, response);
    }
}
