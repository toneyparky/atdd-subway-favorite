package wooteco.subway.doc;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class FavoriteDocumentation {
	public static RestDocumentationResultHandler addFavorite() {
		return document("favorites/add",
				pathParameters(
						parameterWithName("id").description("즐겨찾기를 추가할 Member의 id")
				),
				requestFields(
						fieldWithPath("sourceId").type(JsonFieldType.STRING).description("즐겨찾기 출발역 이름"),
						fieldWithPath("targetId").type(JsonFieldType.STRING).description("즐겨찾기 도착역 이름")
				),
				responseHeaders(
						headerWithName("Location").description("즐겨찾기 정보가 생성된 location")
				)
		);
	}

	public static RestDocumentationResultHandler failToCreateMember() {
		return document("members/create_fail");
	}

	public static RestDocumentationResultHandler readMember() {
		return document("members/read",
				requestParameters(
						parameterWithName("email").description("User 이메일"))
		);
	}

	public static RestDocumentationResultHandler failToReadMemberOfEmail() {
		return document("members/read_fail_email");
	}

	public static RestDocumentationResultHandler updateMember() {
		return document("members/update",
				pathParameters(
						parameterWithName("id").description("수정할 User id")
				),
				requestFields(
						fieldWithPath("name").type(JsonFieldType.STRING).description("User의 새로운 이름"),
						fieldWithPath("password").type(JsonFieldType.STRING).description("User의 새로운 비밀번호")
				)
		);
	}

	public static RestDocumentationResultHandler deleteMember() {
		return document("members/delete",
				pathParameters(
						parameterWithName("id").description("탈퇴할 User id")
				)
		);
	}

	public static RestDocumentationResultHandler failToAuthorizeMemberByToken() {
		return document("members/authorize_fail_token");
	}

	public static RestDocumentationResultHandler failToAuthorizeMemberBySession() {
		return document("members/authorize_fail_session");
	}

}
