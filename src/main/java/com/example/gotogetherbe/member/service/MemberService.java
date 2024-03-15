package com.example.gotogetherbe.member.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.PROFILE_IMAGE_UPLOAD_ERROR;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.util.aws.service.AwsS3Service;
import com.example.gotogetherbe.member.dto.MemberRequest;
import com.example.gotogetherbe.member.dto.MemberResponse;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final AwsS3Service awsS3Service;
  private final PasswordEncoder passwordEncoder;



  @Transactional(readOnly = true)
  public MemberResponse getMyProfileInfo(String username){

    return MemberResponse.fromEntity(memberRepository.findByEmail(username)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND)), true);
  }

  @Transactional(readOnly = true)
  public MemberResponse getMemberInfo(Long id){
    return MemberResponse.fromEntity(memberRepository.findById(id)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND)), false);
  }

  @Transactional
  public MemberResponse updateMyProfileInfo(MemberRequest memberDto, String username,
      MultipartFile profileImage){

    Member member = memberRepository.findByEmail(username)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));

    String encodedPasswordEncoder = passwordEncoder.encode(memberDto.getPassword());
    String profileImageUrl = member.getProfileImageUrl();

    if (profileImage != null && !profileImage.isEmpty()) {
      // 기존 이미지가 있다면 삭제
      if(profileImageUrl != null && !profileImageUrl.isEmpty()){
          awsS3Service.deleteFileUsingUrl(profileImageUrl);
      }
      // 새 이미지 업로드 시도
      try {
        profileImageUrl = uploadProfileImage(profileImage);
      } catch (Exception e) { // 이미지 업로드 중 오류 발생 시 예외 처리
        throw new GlobalException(PROFILE_IMAGE_UPLOAD_ERROR);
      }
    }

    // MemberDto 로 부터 값을 받아와 Member 객체의 상태 변경
    memberDto.updateMemberInfoToEntity(member,encodedPasswordEncoder,profileImageUrl);

    return MemberResponse.fromEntity(member,true);

  }

  /**
   * 프로필 이미지를 AWS S3에 업로드하고 그에 대한 URL 을 반환.
   *
   * @param multipartFile 업로드할 이미지 파일
   * @return 업로드된 이미지의 S3 URL
   */
  private String uploadProfileImage(MultipartFile multipartFile) {
    log.info("[uploadProfileImage] start");

    String fileName = awsS3Service.generateFileName(multipartFile);
    awsS3Service.uploadToS3(multipartFile, fileName);

    return awsS3Service.getUrl(fileName);
  }




}
