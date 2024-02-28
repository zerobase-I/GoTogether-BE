package com.example.gotogetherbe.global.util.mail.service;


import static com.example.gotogetherbe.global.exception.type.ErrorCode.*;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.service.RedisService;
import com.example.gotogetherbe.global.util.mail.dto.SendMailResponse;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;
  private final RedisService redisService;
  private final MemberRepository memberRepository;

  private static final int CODE_LENGTH = 6;
  private static final Long EMAIL_TOKEN_EXPIRATION = 600000L;
  private static final String EMAIL_PREFIX = "Email-Auth:";


  /**
   * 인증 코드를 생성하고, 해당 코드를 이메일로 전송한 뒤, 생성된 코드와 그 유효 기간을 Redis 에 저장.
   *
   * @param email 인증 코드를 전송할 이메일 주소
   * @return 인증 코드와 이메일 주소를 포함한 SendMailResponse 객체
   */
  public SendMailResponse generateAndDispatchAuthCode(String email) {
    String code = createRandomCode();
    sendAuthMail(email, code);
    redisService.setDataExpire(EMAIL_PREFIX + email, code, EMAIL_TOKEN_EXPIRATION);

    return SendMailResponse.builder()
        .email(email)
        .code(code)
        .build();
  }

  /**
   * 주어진 이메일 주소와 인증 코드를 사용하여 이메일 메시지를 생성하고 전송.
   *
   * @param email 이메일 메시지를 전송할 이메일 주소
   * @param code  이메일 메시지에 포함될 인증 코드
   */
  public void sendAuthMail(String email, String code) {

    MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, "utf-8");

    try {
      mimeMessageHelper.setTo(email); // 메일 수신자
      mimeMailMessage.setSubject("== 회원가입을 위한 이메일 인증코드 ==");

      String msgCode = "<div style='margin:20px;'>"
          + "<h1> 안녕하세요 함께하는 여행의 기쁨 '같이가요!' 입니다. </h1>"
          + "<br>"
          + "<p>아래 코드를 입력해주세요<p>"
          + "<br>"
          + "<p>감사합니다.<p>"
          + "<br>"
          + "<div align='center' style='border:1px solid black; font-family:verdana';>"
          + "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>"
          + "<div style='font-size:130%'>"
          + "CODE : <strong>" + code + "</strong><div><br/> "
          + "</div>";

      mimeMessageHelper.setText(msgCode, true); // 메일 본문 내용, HTML 여부

    } catch (MessagingException e) {
      log.info("Mail sand fail");
      throw new GlobalException(INTERNAL_SERVER_ERROR);
    }
    javaMailSender.send(mimeMailMessage);
  }

  /**
   * 주어진 이메일 주소와 인증 코드를 사용하여 이메일 인증을 검증.
   *
   * @param email 검증할 이메일 주소
   * @param code  검증할 인증 코드
   */
  @Transactional
  public void verifyEmail(String email, String code) {
    if (!isVerify(email, code)) {
      throw new GlobalException(INVALID_AUTH_CODE);
    }

    // Member 이메일 인증 여부 변경
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    member.changeEmailAuth();
    memberRepository.save(member);

    redisService.deleteData(EMAIL_PREFIX + email);
  }

  /**
   * 주어진 이메일 주소와 인증 코드가 일치하는지 확인.
   *
   * @param email 확인할 이메일 주소
   * @param code  확인할 인증 코드
   * @return 이메일 주소와 인증 코드가 일치하면 true, 그렇지 않으면 false
   */
  private boolean isVerify(String email, String code) {
    String data = redisService.getData(EMAIL_PREFIX + email);
    if (data == null) {
      throw new GlobalException(USER_NOT_FOUND);
    }

    return data.equals(code);
  }

  /**
   * 랜덤한 인증 코드를 생성.
   *
   * @return 생성된 랜덤 인증 코드
   */
  private String createRandomCode() {
    Random random = new Random();
    StringBuilder builder = new StringBuilder();

    while (builder.length() < CODE_LENGTH) {
      builder.append(random.nextInt(10));
    }

    return builder.toString();
  }

}
