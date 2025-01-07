package side.onetime.fixed;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import side.onetime.configuration.ControllerTestConfig;
import side.onetime.controller.FixedController;
import side.onetime.dto.fixed.request.CreateFixedEventRequest;
import side.onetime.dto.fixed.request.ModifyFixedEventRequest;
import side.onetime.dto.fixed.response.FixedEventByDayResponse;
import side.onetime.dto.fixed.response.FixedEventDetailResponse;
import side.onetime.dto.fixed.response.FixedEventResponse;
import side.onetime.dto.fixed.response.FixedScheduleResponse;
import side.onetime.service.FixedEventService;
import side.onetime.service.FixedScheduleService;
import side.onetime.util.JwtUtil;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FixedController.class)
public class FixedControllerTest extends ControllerTestConfig {

    @MockBean
    private FixedEventService fixedEventService;

    @MockBean
    private FixedScheduleService fixedScheduleService;

    @MockBean
    private JwtUtil jwtUtil;

    private final String authorizationHeader = "Bearer token";

    @Test
    @DisplayName("고정 스케줄을 등록한다.")
    public void createFixedEvent() throws Exception {
        // given
        CreateFixedEventRequest request = new CreateFixedEventRequest("고정 이벤트", List.of(new FixedScheduleResponse("월", List.of("09:00", "09:30"))));

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/fixed-schedules")
                        .header("Authorization", authorizationHeader)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.message").value("고정 스케줄 등록에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("고정 스케줄을 등록한다.")
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("스케줄 이름"),
                                                fieldWithPath("schedules").type(JsonFieldType.ARRAY).description("고정 스케줄 목록"),
                                                fieldWithPath("schedules[].time_point").type(JsonFieldType.STRING).description("요일"),
                                                fieldWithPath("schedules[].times").type(JsonFieldType.ARRAY).description("시간 목록")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("전체 고정 스케줄을 조회한다.")
    public void getAllFixedSchedules() throws Exception {
        // given
        List<FixedEventResponse> responses = List.of(
                new FixedEventResponse(1L, List.of(new FixedScheduleResponse("월", List.of("09:00", "09:30")))),
                new FixedEventResponse(2L, List.of(new FixedScheduleResponse("화", List.of("09:00", "09:30"))))
        );
        Mockito.when(fixedScheduleService.getAllFixedSchedules(authorizationHeader)).thenReturn(responses);

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/fixed-schedules")
                        .header("Authorization", authorizationHeader)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("전체 고정 스케줄 조회에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/getAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("전체 고정 스케줄을 조회한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload[].id").type(JsonFieldType.NUMBER).description("고정 스케줄 ID"),
                                                fieldWithPath("payload[].schedules[].time_point").type(JsonFieldType.STRING).description("요일"),
                                                fieldWithPath("payload[].schedules[].times[]").type(JsonFieldType.ARRAY).description("시간 목록")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("특정 고정 스케줄을 상세 조회한다.")
    public void getFixedScheduleDetail() throws Exception {
        // given
        Long fixedEventId = 1L;
        FixedEventDetailResponse response = new FixedEventDetailResponse("고정 이벤트", List.of(new FixedScheduleResponse("월", List.of("09:00", "09:30"))));
        Mockito.when(fixedScheduleService.getFixedScheduleDetail(authorizationHeader, fixedEventId)).thenReturn(response);

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/fixed-schedules/{id}", fixedEventId)
                        .header("Authorization", authorizationHeader)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("특정 고정 스케줄 상세 조회에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/getDetail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("특정 고정 스케줄을 상세 조회한다.")
                                        .pathParameters(
                                                parameterWithName("id").description("고정 스케줄 ID [예시 : 1 (NUMBER Type)]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload.title").type(JsonFieldType.STRING).description("고정 스케줄 제목"),
                                                fieldWithPath("payload.schedules[].time_point").type(JsonFieldType.STRING).description("요일"),
                                                fieldWithPath("payload.schedules[].times[]").type(JsonFieldType.ARRAY).description("시간 목록")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("특정 고정 스케줄을 수정한다.")
    public void modifyFixedEvent() throws Exception {
        // given
        Long fixedEventId = 1L;
        ModifyFixedEventRequest request = new ModifyFixedEventRequest("수정된 고정 스케줄", List.of(new FixedScheduleResponse("화", List.of("10:00", "11:00"))));

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/api/v1/fixed-schedules/{id}", fixedEventId)
                        .header("Authorization", authorizationHeader)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("고정 스케줄 수정에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/modify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("특정 고정 스케줄을 수정한다.")
                                        .pathParameters(
                                                parameterWithName("id").description("고정 스케줄 ID [예시 : 1 (NUMBER Type)]")
                                        )
                                        .requestFields(
                                                fieldWithPath("title").type(JsonFieldType.STRING).description("수정된 스케줄 이름"),
                                                fieldWithPath("schedules").type(JsonFieldType.ARRAY).description("수정된 고정 스케줄 목록"),
                                                fieldWithPath("schedules[].time_point").type(JsonFieldType.STRING).description("요일"),
                                                fieldWithPath("schedules[].times").type(JsonFieldType.ARRAY).description("시간 목록")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("특정 고정 스케줄을 삭제한다.")
    public void removeFixedEvent() throws Exception {
        // given
        Long fixedEventId = 1L;

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/fixed-schedules/{id}", fixedEventId)
                        .header("Authorization", authorizationHeader)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("고정 스케줄 삭제에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("특정 고정 스케줄을 삭제한다.")
                                        .pathParameters(
                                                parameterWithName("id").description("고정 스케줄 ID [예시 : 1 (NUMBER Type)]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("요일별 고정 스케줄을 조회한다.")
    public void getFixedEventByDay() throws Exception {
        // given
        String day = "mon";
        List<FixedEventByDayResponse> responses = List.of(new FixedEventByDayResponse(1L, "고정 이벤트", "09:00", "10:00"));
        Mockito.when(fixedEventService.getFixedEventByDay(authorizationHeader, day)).thenReturn(responses);

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/fixed-schedules/by-day/{day}", day)
                        .header("Authorization", authorizationHeader)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("요일 별 고정 스케줄 조회에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/getByDay",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("요일별 고정 스케줄을 조회한다.")
                                        .pathParameters(
                                                parameterWithName("day").description("조회할 요일 [예시 : mon, tue, ...]")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload[].id").type(JsonFieldType.NUMBER).description("고정 스케줄 ID"),
                                                fieldWithPath("payload[].title").type(JsonFieldType.STRING).description("고정 스케줄 이름"),
                                                fieldWithPath("payload[].start_time").type(JsonFieldType.STRING).description("시작 시간"),
                                                fieldWithPath("payload[].end_time").type(JsonFieldType.STRING).description("종료 시간")
                                        )
                                        .build()
                        )
                ));
    }
}
