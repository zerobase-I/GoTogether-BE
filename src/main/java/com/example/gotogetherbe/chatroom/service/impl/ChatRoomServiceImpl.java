package com.example.gotogetherbe.chatroom.service.impl;

import com.example.gotogetherbe.chatmember.entity.ChatMember;
import com.example.gotogetherbe.chatmember.repository.ChatMemberRepository;
import com.example.gotogetherbe.chatroom.dto.ChatRoomDto;
import com.example.gotogetherbe.chatroom.entity.ChatRoom;
import com.example.gotogetherbe.chatroom.repository.ChatRoomRepository;
import com.example.gotogetherbe.chatroom.service.ChatRoomService;
import com.example.gotogetherbe.chatroom.type.ChatRoomStatus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;

  @Override
  public ChatRoomDto createChatRoom(String email, Long postId, String name) {
    Member member = memberRepository.findByEmail(email);

    Post post = postRepository.findByPostId(postId);

    if (member.getId() != post.getMember().getId()) {
      throw new CustomException(ErrorCode.MEMBER_POST_INCORRECT);
    }

    if (chatRoomRepository.existsById(postId)) {
      throw new CustomException(ErrorCode.ALREADY_CREATED_CHATROOM);
    }

    ChatRoom createdChatRoom = chatRoomRepository.save(ChatRoom.builder()
            .post(post)
            .name(name)
            .status(ChatRoomStatus.ACTIVE)
            .build());

    return ChatRoomDto.from(createdChatRoom);
  }

  @Override
  public List<ChatRoomDto> getChatRoomList(String email) {
    Member member = memberRepository.findByEmail(email);

    List<ChatRoom> chatRoomList = chatRoomRepository.findAllByMemberId(member.getId());

    return chatRoomList.stream().map(ChatRoomDto::from).collect(Collectors.toList());
  }

  @Override
  public String enterChatRoom(String email, Long chatRoomId) {
    Member member = memberRepository.findByEmail(email);

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

    if (member.getId() != chatRoom.getPost().getMember().getId()) {
      throw new CustomException(ErrorCode.MEMBER_POST_INCORRECT);
    }

    Optional<ChatMember> optionalChatMember = chatMemberRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId());

    if (optionalChatMember.isPresent()) {
      throw new CustomException(ErrorCode.ALREADY_PARTICIPANT_MEMBER);
    }

    ChatMember enterChatMember = chatMemberRepository.save(ChatMember.builder()
        .chatRoom(chatRoom)
        .member(member)
        .build());

    return member.getNickName() + "님이 " + enterChatMember.getChatRoom().getName() + "에 참여하였습니다.";
  }

  @Override
  public String exitChatRoom(String email, Long chatRoomId) {
    Member member = memberRepository.findByEmail(email);

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

    Optional<ChatMember> optionalChatMember = chatMemberRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId());

    if (optionalChatMember.isEmpty()) {
      throw new CustomException(ErrorCode.CHAT_MEMBER_NOT_FOUND);
    }

    chatMemberRepository.delete(optionalChatMember.get());

    return optionalChatMember.get().getMember().getNickName() + "님이 퇴장하였습니다.";
  }
}
