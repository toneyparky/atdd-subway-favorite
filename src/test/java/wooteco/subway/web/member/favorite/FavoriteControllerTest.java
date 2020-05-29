package wooteco.subway.web.member.favorite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import wooteco.subway.doc.FavoriteDocumentation;
import wooteco.subway.domain.member.Favorite;
import wooteco.subway.domain.member.FavoriteDetail;
import wooteco.subway.domain.member.Member;
import wooteco.subway.exception.DuplicatedFavoriteException;
import wooteco.subway.exception.NoFavoriteExistException;
import wooteco.subway.infra.JwtTokenProvider;
import wooteco.subway.service.member.MemberService;
import wooteco.subway.service.member.favorite.FavoriteService;
import wooteco.subway.web.member.interceptor.AuthorizationExtractor;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FavoriteControllerTest {
	public static final String TEST_USER_EMAIL = "brown@email.com";
	public static final String TEST_USER_NAME = "브라운";
	public static final String TEST_USER_PASSWORD = "brown";
	public static final String TEST_USER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJicm93bkBlbWFpbC5jb20iLCJpYXQiOjE1OTAwNTA1NjMsImV4cCI6MTU5MDA1NDE2M30.bPh4VZcEj7aYlXDBP_o-1IqZw5AoKCIetrHvI7OcB_k";
	@MockBean
	protected FavoriteService favoriteService;
	@MockBean
	protected MemberService memberService;
	@MockBean
	protected JwtTokenProvider jwtTokenProvider;
	@MockBean
	protected AuthorizationExtractor authorizationExtractor;
	@Autowired
	protected MockMvc mockMvc;

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new ShallowEtagHeaderFilter()).alwaysDo(print())
				.apply(documentationConfiguration(restDocumentation))
				.build();
	}

	@DisplayName("즐겨찾기 추가")
	@Test
	void addFavorite() throws Exception {
		Member member = new Member(1L, TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);
		Favorite favorite = Favorite.of(1L, 2L);
		when(favoriteService.addFavorite(anyLong(), anyLong(), anyLong())).thenReturn(favorite);
		when(memberService.findMemberByEmail(anyString())).thenReturn(member);
		given(authorizationExtractor.extract(any(), any())).willReturn(TEST_USER_TOKEN);
		given(jwtTokenProvider.validateToken(any())).willReturn(true);
		given(jwtTokenProvider.getSubject(any())).willReturn(TEST_USER_EMAIL);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("loginMemberEmail", TEST_USER_EMAIL);

		String inputJson = "{\"sourceId\":\"" + 1L + "\"," +
				"\"targetId\":\"" + 2L + "\"}";

		this.mockMvc.perform(post("/members/{id}/favorites", 1L)
				.session(session)
				.header("authorization", "Bearer " + TEST_USER_TOKEN)
				.content(inputJson)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.sourceId").value(1L))
				.andExpect(jsonPath("$.targetId").value(2L))
				.andDo(FavoriteDocumentation.addFavorite());
	}

	@DisplayName("즐겨찾기 추가시 없는 역 추가할 경우 실패")
	@Test
	void addFavorite_fail() throws Exception {
		Member member = new Member(1L, TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);
		when(favoriteService.addFavorite(anyLong(), anyLong(), anyLong())).thenThrow(new DuplicatedFavoriteException());
		when(memberService.findMemberByEmail(anyString())).thenReturn(member);
		given(authorizationExtractor.extract(any(), any())).willReturn(TEST_USER_TOKEN);
		given(jwtTokenProvider.validateToken(any())).willReturn(true);
		given(jwtTokenProvider.getSubject(any())).willReturn(TEST_USER_EMAIL);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("loginMemberEmail", TEST_USER_EMAIL);

		String inputJson = "{\"sourceId\":\"" + 199L + "\"," +
				"\"targetId\":\"" + 200L + "\"}";

		this.mockMvc.perform(post("/members/{id}/favorites", 1L)
				.session(session)
				.header("authorization", "Bearer " + TEST_USER_TOKEN)
				.content(inputJson)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(FavoriteDocumentation.failToAddFavorite());
	}

	@DisplayName("즐겨찾기 조회")
	@Test
	void readFavorite() throws Exception {
		Member member = new Member(1L, TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);


		List<FavoriteDetail> favoriteDetails = Arrays.asList(new FavoriteDetail(1L, 1L, 2L, "유안", "토니"),
				new FavoriteDetail(1L, 2L, 3L, "토니", "코즈"));

		when(favoriteService.readFavorites(anyLong())).thenReturn(favoriteDetails);
		when(memberService.findMemberByEmail(anyString())).thenReturn(member);
		given(authorizationExtractor.extract(any(), any())).willReturn(TEST_USER_TOKEN);
		given(jwtTokenProvider.validateToken(any())).willReturn(true);
		given(jwtTokenProvider.getSubject(any())).willReturn(TEST_USER_EMAIL);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("loginMemberEmail", TEST_USER_EMAIL);

		this.mockMvc.perform(get("/members/{id}/favorites", 1L)
				.session(session)
				.header("authorization", "Bearer " + TEST_USER_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.favorites").isArray())
				.andDo(FavoriteDocumentation.readFavorites());
	}

	@DisplayName("즐겨찾기 삭제")
	@Test
	void removeFavorite() throws Exception {
		Member member = new Member(1L, TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);

		when(memberService.findMemberByEmail(anyString())).thenReturn(member);
		doNothing().when(favoriteService).removeFavorite(anyLong(), anyLong(), anyLong());
		given(authorizationExtractor.extract(any(), any())).willReturn(TEST_USER_TOKEN);
		given(jwtTokenProvider.validateToken(any())).willReturn(true);
		given(jwtTokenProvider.getSubject(any())).willReturn(TEST_USER_EMAIL);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("loginMemberEmail", TEST_USER_EMAIL);

		this.mockMvc.perform(delete("/members/{memberId}/favorites/source/{sourceId}/target/{targetId}", 1L, 1L, 2L)
				.session(session)
				.header("authorization", "Bearer " + TEST_USER_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(FavoriteDocumentation.deleteFavorite());
	}

	@DisplayName("즐겨찾기 삭제시 없는 역 추가할 경우 실패")
	@Test
	void removeFavorite_fail() throws Exception {
		Member member = new Member(1L, TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);

		when(memberService.findMemberByEmail(anyString())).thenReturn(member);
		doThrow(new NoFavoriteExistException()).when(favoriteService).removeFavorite(anyLong(), anyLong(), anyLong());
		given(authorizationExtractor.extract(any(), any())).willReturn(TEST_USER_TOKEN);
		given(jwtTokenProvider.validateToken(any())).willReturn(true);
		given(jwtTokenProvider.getSubject(any())).willReturn(TEST_USER_EMAIL);

		MockHttpSession session = new MockHttpSession();
		session.setAttribute("loginMemberEmail", TEST_USER_EMAIL);

		this.mockMvc.perform(delete("/members/{memberId}/favorites/source/{sourceId}/target/{targetId}", 1L, 1L, 2L)
				.session(session)
				.header("authorization", "Bearer " + TEST_USER_TOKEN)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andDo(FavoriteDocumentation.failToDeleteFavorite());
	}
}
