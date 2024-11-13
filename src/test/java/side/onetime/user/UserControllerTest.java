package side.onetime.user;

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
import side.onetime.controller.UserController;
import side.onetime.dto.user.request.OnboardUserRequest;
import side.onetime.dto.user.request.UpdateUserProfileRequest;
import side.onetime.dto.user.response.GetUserProfileResponse;
import side.onetime.dto.user.response.OnboardUserResponse;
import side.onetime.service.UserService;
import side.onetime.util.JwtUtil;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends ControllerTestConfig {
    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("유저 온보딩을 진행한다.")
    public void onboardUser() throws Exception {
        // given
        OnboardUserResponse response = new OnboardUserResponse("sampleAccessToken", "sampleRefreshToken");
        Mockito.when(userService.onboardUser(any(OnboardUserRequest.class))).thenReturn(response);

        OnboardUserRequest request = new OnboardUserRequest("sampleRegisterToken", "UserNickname");

        String requestContent = new ObjectMapper().writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/users/onboarding")
                .content(requestContent)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.message").value("유저 온보딩에 성공했습니다."))
                .andExpect(jsonPath("$.payload.access_token").value("sampleAccessToken"))
                .andExpect(jsonPath("$.payload.refresh_token").value("sampleRefreshToken"))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("user/onboard",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User API")
                                        .description("유저 온보딩을 진행한다.")
                                        .requestFields(
                                                fieldWithPath("register_token").type(JsonFieldType.STRING).description("레지스터 토큰"),
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("payload.access_token").type(JsonFieldType.STRING).description("액세스 토큰"),
                                                fieldWithPath("payload.refresh_token").type(JsonFieldType.STRING).description("리프레쉬 토큰")
                                        )
                                        .requestSchema(Schema.schema("OnboardUserRequestSchema"))
                                        .responseSchema(Schema.schema("OnboardUserResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("유저 정보를 조회한다.")
    public void getUserProfile() throws Exception {
        // given
        String nickname = "UserNickname";
        String email = "user@example.com";
        GetUserProfileResponse response = new GetUserProfileResponse(nickname, email);

        Mockito.when(userService.getUserProfile(anyString())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.nickname").value(nickname))
                .andExpect(jsonPath("$.payload.email").value(email))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("user/get-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User API")
                                        .description("유저 정보를 조회한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("유저 정보 데이터"),
                                                fieldWithPath("payload.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                                fieldWithPath("payload.email").type(JsonFieldType.STRING).description("유저 이메일")
                                        )
                                        .responseSchema(Schema.schema("GetUserProfileResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("유저 정보를 수정한다.")
    public void updateUserProfile() throws Exception {
        // given
        UpdateUserProfileRequest request = new UpdateUserProfileRequest("NewNickname");

        Mockito.doNothing().when(userService).updateUserProfile(anyString(), any(UpdateUserProfileRequest.class));

        String requestContent = new ObjectMapper().writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/users/profile/action-update")
                .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저 정보 수정에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("user/update-profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User API")
                                        .description("유저 정보를 수정한다.")
                                        .requestFields(
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("수정할 닉네임")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("유저가 서비스를 탈퇴한다.")
    public void withdrawService() throws Exception {
        // given
        Mockito.doNothing().when(userService).withdrawService(anyString());

        // when
        ResultActions resultActions = this.mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/users/action-withdraw")
                .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("유저 서비스 탈퇴에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("user/withdraw-service",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User API")
                                        .description("유저가 서비스를 탈퇴한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .build()
                        )
                ));
    }
}
