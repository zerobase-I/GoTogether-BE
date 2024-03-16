package com.example.gotogetherbe.mainschedule.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.mainschedule.dto.MainScheduleDto;
import com.example.gotogetherbe.mainschedule.dto.MainScheduleRequest;
import com.example.gotogetherbe.mainschedule.entity.MainSchedule;
import com.example.gotogetherbe.mainschedule.repository.MainScheduleRepository;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MainScheduleService {

  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final MainScheduleRepository mainScheduleRepository;

  /**
   * 주요일정 생성
   *
   * @param email      주요일정을 생성하는 회원의 이메일
   * @param postId     주요일정을 생성할 게시글ID
   * @param request    생성할 주요일정 정보
   * @return 생성된 주요일정의 정보를 포함한 MainScheduleDto 객체
   */
  @Transactional
  public MainScheduleDto createMainSchedule(String email, Long postId, MainScheduleRequest request) {
    Member member = getMemberOrThrow(email);
    Post post = getPostOrThrow(postId);

    // 게시글 작성자만 주요일정 추가 가능
    if (!Objects.equals(member.getId(), post.getMember().getId())) {
      throw new GlobalException(ErrorCode.MEMBER_POST_INCORRECT);
    }

    MainSchedule mainSchedule = mainScheduleRepository.save(MainSchedule.builder()
            .post(post)
            .scheduleDate(request.getScheduleDate())
            .content(request.getContent())
            .build());

    return MainScheduleDto.from(mainSchedule);
  }

  /**
   * 주요일정 목록 조회
   *
   * @param postId     주요일정을 조회할 게시글ID
   * @return 주요일정의 정보를 포함한 MainScheduleDto 목록
   */
  @Transactional(readOnly = true)
  public List<MainScheduleDto> getMainSchedule(Long postId) {
    return mainScheduleRepository.findAllByPostId(postId).stream().map(MainScheduleDto::from).collect(Collectors.toList());
  }

  /**
   * 주요일정 수정
   *
   * @param email      주요일정을 수정하는 회원의 이메일
   * @param mainScheduleId     수정할 주요일정ID
   * @param request    수정할 주요일정 정보
   * @return 수정된 주요일정의 정보를 포함한 MainScheduleDto 객체
   */
  @Transactional
  public MainScheduleDto updateMainSchedule(String email, Long mainScheduleId, MainScheduleRequest request) {
    Member member = getMemberOrThrow(email);
    MainSchedule mainSchedule = mainScheduleRepository.findById(mainScheduleId)
        .orElseThrow(() -> new GlobalException(ErrorCode.MAIN_SCHEDULE_NOT_FOUND));

    if (!Objects.equals(member.getId(), mainSchedule.getPost().getMember().getId())) {
      throw new GlobalException(ErrorCode.MEMBER_AND_MAIN_SCHEDULE_INCORRECT);
    }

    mainSchedule.updateScheduleDate(request.getScheduleDate());
    mainSchedule.updateContent(request.getContent());

    MainSchedule updatedMainSchedule = mainScheduleRepository.save(mainSchedule);

    return MainScheduleDto.from(updatedMainSchedule);
  }

  /**
   * 주요일정 삭제
   *
   * @param email      주요일정을 삭제하는 회원의 이메일
   * @param mainScheduleId     삭제할 주요일정ID
   * @return 삭제한 주요일정의 정보를 포함한 MainScheduleDto 객체
   */
  public MainScheduleDto deleteMainSchedule(String email, Long mainScheduleId) {
    Member member = getMemberOrThrow(email);
    MainSchedule mainSchedule = mainScheduleRepository.findById(mainScheduleId)
        .orElseThrow(() -> new GlobalException(ErrorCode.MAIN_SCHEDULE_NOT_FOUND));

    if (!Objects.equals(member.getId(), mainSchedule.getPost().getMember().getId())) {
      throw new GlobalException(ErrorCode.MEMBER_AND_MAIN_SCHEDULE_INCORRECT);
    }

    mainScheduleRepository.delete(mainSchedule);

    return MainScheduleDto.from(mainSchedule);
  }

  private Member getMemberOrThrow(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
  }

  private Post getPostOrThrow(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));
  }
}
