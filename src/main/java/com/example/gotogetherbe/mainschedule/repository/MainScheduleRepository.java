package com.example.gotogetherbe.mainschedule.repository;

import com.example.gotogetherbe.mainschedule.entity.MainSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainScheduleRepository extends JpaRepository<MainSchedule, Long> {

  List<MainSchedule> findAllByPostId(Long postId);
}
