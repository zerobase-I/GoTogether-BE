package com.example.gotogetherbe.mainschedule.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainScheduleRequest {
  @NotNull(message = "주요일정의 날짜를 입력해주세요.")
  private LocalDate scheduleDate;

  @NotNull(message = "내용을 입력해주세요.")
  private String content;
}
