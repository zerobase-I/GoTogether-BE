package com.example.gotogetherbe.global.scheduler;

import static com.example.gotogetherbe.accompany.request.type.AccompanyStatus.COMPLETED;
import static com.example.gotogetherbe.accompany.request.type.AccompanyStatus.PARTICIPATING;
import static com.example.gotogetherbe.notification.type.NotificationType.REVIEW_WRITING_SUGGESTION;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.repository.AccompanyRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.notification.service.EventPublishService;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final AccompanyRepository accompanyRepository;
  private final EventPublishService eventPublishService;

  @Scheduled(cron = "0 0 0 * * *", zone="Asia/Seoul")
  public void startTravel() {
    List<Post> postList  = postRepository.findAllByStartDate(LocalDateTime.now());

    if (!postList.isEmpty()) {
      postList.forEach(post -> {
        post.setRecruitmentStatus(PostRecruitmentStatus.IN_PROGRESS);
        postRepository.save(post);
        log.info("[Scheduler] travel start - post_id : {}", post.getId());
      });
    }
  }

  @Scheduled(cron = "0 0 0 * * *", zone="Asia/Seoul")
  @Transactional
  public void endTravel() {
    List<Post> postList  = postRepository.findAllByEndDate(LocalDateTime.now());

    if (!postList.isEmpty()) {
      postList.forEach(post -> {
        post.setRecruitmentStatus(PostRecruitmentStatus.COMPLETED);
        postRepository.save(post);
        log.info("[Scheduler] travel end - post_id : {}", post.getId());
        updateAccompanyStatus(post);
      });
    }
  }
  private void updateAccompanyStatus(Post post) {
    List<Accompany> accompanies = accompanyRepository.findAllByPostIdAndStatus(post.getId(),
        PARTICIPATING);
    Set<Long> memberIds = new HashSet<>();

    accompanies.forEach(accompany -> {
      accompany.updateRequestStatus(COMPLETED);
      accompanyRepository.save(accompany);
      memberIds.add(accompany.getRequestMember().getId());
      memberIds.add(accompany.getRequestedMember().getId());
      log.info("[Scheduler] accompany status update - accompany_id : {}", accompany.getId());
    });

    for (Long memberId : memberIds) {
      Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
      eventPublishService.publishEvent(post, member, REVIEW_WRITING_SUGGESTION);
    }
  }
}
