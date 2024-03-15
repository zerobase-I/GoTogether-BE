package com.example.gotogetherbe.chat.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.gotogetherbe.chat.dto.ChatLastMessageRequest;
import com.example.gotogetherbe.chat.dto.ChatMemberDto;
import com.example.gotogetherbe.chat.dto.ChatMessageDto;
import com.example.gotogetherbe.chat.dto.ChatRoomDto;
import com.example.gotogetherbe.chat.entity.ChatMember;
import com.example.gotogetherbe.chat.entity.ChatMessage;
import com.example.gotogetherbe.chat.entity.ChatRoom;
import com.example.gotogetherbe.chat.repository.ChatMemberRepository;
import com.example.gotogetherbe.chat.repository.ChatMessageRepository;
import com.example.gotogetherbe.chat.repository.ChatRoomRepository;
import com.example.gotogetherbe.chat.type.ChatRoomStatus;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import com.example.gotogetherbe.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
@Transactional
public class ChatRoomServiceTest {

  @InjectMocks
  ChatRoomService chatRoomService;

  @Mock
  ChatRoomRepository chatRoomRepository;

  @Mock
  ChatMessageRepository chatMessageRepository;

  @Mock
  MemberRepository memberRepository;

  @Mock
  ChatMemberRepository chatMemberRepository;

  @Mock
  PostRepository postRepository;

  private Member member;
  private Post post;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .email("test1234@gmail.com")
        .password("1234")
        .name("kim")
        .nickname("nick")
        .address("서울시 강남구")
        .phoneNumber("010-1234-5678")
        .age(25)
        .gender(MemberGender.MALE)
        .profileImageUrl(null)
        .mbti(MemberMbti.ISFP)
        .instagramId("instagram123")
        .description("hi i'm kim")
        .loginType(MemberLoginType.EMAIL)
        .roleType(MemberRoleType.USER)
        .emailAuth(true)
        .posts(null)
        .build();

