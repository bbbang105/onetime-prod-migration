package side.onetime.admin;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import side.onetime.auth.service.CustomUserDetailsService;
import side.onetime.configuration.ControllerTestConfig;
import side.onetime.controller.BannerController;
import side.onetime.dto.admin.response.GetActivatedBannerResponse;
import side.onetime.service.AdminService;
import side.onetime.util.JwtUtil;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BannerController.class)
public class BannerControllerTest extends ControllerTestConfig {

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("활성화된 띠배너를 조회한다.")
    public void getActivatedBanner() throws Exception {
        // given
        Long bannerId = 1L;
        GetActivatedBannerResponse response = new GetActivatedBannerResponse(
                bannerId, "공지사항", "Notice", "#FF5733", "#FFFFFF", true, "2025-04-01 12:00:00", "https://www.link.com"
        );

        // when
        Mockito.when(adminService.getActivatedBanner()).thenReturn(response);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/banners/activated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("활성화된 띠배너 조회에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document("admin/banner-get-activated",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Banner API")
                                        .description("활성화된 띠배너를 조회한다.")
                                        .responseFields(
                                                fieldWithPath("is_success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("payload").type(JsonFieldType.OBJECT).description("배너 정보"),
                                                fieldWithPath("payload.id").type(JsonFieldType.NUMBER).description("배너 ID"),
                                                fieldWithPath("payload.content_kor").type(JsonFieldType.STRING).description("한국어 내용"),
                                                fieldWithPath("payload.content_eng").type(JsonFieldType.STRING).description("영어 내용"),
                                                fieldWithPath("payload.background_color_code").type(JsonFieldType.STRING).description("배경 색상 코드"),
                                                fieldWithPath("payload.text_color_code").type(JsonFieldType.STRING).description("텍스트 색상 코드"),
                                                fieldWithPath("payload.is_activated").type(JsonFieldType.BOOLEAN).description("활성화 여부"),
                                                fieldWithPath("payload.created_date").type(JsonFieldType.STRING).description("생성일자"),
                                                fieldWithPath("payload.link_url").type(JsonFieldType.STRING).description("링크 URL")
                                        )
                                        .responseSchema(Schema.schema("GetActivatedBannerResponse"))
                                        .build()
                        )
                ));
    }
}
