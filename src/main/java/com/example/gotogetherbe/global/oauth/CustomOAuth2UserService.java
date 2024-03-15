package com.example.gotogetherbe.global.oauth;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.service.MemberService;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final MemberService memberService;
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

    // OAuth2UserService 를 사용하여 OAuth2User 정보를 가져온다.
    OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

    // 클라이언트 등록 ID와 사용자 이름 속성을 가져온다.
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();


    // OAuth2UserService 를 사용하여 가져온 OAuth2User 정보로 OAuth2Attribute 객체를 만든다.
    OAuth2Attribute oAuth2Attribute =
        OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

    // OAuth2Attribute 의 속성값들을 Map 으로 반환 받는다.
    Map<String, Object> memberAttribute = oAuth2Attribute.convertToMap();

    // 사용자 email(또는 id) 정보를 가져온다.
    String email = (String) memberAttribute.get("email");

    // 이메일로 가입된 회원인지 조회한다.
    Optional<Member> findMember = memberService.findByEmail(email);

    if (findMember.isEmpty()) {
      // 회원이 존재하지 않을경우, memberAttribute 의 exist 값을 false 로 넣어준다.
      memberAttribute.put("exist", false);
      // 회원의 권한(회원이 존재하지 않으므로 기본권한인 ROLE_USER 를 넣어준다), 회원속성, 속성이름을 이용해 DefaultOAuth2User 객체를 생성해 반환한다.
      return new DefaultOAuth2User(
          Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
          memberAttribute, "email");
    }

    // 회원이 존재할경우, memberAttribute 의 exist 값을 true 로 넣어준다.
    memberAttribute.put("exist", true);

    // 회원의 권한과, 회원속성, 속성이름을 이용해 DefaultOAuth2User 객체를 생성해 반환한다.
    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(findMember.get().getRoleType().toString())),
        memberAttribute, "email");
  }
}
