package com.example.gotogetherbe.accompany.request.service;

import static com.example.gotogetherbe.accompany.request.type.RequestStatus.APPROVED;
import static com.example.gotogetherbe.accompany.request.type.RequestStatus.REJECTED;
import static com.example.gotogetherbe.accompany.request.type.RequestStatus.WAITING;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.ACCOMPANY_REQUEST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_ACCOMPANY_REQUEST;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_AUTHOR_MISMATCH;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_MISMATCH;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestDto;
import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestSendDto;
import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import com.example.gotogetherbe.accompany.request.repository.AccompanyRequestRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccompanyRequestService {

    private final AccompanyRequestRepository accompanyRequestRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public AccompanyRequestDto sendAccompanyRequest(
        String email,
        AccompanyRequestSendDto accompanyRequestSendDto
    ) {
        Member requestMember = getMemberByEmail(email);

        // 동행 요청을 받는 사용자가 게시글 작성자와 같은지 확인
        Post post = postRepository.findByMemberIdAndPostId(
                accompanyRequestSendDto.getRequestedMemberId(), accompanyRequestSendDto.getPostId())
            .orElseThrow(() -> new GlobalException(POST_AUTHOR_MISMATCH));

        // 중복 요청 확인
        checkDuplication(requestMember, post.getMember(), post);
        AccompanyRequest accompanyRequest = AccompanyRequest.builder()
            .requestMember(requestMember)
            .requestedMember(post.getMember())
            .post(post)
            .requestStatus(WAITING)
            .build();

        return AccompanyRequestDto.from(accompanyRequestRepository.save(accompanyRequest));
    }

    public List<AccompanyRequestDto> getSentAccompanyRequests(String email) {
        List<AccompanyRequest> sentRequests =
            accompanyRequestRepository.findAllByRequestMember_Email(email);

        return convert(sentRequests);
    }

    public List<AccompanyRequestDto> getReceivedAccompanyRequests(String email) {
        List<AccompanyRequest> receivedRequests =
            accompanyRequestRepository.findAllByRequestedMember_Email(email);

        return convert(receivedRequests);
    }

    @Transactional
    public AccompanyRequestDto approveAccompanyRequest(String email, Long requestId) {
        AccompanyRequest request = getAccompanyRequest(email, requestId);
        request.setRequestStatus(APPROVED);

        return AccompanyRequestDto.from(accompanyRequestRepository.save(request));
    }

    @Transactional
    public AccompanyRequestDto rejectAccompanyRequest(String email, Long requestId) {
        AccompanyRequest request = getAccompanyRequest(email, requestId);
        request.setRequestStatus(REJECTED);

        return AccompanyRequestDto.from(accompanyRequestRepository.save(request));
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }

    /**
     * 동일한 postId에 대해 중복되는 requestedMember & requestMember인지 체크(중복요청인지 확인)
     */
    private void checkDuplication(Member requestMember, Member requestedMember, Post post) {
        if (accompanyRequestRepository.existsByRequestMemberAndRequestedMemberAndPost(
            requestMember, requestedMember, post)) {
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
