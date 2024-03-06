package com.example.gotogetherbe.accompany.request.service;

import static com.example.gotogetherbe.accompany.request.type.RequestStatus.APPROVED;
import static com.example.gotogetherbe.accompany.request.type.RequestStatus.REJECTED;
import static com.example.gotogetherbe.accompany.request.type.RequestStatus.WAITING;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.ACCOMPANY_REQUEST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_ACCOMPANY_REQUEST;
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

        Member requestedMember = memberRepository
            .findById(accompanyRequestSendDto.getRequestedMemberId())
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));

        // TODO post 조회
        Post post = postRepository.findById(accompanyRequestSendDto.getPostId())
            .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));

        checkDuplication(requestMember, requestedMember, post); // post, requestedMember로 파라미터 변경
        AccompanyRequest accompanyRequest = AccompanyRequest.builder()
            .requestMember(requestMember)
            .requestedMember(requestedMember)
            .post(post)
            .requestStatus(WAITING)
            .build();

        return AccompanyRequestDto.from(accompanyRequestRepository.save(accompanyRequest));
    }

    public List<AccompanyRequestDto> getSentAccompanyRequests(String email) {
        Member member = getMemberByEmail(email);

        List<AccompanyRequest> sentRequests =
            accompanyRequestRepository.findAllByRequestMember(member);

        return convert(sentRequests);
    }

    public List<AccompanyRequestDto> getReceivedAccompanyRequests(String email) {
        Member member = getMemberByEmail(email);

        List<AccompanyRequest> receivedRequests =
            accompanyRequestRepository.findAllByRequestedMember(member);

        return convert(receivedRequests);
    }

    @Transactional
    public AccompanyRequestDto approveAccompanyRequest(String email, Long requestId) {
        Member member = getMemberByEmail(email);

        AccompanyRequest request = getAccompanyRequest(member, requestId);
        request.setRequestStatus(APPROVED);

        return AccompanyRequestDto.from(accompanyRequestRepository.save(request));
    }

    @Transactional
    public AccompanyRequestDto rejectAccompanyRequest(String email, Long requestId) {
        Member member = getMemberByEmail(email);

        AccompanyRequest request = getAccompanyRequest(member, requestId);
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
    private void checkDuplication(
        Member requestMember,
        Member requestedMember,
        Post post
    ) {
        //TODO postRepository에서 postId에 대한 존재 여부 확인

        // 중복된 요청이 존재하는 경우 예외 발생
        if (accompanyRequestRepository.existsByRequestMemberAndRequestedMemberAndPost(
            requestMember,
            requestedMember,
            post
        )) {
            throw new GlobalException(DUPLICATE_ACCOMPANY_REQUEST);
        }
    }

    private List<AccompanyRequestDto> convert(List<AccompanyRequest> requests) {
        return requests.stream().map(AccompanyRequestDto::from).collect(Collectors.toList());
    }

    private AccompanyRequest getAccompanyRequest(Member member, Long requestId) {
        AccompanyRequest accompanyRequest = accompanyRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new GlobalException(ACCOMPANY_REQUEST_NOT_FOUND));

        // 승인 또는 거절하려는 사용자와 accompanyRequest에 저장된 요청을 받은 사용자가 일치하지 않으면 예외 발생
        if (!Objects.equals(accompanyRequest.getRequestedMember(), member)) {
            throw new GlobalException(USER_MISMATCH);
        }

        return accompanyRequest;
    }
}
