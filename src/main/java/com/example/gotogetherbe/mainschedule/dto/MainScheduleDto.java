package com.example.gotogetherbe.mainschedule.dto;

import com.example.gotogetherbe.mainschedule.entity.MainSchedule;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainScheduleDto {

  private Long id;
  private Long postId;
  private LocalDate scheduleDate;
  private String content;

  public static MainScheduleDto from(MainSchedule mainSchedule) {
    return MainScheduleDto.builder()
        .id(mainSchedule.getId())
        .postId(mainSchedule.getPost().getId())
        .scheduleDate(mainSchedule.getScheduleDate())
        .content(mainSchedule.getContent())
        .build();
  }
}
