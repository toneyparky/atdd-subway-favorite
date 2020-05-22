package wooteco.subway.web.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import wooteco.subway.doc.MemberDocumentation;
import wooteco.subway.domain.member.Member;
import wooteco.subway.service.member.MemberService;
import wooteco.subway.web.member.interceptor.BearerAuthInterceptor;
import wooteco.subway.web.member.interceptor.SessionInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wooteco.subway.service.member.MemberServiceTest.*;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

	@MockBean
	protected MemberService memberService;

	@MockBean
	protected BearerAuthInterceptor bearerAuthInterceptor;

	@MockBean
	protected SessionInterceptor sessionInterceptor;

	@Autowired
	protected MockMvc mockMvc;

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new ShallowEtagHeaderFilter()).alwaysDo(print()) // TODO: 2020/05/21 개꿀
				.apply(documentationConfiguration(restDocumentation))
				.build();
	}

	@Test
	public void createMember() throws Exception {
		Member member = new Member(1L, TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);
		given(memberService.createMember(any())).willReturn(member);

		String inputJson = "{\"email\":\"" + TEST_USER_EMAIL + "\"," +
				"\"name\":\"" + TEST_USER_NAME + "\"," +
				"\"password\":\"" + TEST_USER_PASSWORD + "\"}";

		this.mockMvc.perform(post("/members")
				.content(inputJson)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print())
				.andDo(MemberDocumentation.createMember());
	}

	@Test
	public void readMember() throws Exception {
		Member member = new Member(1L, TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);
		given(memberService.findMemberByEmail(any())).willReturn(member);

		String token = "bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicm93bkBlbWFpbC5jb20iLCJpYXQiOjE1OTAwNTA1NjMsImV4cCI6MTU5MDA1NDE2M30.bPh4VZcEj7aYlXDBP_o-1IqZw5AoKCIetrHvI7OcB_k";
		String session = "83C9D0BE28F5F940ECD3FFBCD0ED73DD";

		this.mockMvc.perform(get("/members?email=" + TEST_USER_EMAIL)
				.header("authorization", token)
				.header("cookies", "JSESSIONID=" + session)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(TEST_USER_NAME))
				.andDo(print())
				.andDo(MemberDocumentation.readMember());
	}

	@Test
	public void updateMember() throws Exception {
		doNothing().when(memberService).updateMember(any(), any());
		given(bearerAuthInterceptor.preHandle(any(), any(), any())).willReturn(true);
		given(sessionInterceptor.preHandle(any(), any(), any())).willReturn(true);

		String inputJson = "{" +
				"\"name\":\"" + TEST_USER_NAME + "\"," +
				"\"password\":\"" + TEST_USER_PASSWORD + "\"}";

		this.mockMvc.perform(put("/members/{id}", 1L)
				.content(inputJson)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(MemberDocumentation.updateMember());
	}

	@Test
	public void deleteMember() throws Exception {
		doNothing().when(memberService).deleteMember(any());

		this.mockMvc.perform(delete("/members/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andDo(print())
				.andDo(MemberDocumentation.deleteMember());
	}
}
