package com.example.gotogetherbe.global.util.jwt;


import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.util.jwt.dto.CustomUserDto;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailService implements UserDetailsService {

  private final MemberRepository userRepository;
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Member member = userRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));

    return new JwtUserDetails(CustomUserDto.fromEntity(member));
  }
}
