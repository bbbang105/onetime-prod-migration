package side.onetime.adminUser;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import side.onetime.auth.service.CustomUserDetailsService;
import side.onetime.configuration.ControllerTestConfig;
import side.onetime.controller.AdminUserController;
import side.onetime.dto.adminUser.request.LoginAdminUserRequest;
import side.onetime.dto.adminUser.request.RegisterAdminUserRequest;
import side.onetime.dto.adminUser.response.GetAdminUserProfileResponse;
import side.onetime.dto.adminUser.response.LoginAdminUserResponse;
import side.onetime.service.AdminUserService;
import side.onetime.util.JwtUtil;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
public class AdminUserControllerTest extends ControllerTestConfig {

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("관리자 계정 회원가입을 진행한다.")
    public void registerAdminUser() throws Exception {
        // given
        RegisterAdminUserRequest request = new RegisterAdminUserRequest(
                "관리자 이름",
                "admin@example.com",
                "Password123!"
        );
        String requestContent = objectMapper.writeValueAsString(request);

        // when
        Mockito.doNothing().when(adminUserService).registerAdminUser(any(RegisterAdminUserRequest.class));

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.message").value("관리자 계정 등록에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("admin/register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Admin API")
                                        .description("관리자 회원가입을 진행한다.")
                                        .requestFields(
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .requestSchema(Schema.schema("RegisterAdminUserRequest"))
                                        .responseSchema(Schema.schema("CommonSuccessResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("관리자 계정 로그인을 진행한다.")
    public void loginAdminUser() throws Exception {
        // given
        LoginAdminUserRequest request = new LoginAdminUserRequest(
                "admin@example.com",
                "Password123!"
        );
        String requestContent = objectMapper.writeValueAsString(request);
        String tempAccessToken = "temp.jwt.access.token";

        // when
        Mockito.when(adminUserService.loginAdminUser(any(LoginAdminUserRequest.class)))
                .thenReturn(LoginAdminUserResponse.of(tempAccessToken));

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("관리자 계정 로그인에 성공했습니다."))
                .andExpect(jsonPath("$.payload.access_token").value(tempAccessToken))
                .andDo(MockMvcRestDocumentationWrapper.document("admin/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Admin API")
                                        .description("관리자 계정 로그인을 진행한다.")
                                        .requestFields(
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("payload.access_token").type(JsonFieldType.STRING).description("액세스 토큰")
                                        )
                                        .requestSchema(Schema.schema("LoginAdminUserRequest"))
                                        .responseSchema(Schema.schema("LoginAdminUserResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("관리자 프로필 조회를 진행한다.")
    public void getAdminUserProfile() throws Exception {
        // given
        String accessToken = "Bearer temp.jwt.access.token";

        GetAdminUserProfileResponse response = new GetAdminUserProfileResponse(
                "관리자 이름",
                "admin@example.com"
        );

        // when
        Mockito.when(adminUserService.getAdminUserProfile(any(String.class)))
                .thenReturn(response);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/admin/profile")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("관리자 프로필 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload.name").value("관리자 이름"))
                .andExpect(jsonPath("$.payload.email").value("admin@example.com"))
                .andDo(MockMvcRestDocumentationWrapper.document("admin/profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Admin API")
                                        .description("관리자 프로필 조회를 진행한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("payload.name").type(JsonFieldType.STRING).description("관리자 이름"),
                                                fieldWithPath("payload.email").type(JsonFieldType.STRING).description("관리자 이메일")
                                        )
                                        .responseSchema(Schema.schema("GetAdminUserProfileResponse"))
                                        .build()
                        )
                ));
    }
}
