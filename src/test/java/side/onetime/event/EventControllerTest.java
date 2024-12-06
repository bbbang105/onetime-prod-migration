package side.onetime.event;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import side.onetime.configuration.ControllerTestConfig;
import side.onetime.controller.EventController;
import side.onetime.domain.enums.Category;
import side.onetime.domain.enums.EventStatus;
import side.onetime.dto.event.request.CreateEventRequest;
import side.onetime.dto.event.request.ModifyUserCreatedEventTitleRequest;
import side.onetime.dto.event.response.*;
import side.onetime.service.EventService;
import side.onetime.util.JwtUtil;

import java.util.List;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest extends ControllerTestConfig {

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("이벤트를 생성한다. (토큰 유무에 따라 로그인/비로그인 구분)")
    public void createEventForAnonymousUser() throws Exception {
        // given
        UUID eventId = UUID.randomUUID();
        CreateEventResponse response = new CreateEventResponse(eventId);
        Mockito.when(eventService.createEventForAnonymousUser(any(CreateEventRequest.class)))
                .thenReturn(response);

        CreateEventRequest request = new CreateEventRequest(
                "Sample Event",
                "10:00",
                "12:00",
                Category.DATE,
                List.of("2024.11.13")
        );

        String requestContent = new ObjectMapper().writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/events")
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.message").value("이벤트 생성에 성공했습니다."))
                .andExpect(jsonPath("$.payload.event_id").value(eventId.toString()))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("이벤트를 생성한다.(토큰 유무에 따라 로그인/비로그인 구분)")
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("이벤트 제목"),
                                                fieldWithPath("start_time").type(JsonFieldType.STRING).description("이벤트 시작 시간"),
                                                fieldWithPath("end_time").type(JsonFieldType.STRING).description("이벤트 종료 시간"),
                                                fieldWithPath("category").type(JsonFieldType.STRING).description("이벤트 카테고리"),
                                                fieldWithPath("ranges").type(JsonFieldType.ARRAY).description("이벤트 날짜 또는 요일 범위")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("payload.event_id").type(JsonFieldType.STRING).description("생성된 이벤트의 UUID (형식: UUID)")
                                        )
                                        .requestSchema(Schema.schema("CreateEventRequestSchema"))
                                        .responseSchema(Schema.schema("CreateEventResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("이벤트를 조회한다.")
    public void getEvent() throws Exception {
        // given
        UUID eventId = UUID.randomUUID();
        GetEventResponse response = new GetEventResponse(
                "Sample Event",
                "10:00",
                "12:00",
                Category.DATE,
                List.of("2024.11.13"),
                EventStatus.CREATOR
        );

        Mockito.when(eventService.getEvent(eventId.toString(), null))
                .thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/events/{event_id}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("이벤트 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.title").value("Sample Event"))
                .andExpect(jsonPath("$.payload.start_time").value("10:00"))
                .andExpect(jsonPath("$.payload.end_time").value("12:00"))
                .andExpect(jsonPath("$.payload.category").value("DATE"))
                .andExpect(jsonPath("$.payload.ranges[0]").value("2024.11.13"))
                .andExpect(jsonPath("$.payload.event_status").value("CREATOR"))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("이벤트를 조회한다.")
                                        .pathParameters(
                                                parameterWithName("event_id").description("조회할 이벤트의 ID [예시 : dd099816-2b09-4625-bf95-319672c25659]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("payload.title").type(JsonFieldType.STRING).description("이벤트 제목"),
                                                fieldWithPath("payload.start_time").type(JsonFieldType.STRING).description("이벤트 시작 시간"),
                                                fieldWithPath("payload.end_time").type(JsonFieldType.STRING).description("이벤트 종료 시간"),
                                                fieldWithPath("payload.category").type(JsonFieldType.STRING).description("이벤트 카테고리"),
                                                fieldWithPath("payload.ranges").type(JsonFieldType.ARRAY).description("이벤트 날짜 또는 요일 범위"),
                                                fieldWithPath("payload.event_status").type(JsonFieldType.STRING).description("이벤트 상태 (로그인 유저만 반환)")
                                        )
                                        .responseSchema(Schema.schema("GetEventResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("이벤트 참여자 목록을 조회한다.")
    public void getParticipants() throws Exception {
        // given
        String eventId = UUID.randomUUID().toString();
        GetParticipantsResponse response = new GetParticipantsResponse(List.of("Member1", "User1", "Member2", "User2"));

        Mockito.when(eventService.getParticipants(anyString()))
                .thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/events/{event_id}/participants", eventId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("참여자 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.names[0]").value("Member1"))
                .andExpect(jsonPath("$.payload.names[1]").value("User1"))
                .andExpect(jsonPath("$.payload.names[2]").value("Member2"))
                .andExpect(jsonPath("$.payload.names[3]").value("User2"))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/get-participants",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("이벤트 참여자 목록을 조회한다.")
                                        .pathParameters(
                                                parameterWithName("event_id").description("조회할 이벤트의 ID [예시 : dd099816-2b09-4625-bf95-319672c25659]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("payload.names").type(JsonFieldType.ARRAY).description("참여자 이름 목록")
                                        )
                                        .responseSchema(Schema.schema("GetParticipantsResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("가장 많이 되는 시간을 조회한다.")
    public void getMostPossibleTime() throws Exception {
        // given
        String eventId = UUID.randomUUID().toString();
        List<GetMostPossibleTime> response = List.of(
                new GetMostPossibleTime("2024.11.13", "10:00", "10:30", 5, List.of("User1", "User2"), List.of("User3")),
                new GetMostPossibleTime("2024.11.13", "11:00", "11:30", 4, List.of("User1", "User3"), List.of("User2"))
        );

        Mockito.when(eventService.getMostPossibleTime(anyString())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/events/{event_id}/most", eventId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("가장 많이 되는 시간 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].time_point").value("2024.11.13"))
                .andExpect(jsonPath("$.payload[0].start_time").value("10:00"))
                .andExpect(jsonPath("$.payload[0].end_time").value("10:30"))
                .andExpect(jsonPath("$.payload[0].possible_count").value(5))
                .andExpect(jsonPath("$.payload[0].possible_names[0]").value("User1"))
                .andExpect(jsonPath("$.payload[0].possible_names[1]").value("User2"))
                .andExpect(jsonPath("$.payload[0].impossible_names[0]").value("User3"))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/get-most-possible-time",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("가장 많이 되는 시간을 조회한다.")
                                        .pathParameters(
                                                parameterWithName("event_id").description("조회할 이벤트의 ID [예시 : dd099816-2b09-4625-bf95-319672c25659]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.ARRAY).description("가장 많이 되는 시간 목록"),
                                                fieldWithPath("payload[].time_point").type(JsonFieldType.STRING).description("날짜 또는 요일"),
                                                fieldWithPath("payload[].start_time").type(JsonFieldType.STRING).description("시작 시간"),
                                                fieldWithPath("payload[].end_time").type(JsonFieldType.STRING).description("종료 시간"),
                                                fieldWithPath("payload[].possible_count").type(JsonFieldType.NUMBER).description("가능한 참여자 수"),
                                                fieldWithPath("payload[].possible_names").type(JsonFieldType.ARRAY).description("가능한 참여자 이름 목록"),
                                                fieldWithPath("payload[].impossible_names").type(JsonFieldType.ARRAY).description("참여 불가능한 이름 목록")
                                        )
                                        .responseSchema(Schema.schema("GetMostPossibleTimeResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("유저 참여 이벤트 목록을 조회한다.")
    public void getUserParticipatedEvents() throws Exception {
        // given
        List<GetUserParticipatedEventsResponse> response = List.of(
                new GetUserParticipatedEventsResponse(
                        UUID.randomUUID(),
                        Category.DATE,
                        "Sample Event",
                        "2024.11.13",
                        10,
                        EventStatus.CREATOR,
                        List.of(
                                new GetMostPossibleTime("2024.11.13", "10:00", "10:30", 5, List.of("User1", "User2"), List.of("User3"))
                        )
                )
        );

        Mockito.when(eventService.getUserParticipatedEvents(anyString())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/events/user/all")
                .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저 참여 이벤트 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].event_id").exists())
                .andExpect(jsonPath("$.payload[0].title").value("Sample Event"))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/get-user-participated-events",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("유저가 참여한 이벤트 목록을 조회한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.ARRAY).description("참여 이벤트 목록"),
                                                fieldWithPath("payload[].event_id").type(JsonFieldType.STRING).description("이벤트 ID"),
                                                fieldWithPath("payload[].category").type(JsonFieldType.STRING).description("이벤트 카테고리"),
                                                fieldWithPath("payload[].title").type(JsonFieldType.STRING).description("이벤트 제목"),
                                                fieldWithPath("payload[].created_date").type(JsonFieldType.STRING).description("이벤트 생성일"),
                                                fieldWithPath("payload[].participant_count").type(JsonFieldType.NUMBER).description("참여자 수"),
                                                fieldWithPath("payload[].event_status").type(JsonFieldType.STRING).description("이벤트 참여 상태"),
                                                fieldWithPath("payload[].most_possible_times").type(JsonFieldType.ARRAY).description("가장 많이 가능한 시간대"),
                                                fieldWithPath("payload[].most_possible_times[].time_point").type(JsonFieldType.STRING).description("날짜 또는 요일"),
                                                fieldWithPath("payload[].most_possible_times[].start_time").type(JsonFieldType.STRING).description("시작 시간"),
                                                fieldWithPath("payload[].most_possible_times[].end_time").type(JsonFieldType.STRING).description("종료 시간"),
                                                fieldWithPath("payload[].most_possible_times[].possible_count").type(JsonFieldType.NUMBER).description("가능한 참여자 수"),
                                                fieldWithPath("payload[].most_possible_times[].possible_names").type(JsonFieldType.ARRAY).description("참여 가능한 유저 이름 목록"),
                                                fieldWithPath("payload[].most_possible_times[].impossible_names").type(JsonFieldType.ARRAY).description("참여 불가능한 유저 이름 목록")
                                        )
                                        .responseSchema(Schema.schema("GetUserParticipatedEventsResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("유저가 생성한 이벤트를 삭제한다.")
    public void removeUserCreatedEvent() throws Exception {
        // given
        String eventId = UUID.randomUUID().toString();
        Mockito.doNothing().when(eventService).removeUserCreatedEvent(anyString(), anyString());

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/events/{event_id}", eventId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저가 생성한 이벤트 삭제에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/remove-user-created-event",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("유저가 생성한 이벤트를 삭제한다.")
                                        .pathParameters(
                                                parameterWithName("event_id").description("삭제할 이벤트의 ID [예시 : dd099816-2b09-4625-bf95-319672c25659]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("RemoveUserCreatedEventResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("유저가 생성한 이벤트 제목을 수정한다.")
    public void modifyUserCreatedEventTitle() throws Exception {
        // given
        String eventId = UUID.randomUUID().toString();
        ModifyUserCreatedEventTitleRequest request = new ModifyUserCreatedEventTitleRequest("수정할 이벤트 제목");

        String requestContent = new ObjectMapper().writeValueAsString(request);

        Mockito.doNothing().when(eventService).modifyUserCreatedEventTitle(anyString(), anyString(), any(ModifyUserCreatedEventTitleRequest.class));

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/events/{event_id}", eventId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저가 생성한 이벤트 제목 수정에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/modify-user-created-event-title",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("유저가 생성한 이벤트 제목을 수정한다.")
                                        .pathParameters(
                                                parameterWithName("event_id").description("수정할 이벤트의 ID [예시 : dd099816-2b09-4625-bf95-319672c25659]")
                                        )
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("새로운 이벤트 제목")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("ModifyUserCreatedEventTitleResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("이벤트 QR 코드를 조회한다.")
    public void getEventQrCode() throws Exception {
        // given
        String eventId = UUID.randomUUID().toString();
        String qrCodeImgUrl = "https://example.com/qr-code-image.png";
        GetEventQrCodeResponse response = GetEventQrCodeResponse.from(qrCodeImgUrl);

        Mockito.when(eventService.getEventQrCode(anyString())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/events/qr/{event_id}", eventId)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("이벤트 QR 코드 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.qr_code_img_url").value(qrCodeImgUrl))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("event/get-event-qr-code",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Event API")
                                        .description("이벤트 QR 코드를 조회한다.")
                                        .pathParameters(
                                                parameterWithName("event_id").description("조회할 이벤트의 ID [예시 : dd099816-2b09-4625-bf95-319672c25659]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("payload.qr_code_img_url").type(JsonFieldType.STRING).description("QR 코드 이미지 URL")
                                        )
                                        .responseSchema(Schema.schema("GetEventQrCodeResponseSchema"))
                                        .build()
                        )
                ));
    }
}
