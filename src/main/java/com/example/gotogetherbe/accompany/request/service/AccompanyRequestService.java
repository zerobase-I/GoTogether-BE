package com.example.gotogetherbe.accompany.request.service;

import static com.example.gotogetherbe.accompany.request.type.RequestStatus.APPROVED;
import static com.example.gotogetherbe.accompany.request.type.RequestStatus.REJECTED;
import static com.example.gotogetherbe.accompany.request.type.RequestStatus.WAITING;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.ACCOMPANY_REQUEST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_ACCOMPANY_REQUEST;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_AUTHOR_MISMATCH;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_MISMATCH;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestDto;
import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestSendDto;
import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import com.example.gotogetherbe.accompany.request.repository.AccompanyRequestRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.notification.dto.NotificationInfoDto;
import com.example.gotogetherbe.notification.type.NotificationStatus;
import com.example.gotogetherbe.notification.type.NotificationType;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccompanyRequestService {

    private final AccompanyRequestRepository accompanyRequestRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final String URL_PREFIX = "/api/accompany/request";

    /**
     * 동행 요청 보내기
     * @param email 사용자 이메일
     * @param accompanyRequestSendDto 동행 요청 정보
     * @return 동행 요청 정보
     */
    @Transactional
    public AccompanyRequestDto sendAccompanyRequest(
        String email, AccompanyRequestSendDto accompanyRequestSendDto
    ) {
        Member requestMember = getMemberByEmail(email);

        // 동행 요청을 받는 사용자가 게시글 작성자와 같은지 확인
        Post post = postRepository.findByMemberIdAndId(
                accompanyRequestSendDto.getRequestedMemberId(), accompanyRequestSendDto.getPostId())
            .orElseThrow(() -> new GlobalException(POST_AUTHOR_MISMATCH));

        // 중복 요청 확인
        checkDuplication(requestMember.getId(), post.getMember().getId(), post.getId());
        AccompanyRequest accompanyRequest = AccompanyRequest.builder()
            .requestMember(requestMember)
            .requestedMember(post.getMember())
            .post(post)
            .requestStatus(WAITING)
            .build();

        // 이벤트 발행
        NotificationInfoDto notificationInfo = NotificationInfoDto.builder()
            .member(requestedMember)
            .postId(post.getId())
            .status(NotificationStatus.UNREAD)
            .type(NotificationType.ACCOMPANY_REQUEST)
            .url(URL_PREFIX + "/receive") // 알림 클릭시 이동할 url
            .build();
        applicationEventPublisher.publishEvent(notificationInfo);

        return AccompanyRequestDto.from(accompanyRequestRepository.save(accompanyRequest));
    }

    public List<AccompanyRequestDto> getSentAccompanyRequests(String email) {
        Member member = getMemberByEmail(email);
        List<AccompanyRequest> sentRequests = accompanyRequestRepository
            .findAllByRequestMemberIdOrderByCreatedAtDesc(member.getId());

        return convert(sentRequests);
    }

    public List<AccompanyRequestDto> getReceivedAccompanyRequests(String email) {
        Member member = getMemberByEmail(email);
        List<AccompanyRequest> receivedRequests = accompanyRequestRepository
            .findAllByRequestedMemberIdOrderByCreatedAtDesc(member.getId());

        return convert(receivedRequests);
    }

    @Transactional
    public AccompanyRequestDto approveAccompanyRequest(String email, Long requestId) {
        AccompanyRequest request = getAccompanyRequest(email, requestId);
        request.updateRequestStatus(APPROVED);

        Post post = postRepository.findById(request.getPost().getId())
            .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));

        post.updateCurrentPeople();
        postRepository.save(post);

        return AccompanyRequestDto.from(accompanyRequestRepository.save(request));
    }

    @Transactional
    public AccompanyRequestDto rejectAccompanyRequest(String email, Long requestId) {
        AccompanyRequest request = getAccompanyRequest(email, requestId);
        request.updateRequestStatus(REJECTED);

        return AccompanyRequestDto.from(accompanyRequestRepository.save(request));
    }

    /**
     * 동행 요청 취소
     * @param requestId 요청 ID
     */
    @Transactional
    public void cancelAccompanyRequest(Long requestId) {
        AccompanyRequest accompanyRequest = accompanyRequestRepository.findById(requestId)
            .orElseThrow(() -> new GlobalException(ACCOMPANY_REQUEST_NOT_FOUND));

        accompanyRequestRepository.delete(accompanyRequest);
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }

    /**
     * 동일한 postId에 대해 중복되는 requestedMember & requestMember인지 체크(중복요청인지 확인)
     */
    private void checkDuplication(Long requestMemberId, Long requestedMemberId, Long postId) {
        if (accompanyRequestRepository.existsByRequestedMember_IdAndRequestedMember_IdAndPost_Id(
            requestMemberId, requestedMemberId, postId)
        ) {
            throw new GlobalException(DUPLICATE_ACCOMPANY_REQUEST);
        }
    }

    private List<AccompanyRequestDto> convert(List<AccompanyRequest> requests) {
        return requests.stream().map(AccompanyRequestDto::from).collect(Collectors.toList());
    }

    private AccompanyRequest getAccompanyRequest(String email, Long requestId) {
        AccompanyRequest accompanyRequest = accompanyRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new GlobalException(ACCOMPANY_REQUEST_NOT_FOUND));

        // 승인 또는 거절하려는 사용자와 accompanyRequest에 저장된 요청을 받은 사용자가 일치하지 않으면 예외 발생
        if (!Objects.equals(accompanyRequest.getRequestedMember().getEmail(), email)) {
            throw new GlobalException(USER_MISMATCH);
        }

        return accompanyRequest;
    }

}
