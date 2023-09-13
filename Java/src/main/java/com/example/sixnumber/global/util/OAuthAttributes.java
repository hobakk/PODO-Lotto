package com.example.sixnumber.global.util;

import java.util.Map;

import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.type.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
	private Map<String, Object> attributes; // OAuth2 반환하는 유저 정보 Map
	private String nameAttributeKey;
	private String name;
	private String email;
	private String picture;

	@Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
		switch (registrationId) {
			case "google":
				return ofGoogle(userNameAttributeName, attributes);
			case "kakao":
				return ofKakao("email", attributes);
			case "naver":
				return ofNaver("id", attributes);
			default:
				throw new RuntimeException();
		}
	}

	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
			.name((String) attributes.get("name"))
			.email((String) attributes.get("email"))
			.picture((String) attributes.get("picture"))
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	private static OAuthAttributes ofKakao(String attributeKey, Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

		return OAuthAttributes.builder()
			.name((String) kakaoProfile.get("nickname"))
			.email((String) kakaoAccount.get("email"))
			.picture((String) kakaoProfile.get("profile_image_url"))
			.attributes(attributes)
			.nameAttributeKey(attributeKey)
			.build();
	}

	private static OAuthAttributes ofNaver(String attributeKey, Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		return OAuthAttributes.builder()
			.name((String) response.get("name"))
			.email((String) response.get("email"))
			.picture((String) response.get("profile_image"))
			.attributes(attributes)
			.nameAttributeKey(attributeKey)
			.build();
	}

	public User toEntity(){
		return new User(email, name, UserRole.ROLE_USER);
	}
}
