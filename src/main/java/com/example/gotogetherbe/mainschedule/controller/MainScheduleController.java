package com.example.gotogetherbe.mainschedule.controller;

import com.example.gotogetherbe.auth.config.LoginUser;
import com.example.gotogetherbe.mainschedule.dto.MainScheduleDto;
import com.example.gotogetherbe.mainschedule.dto.MainScheduleRequest;
import com.example.gotogetherbe.mainschedule.service.MainScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main-schedule")
public class MainScheduleController {

  private final MainScheduleService mainScheduleService;

  @PostMapping("/{postId}")
  public ResponseEntity<MainScheduleDto> createMainSchedule(@LoginUser String email,
      @PathVariable Long postId,
      @RequestBody MainScheduleRequest request
      ) {
    return ResponseEntity.ok(mainScheduleService.createMainSchedule(email, postId, request));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<?> getMainSchedule(@PathVariable Long postId) {
    return ResponseEntity.ok(mainScheduleService.getMainSchedule(postId));
  }

  @PutMapping("/{mainScheduleId}")
  public ResponseEntity<MainScheduleDto> updateMainSchedule(@LoginUser String email,
      @PathVariable Long mainScheduleId,
      @RequestBody MainScheduleRequest request
  ) {
    return ResponseEntity.ok(mainScheduleService.updateMainSchedule(email, mainScheduleId, request));
  }

  @DeleteMapping("/{mainScheduleId}")
  public ResponseEntity<MainScheduleDto> deleteMainSchedule(@LoginUser String email,
      @PathVariable Long mainScheduleId
  ) {
    return ResponseEntity.ok(mainScheduleService.deleteMainSchedule(email, mainScheduleId));
  }
}
