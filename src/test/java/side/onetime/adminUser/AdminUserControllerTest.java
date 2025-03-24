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
import side.onetime.domain.enums.AdminStatus;
import side.onetime.dto.adminUser.request.LoginAdminUserRequest;
import side.onetime.dto.adminUser.request.RegisterAdminUserRequest;
import side.onetime.dto.adminUser.request.UpdateAdminUserStatusRequest;
import side.onetime.dto.adminUser.response.AdminUserDetailResponse;
import side.onetime.dto.adminUser.response.GetAdminUserProfileResponse;
import side.onetime.dto.adminUser.response.LoginAdminUserResponse;
import side.onetime.service.AdminUserService;
import side.onetime.util.JwtUtil;

import java.util.List;

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
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/admin/profile")
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

    @Test
    @DisplayName("전체 관리자 정보를 조회한다.")
    public void getAllAdminUserDetail() throws Exception {
        // given
        String accessToken = "Bearer temp.jwt.access.token";

        List<AdminUserDetailResponse> response = List.of(
                new AdminUserDetailResponse(1L, "마스터 관리자", "master@example.com", AdminStatus.MASTER),
                new AdminUserDetailResponse(2L, "일반 관리자", "admin@example.com", AdminStatus.APPROVED)
        );

        // when
        Mockito.when(adminUserService.getAllAdminUserDetail(any(String.class)))
                .thenReturn(response);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/admin/all")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("전체 관리자 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.payload[0].id").value(1L))
                .andExpect(jsonPath("$.payload[0].name").value("마스터 관리자"))
                .andExpect(jsonPath("$.payload[0].email").value("master@example.com"))
                .andExpect(jsonPath("$.payload[0].admin_status").value("MASTER"))
                .andDo(MockMvcRestDocumentationWrapper.document("admin/get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Admin API")
                                        .description("전체 관리자 정보를 조회한다. (마스터 관리자만 가능)")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.ARRAY).description("응답 데이터"),
                                                fieldWithPath("payload[].id").type(JsonFieldType.NUMBER).description("관리자 ID"),
                                                fieldWithPath("payload[].name").type(JsonFieldType.STRING).description("관리자 이름"),
                                                fieldWithPath("payload[].email").type(JsonFieldType.STRING).description("관리자 이메일"),
                                                fieldWithPath("payload[].admin_status").type(JsonFieldType.STRING).description("관리자 상태 (MASTER, APPROVED, PENDING_APPROVAL)")
                                        )
                                        .responseSchema(Schema.schema("GetAllAdminUserDetailResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("관리자 권한을 수정한다.")
    public void updateAdminUserStatus() throws Exception {
        // given
        String accessToken = "Bearer temp.jwt.access.token";
        String requestContent = objectMapper.writeValueAsString(
                new UpdateAdminUserStatusRequest(2L, AdminStatus.APPROVED)
        );

        // when
        Mockito.doNothing().when(adminUserService).updateAdminUserStatus(any(String.class), any(UpdateAdminUserStatusRequest.class));

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/admin/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(requestContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("관리자 권한 수정에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("admin/update-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Admin API")
                                        .description("관리자 권한을 수정한다. (마스터 관리자만 가능)")
                                        .requestFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("수정 대상 관리자 ID"),
                                                fieldWithPath("admin_status").type(JsonFieldType.STRING).description("변경할 관리자 상태 (MASTER, APPROVED, PENDING_APPROVAL)")
                                        )
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .requestSchema(Schema.schema("UpdateAdminUserStatusRequest"))
                                        .responseSchema(Schema.schema("CommonSuccessResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("관리자 계정을 탈퇴한다.")
    public void withdrawAdminUser() throws Exception {
        // given
        String accessToken = "Bearer temp.jwt.access.token";

        // when
        Mockito.doNothing().when(adminUserService).withdrawAdminUser(any(String.class));

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/admin/withdraw")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("관리자 계정 탈퇴에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("admin/withdraw",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Admin API")
                                        .description("관리자 계정을 탈퇴한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("CommonSuccessResponse"))
                                        .build()
                        )
                ));
    }
}
