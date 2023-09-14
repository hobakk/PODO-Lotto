package com.example.sixnumber.global.util;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.sixnumber.user.entity.User;
import com.example.sixnumber.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// OAuth2 서비스 id (구글, 카카오, 네이버)
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String nameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

		OAuthAttributes attributes = OAuthAttributes.of(registrationId, nameAttributeName, oAuth2User.getAttributes());
		Map<String, Object> userAttribute = attributes.convertToMap();

		User user = saveOrUpdate(attributes);
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getAuthority())),
			userAttribute,
			attributes.getNameAttributeKey());
	}

	private User saveOrUpdate(OAuthAttributes attributes){
		User user = userRepository.findByEmail(attributes.getEmail())
			.map(entity -> entity.setNickname(attributes.getName()))
			.orElse(attributes.toEntity());
		return userRepository.save(user);
	}
}
