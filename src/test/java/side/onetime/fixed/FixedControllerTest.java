package side.onetime.fixed;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;
import side.onetime.auth.dto.CustomUserDetails;
import side.onetime.auth.service.CustomUserDetailsService;
import side.onetime.configuration.ControllerTestConfig;
import side.onetime.controller.FixedController;
import side.onetime.domain.User;
import side.onetime.dto.fixed.request.UpdateFixedScheduleRequest;
import side.onetime.dto.fixed.response.FixedScheduleResponse;
import side.onetime.dto.fixed.response.GetFixedScheduleResponse;
import side.onetime.service.FixedScheduleService;
import side.onetime.util.JwtUtil;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FixedController.class)
public class FixedControllerTest extends ControllerTestConfig {

    @MockBean
    private FixedScheduleService fixedScheduleService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    public void setupSecurityContext() {
        User mockUser = User.builder().name("User").email("user@example.com").build();
        customUserDetails = new CustomUserDetails(mockUser);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );
    }

    @Test
    @DisplayName("고정 스케줄을 조회한다.")
    public void getAllFixedSchedules() throws Exception {
        List<FixedScheduleResponse> schedules = List.of(
                new FixedScheduleResponse("월", List.of("08:00", "08:30", "09:00")),
                new FixedScheduleResponse("화", List.of("10:00", "10:30", "11:00")),
                new FixedScheduleResponse("수", List.of("14:00", "14:30", "15:00")),
                new FixedScheduleResponse("목", List.of("16:00", "16:30", "17:00")),
                new FixedScheduleResponse("금", List.of("18:00", "18:30", "19:00")),
                new FixedScheduleResponse("토", List.of("20:00", "20:30", "21:00")),
                new FixedScheduleResponse("일", List.of("22:00", "22:30", "23:00"))
        );

        GetFixedScheduleResponse response = new GetFixedScheduleResponse(schedules);

        Mockito.when(fixedScheduleService.getUserFixedSchedule(customUserDetails.user())).thenReturn(response);

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/fixed-schedules")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저 고정 스케줄 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.schedules").isArray())
                .andExpect(jsonPath("$.payload.schedules[0].time_point").value("월"))
                .andExpect(jsonPath("$.payload.schedules[0].times[0]").value("08:00"))
                .andExpect(jsonPath("$.payload.schedules[1].time_point").value("화"))
                .andExpect(jsonPath("$.payload.schedules[1].times[1]").value("10:30"))
                .andExpect(jsonPath("$.payload.schedules[6].time_point").value("일"))
                .andExpect(jsonPath("$.payload.schedules[6].times[2]").value("23:00"))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/getAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("고정 스케줄을 조회한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload.schedules").type(JsonFieldType.ARRAY).description("고정 스케줄 목록"),
                                                fieldWithPath("payload.schedules[].time_point").type(JsonFieldType.STRING).description("요일"),
                                                fieldWithPath("payload.schedules[].times[]").type(JsonFieldType.ARRAY).description("시간 목록")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("고정 스케줄을 수정한다.")
    public void createFixedSchedules() throws Exception {
        UpdateFixedScheduleRequest request = new UpdateFixedScheduleRequest(
                List.of(
                        new FixedScheduleResponse("월", List.of("08:00", "08:30", "09:00")),
                        new FixedScheduleResponse("화", List.of("10:00", "10:30", "11:00")),
                        new FixedScheduleResponse("수", List.of("14:00", "14:30", "15:00")),
                        new FixedScheduleResponse("목", List.of("16:00", "16:30", "17:00"))
                )
        );

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/v1/fixed-schedules")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저 고정 스케줄 수정에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("fixed/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Fixed API")
                                        .description("고정 스케줄을 수정한다.")
                                        .requestFields(
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
}
