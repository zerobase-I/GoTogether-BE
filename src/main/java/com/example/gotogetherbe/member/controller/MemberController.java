package com.example.gotogetherbe.member.controller;


import com.example.gotogetherbe.auth.config.LoginUser;
import com.example.gotogetherbe.member.dto.MemberRequest;
import com.example.gotogetherbe.member.dto.MemberResponse;
import com.example.gotogetherbe.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/myProfile")
  public ResponseEntity<MemberResponse> getMyProfile(@LoginUser String username){
    return ResponseEntity.ok(memberService.getMyProfileInfo(username));
  }


  @PutMapping(path = "/myProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  ,produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MemberResponse> updateMyProfile(@RequestPart(name = "request", required = false)
  @Valid MemberRequest request,
      @LoginUser String username,
      @RequestPart(name = "image", required = false)MultipartFile profileImage){
    return ResponseEntity.ok(memberService.updateMyProfileInfo(request,username, profileImage));
  }

  @GetMapping("/profile")
  public ResponseEntity<MemberResponse> getProfile(@RequestParam("memberId") Long id){
    return ResponseEntity.ok(memberService.getMemberInfo(id));
  }

  @PatchMapping("/withdraw")
  public ResponseEntity<?> withdraw(@LoginUser String username){
    memberService.withdraw(username);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
  @GetMapping("/alarm")
  public ResponseEntity<?> getNotificationSetting(@LoginUser String username){
    return ResponseEntity.ok(memberService.checkAlarmStatus(username));
  }
  @PatchMapping("/alarm")
  public ResponseEntity<?> changeNotificationSetting(@LoginUser String username){
    memberService.changeAlarmStatus(username);
    return ResponseEntity.status(HttpStatus.OK).build();
  }


}
