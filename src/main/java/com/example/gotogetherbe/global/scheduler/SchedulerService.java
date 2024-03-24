package com.example.gotogetherbe.global.scheduler;

import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

  private final PostRepository postRepository;
  @Scheduled(cron = "0 0 * * *", zone="Asia/Seoul")
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

  @Scheduled(cron = "0 0 * * *", zone="Asia/Seoul")
  public void endTravel() {
    List<Post> postList  = postRepository.findAllByEndDate(LocalDateTime.now());

    if (!postList.isEmpty()) {
      postList.forEach(post -> {
        post.setRecruitmentStatus(PostRecruitmentStatus.COMPLETED);
        postRepository.save(post);
        log.info("[Scheduler] travel end - post_id : {}", post.getId());
      });
    }
  }
}