    post = Post.builder()
        .id(1L)
        .member(member)
        .travelCountry(TravelCountryType.KOREA)
        .travelCity(TravelCityType.SEOUL)
        .startDate(LocalDateTime.parse("2024-05-01T12:00:00"))
        .endDate(LocalDateTime.parse("2024-05-03T12:00:00"))
        .gender(PostGenderType.ALL)
        .minimumAge(20)
        .maximumAge(30)
        .recruitsPeople(5)
        .estimatedTravelExpense(500_000)
        .category(PostCategory.FOOD_CAFE)
        .title("Go Seoul")
        .content("My First Trip At Seoul")
        .chatRoomExists(false)
        .build();
  }

  @Test
  @DisplayName("채팅방 생성 성공")
  void createChatRoom_success() {
    //given
    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .post(post)
        .name(post.getTitle())
        .status(ChatRoomStatus.ACTIVE)
        .build();

    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    post.setCurrentPeople(2);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    given(chatRoomRepository.save(any()))
        .willReturn(chatRoom);

    //when
    ChatRoomDto chatRoomDto = chatRoomService.createChatRoom(member.getEmail(), post.getId());

    //then
    assertThat(chatRoomDto.getChatRoomId()).isEqualTo(chatRoom.getId());
    assertThat(chatRoomDto.getName()).isEqualTo(chatRoom.getName());
  }

  @Test
  @DisplayName("채팅방 생성 실패 : 이미 개설된 채팅방인 경우")
  void createChatRoom_FailByAlreadyCreatedChatRoom() {
    //given
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    post.setChatRoomExists(true);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    //when
    GlobalException globalException = Assertions.assertThrows(GlobalException.class,
        () -> chatRoomService.createChatRoom(member.getEmail(), post.getId()));

    //then
    assertThat(ErrorCode.ALREADY_CREATED_CHATROOM.getDescription()).isEqualTo(globalException.getErrorCode().getDescription());
  }

  @Test
  @DisplayName("채팅방 생성 실패 : 모집 인원이 2명 미만인 경우")
  void createChatRoom_FailByCurrentPeopleNotEnough() {
    //given
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    //when
    GlobalException globalException = Assertions.assertThrows(GlobalException.class,
        () -> chatRoomService.createChatRoom(member.getEmail(), post.getId()));

    //then
    assertThat(ErrorCode.NOT_ENOUGH_CURRENT_PEOPLE.getDescription()).isEqualTo(globalException.getErrorCode().getDescription());
  }

  @Test
  @DisplayName("채팅방 입장 성공")
  void enterChatRoom_Success() {
    //given
    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .post(post)
        .name(post.getTitle())
        .status(ChatRoomStatus.ACTIVE)
        .build();

    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    given(chatRoomRepository.findById(anyLong()))
        .willReturn(Optional.of(chatRoom));

    given(chatMemberRepository.save(any()))
        .willReturn(ChatMember.builder()
            .chatRoom(chatRoom)
            .member(member)
            .build());

    //when
    ChatMemberDto chatMemberDto = chatRoomService.enterChatRoom(member.getEmail(), 1L);

    //then
    assertThat(chatRoom.getId()).isEqualTo(chatMemberDto.getChatRoomId());
  }

  @Test
  @DisplayName("채팅방 퇴장 성공")
  void exitChatRoom_Success() {
    //given
    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .post(post)
        .name(post.getTitle())
        .status(ChatRoomStatus.ACTIVE)
        .build();

    ChatMember chatMember = ChatMember.builder()
        .id(1L)
        .member(member)
        .chatRoom(chatRoom)
        .build();

    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    given(chatRoomRepository.findById(anyLong()))
        .willReturn(Optional.of(chatRoom));

    given(chatMemberRepository.findByChatRoomIdAndMemberId(1L, 1L))
        .willReturn(Optional.of(chatMember));

    //when
    chatRoomService.exitChatRoom(member.getEmail(), 1L);

    //then
    verify(chatMemberRepository, times(1)).delete(chatMember);
  }

  @Test
  @DisplayName("내가 참여중인 채팅방 목록 조회")
  void getMyChatRoomList() {
    //given
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .post(post)
        .name(post.getTitle())
        .status(ChatRoomStatus.ACTIVE)
        .build();

    ChatMember chatMember = ChatMember.builder()
        .id(1L)
        .chatRoom(chatRoom)
        .member(member)
        .build();

    given(chatMemberRepository.findAllByMemberId(member.getId()))
        .willReturn(List.of(chatMember));

    //when
    List<ChatRoomDto> chatRoomDtoList = chatRoomService.getMyChatRoomList(member.getEmail());

    //then
    assertFalse(chatRoomDtoList.isEmpty());
  }

  @Test
  @DisplayName("채팅방에 참여중인 멤버 목록 조회 성공")
  void getChatMemberList_success() {
    //given
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .post(post)
        .name(post.getTitle())
        .status(ChatRoomStatus.ACTIVE)
        .build();

    ChatMember chatMember = ChatMember.builder()
        .id(1L)
        .chatRoom(chatRoom)
        .member(member)
        .build();

    given(chatMemberRepository.findAllByChatRoomId(chatRoom.getId()))
        .willReturn(List.of(chatMember));

    //when
    List<ChatMemberDto> chatMemberDtoList = chatRoomService.getChatMemberList(member.getEmail(), chatRoom.getId());

    //then
    assertFalse(chatMemberDtoList.isEmpty());
    assertThat(chatMemberDtoList.get(0).getMemberId()).isEqualTo(member.getId());
  }

  @Test
  @DisplayName("채팅방 메세지 조회")
  void getChatRoomMessage() {
    //given
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    ChatRoom chatRoom = ChatRoom.builder()
        .id(1L)
        .post(post)
        .name(post.getTitle())
        .status(ChatRoomStatus.ACTIVE)
        .build();

    ChatMember chatMember = ChatMember.builder()
        .id(1L)
        .chatRoom(chatRoom)
        .member(member)
        .lastChatId(1L)
        .build();

    given(chatMemberRepository.existsByChatRoomIdAndMemberId(chatRoom.getId(),member.getId()))
        .willReturn(true);

    ChatLastMessageRequest request = new ChatLastMessageRequest(null, null);

    given(chatMessageRepository.findChatRoomMessage(request.lastMessageId(), 1L, Pageable.ofSize(10)))
        .willReturn(new SliceImpl<>(List.of(ChatMessage.builder()
            .chatRoom(chatRoom)
            .chatMember(chatMember)
            .content("test")
            .createdAt(LocalDateTime.now())
            .build())));



    //when
    Slice<ChatMessageDto> chatMessageDtoList = chatRoomService.getChatRoomMessage(member.getEmail(), request, chatRoom.getId());

    //then
   assertThat(chatMessageDtoList.hasContent()).isTrue();
  }
}
