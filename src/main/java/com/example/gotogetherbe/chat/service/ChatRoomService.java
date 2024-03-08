package com.example.gotogetherbe.chat.service;

import com.example.gotogetherbe.chat.dto.ChatMemberDto;
import com.example.gotogetherbe.chat.entity.ChatMember;
import com.example.gotogetherbe.chat.repository.ChatMemberRepository;
import com.example.gotogetherbe.chat.dto.ChatRoomDto;
import com.example.gotogetherbe.chat.entity.ChatRoom;
import com.example.gotogetherbe.chat.repository.ChatRoomRepository;
import com.example.gotogetherbe.chat.type.ChatRoomStatus;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;

  /**
   * 채팅방 생성
   *
   * @param email  로그인한 사용자 이메일
   * @param postId 게시글에 해당하는 채팅방을 생성하기 위한 id
   * @return 생성된 ChatRoom 정보
   */
  @Transactional
  public ChatRoomDto createChatRoom(String email, Long postId) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));

    if (!Objects.equals(member.getId(), post.getMember().getId())) {
      throw new GlobalException(ErrorCode.MEMBER_POST_INCORRECT);
    }

    if (chatRoomRepository.existsById(postId)) {
      throw new GlobalException(ErrorCode.ALREADY_CREATED_CHATROOM);
    }

    ChatRoom createdChatRoom = chatRoomRepository.save(ChatRoom.builder()
            .post(post)
            .name(post.getTitle())
            .status(ChatRoomStatus.ACTIVE)
            .build());

    return ChatRoomDto.from(createdChatRoom);
  }

  /**
   * 내가 참여중인 채팅방 목록 조회
   *
   * @param email  로그인한 사용자 이메일
   * @return 내가 참여중인 채팅방 목록
   */
  public List<ChatRoomDto> getMyChatRoomList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    List<ChatMember> chatMemberList = chatMemberRepository.findAllByMemberId(member.getId());

    List<ChatRoom> chatRoomList = chatMemberList.stream().map(ChatMember::getChatRoom).toList();

    return chatRoomList.stream().map(ChatRoomDto::from).collect(Collectors.toList());
  }

  /**
   * 채팅방 입장
   *
   * @param email  로그인한 사용자 이메일
   * @param chatRoomId 참여하는 채팅방 아이디
   * @return 참여한 회원 정보
   */
  @Transactional
  public ChatMemberDto enterChatRoom(String email, Long chatRoomId) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new GlobalException(ErrorCode.CHATROOM_NOT_FOUND));

    Optional<ChatMember> optionalChatMember = chatMemberRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId());

    if (optionalChatMember.isPresent()) {
      throw new GlobalException(ErrorCode.ALREADY_PARTICIPANT_MEMBER);
    }

    ChatMember enterChatMember = chatMemberRepository.save(ChatMember.builder()
        .chatRoom(chatRoom)
        .member(member)
        .build());

    return ChatMemberDto.from(enterChatMember);
  }

  /**
   * 채팅방 퇴장
   *
   * @param email  로그인한 사용자 이메일
   * @param chatRoomId 퇴장하는 채팅방 아이디
   * @return 퇴장한 회원 정보
   */
  @Transactional
  public ChatMemberDto exitChatRoom(String email, Long chatRoomId) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new GlobalException(ErrorCode.CHATROOM_NOT_FOUND));

    ChatMember chatMember = chatMemberRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId())
        .orElseThrow(() -> new GlobalException(ErrorCode.CHAT_MEMBER_NOT_FOUND));

    chatMemberRepository.delete(chatMember);

    List<ChatMember> chatMemberList = chatMemberRepository.findAllByChatRoomId(chatRoom.getId());

    if (chatMemberList.isEmpty()) { // 채팅방 인원이 다 나갔다면 채팅방 삭제
      chatRoomRepository.delete(chatRoom);
    }

    return ChatMemberDto.from(chatMember);
  }
}
