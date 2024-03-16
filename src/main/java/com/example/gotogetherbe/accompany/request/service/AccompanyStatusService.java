package com.example.gotogetherbe.accompany.request.service;

import static com.example.gotogetherbe.accompany.request.type.AccompanyStatus.PARTICIPATING;
import static com.example.gotogetherbe.accompany.request.type.AccompanyStatus.REJECTED;
import static com.example.gotogetherbe.accompany.request.type.AccompanyStatus.WAITING;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.ACCOMPANY_REQUEST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_ACCOMPANY_REQUEST;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_MISMATCH;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.accompany.request.dto.AccompanyStatusDto;
import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.repository.AccompanyStatusRepository;
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
public class AccompanyStatusService {

    private final AccompanyStatusRepository accompanyStatusRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private static final String URL_PREFIX = "/api/accompany/request";

    /**
     * 동행 요청 보내기
     *
     * @param email  사용자 이메일
     * @param postId 게시물 ID
     * @return 동행 요청 정보
     */
    @Transactional
    public AccompanyStatusDto sendAccompanyRequest(String email, Long postId) {
        Member requestMember = getMemberByEmail(email);
        Post post = getOrElseThrow(postId);

        checkDuplication(requestMember.getId(), post.getId());

        Accompany accompany = makeAccompanyStatus(requestMember, post);

        return AccompanyStatusDto.from(accompanyStatusRepository.save(accompany));
    }

    /**
     * 보낸 동행 요청 조회
     * @param email 사용자 이메일
     * @return 보낸 동행 요청 목록
     */
    public List<AccompanyStatusDto> getSentAccompanyRequests(String email) {
        Member member = getMemberByEmail(email);
        List<Accompany> sentRequests = accompanyStatusRepository
            .findAllByRequestMemberIdAndStatusOrderByCreatedAtDesc(member.getId(), WAITING);

        return convert(sentRequests);
    }

    /**
     * 받은 동행 요청 조회
     * @param email 사용자 이메일
     * @return 받은 동행 요청 목록
     */
    public List<AccompanyStatusDto> getReceivedAccompanyRequests(String email) {
        Member member = getMemberByEmail(email);
        List<Accompany> receivedRequests = accompanyStatusRepository
            .findAllByRequestedMemberIdAndStatusOrderByCreatedAtDesc(member.getId(), WAITING);

        return convert(receivedRequests);
    }

    /**
     * 동행 요청 승인
     * @param email    사용자 이메일
     * @param requestId 요청 ID
     * @return 동행 요청 정보
     */
    @Transactional
    public AccompanyStatusDto approveAccompanyRequest(String email, Long requestId) {
        Accompany request = getAccompanyRequest(email, requestId);
        request.updateRequestStatus(PARTICIPATING);

        Post post = getOrElseThrow(request.getPost().getId());
        post.updateCurrentPeople();
        postRepository.save(post);

        return AccompanyStatusDto.from(accompanyStatusRepository.save(request));
    }

    /**
     * 동행 요청 거절
     * @param email    사용자 이메일
     * @param requestId 요청 ID
     * @return 동행 요청 정보
     */
    @Transactional
    public AccompanyStatusDto rejectAccompanyRequest(String email, Long requestId) {
        Accompany request = getAccompanyRequest(email, requestId);
        request.updateRequestStatus(REJECTED);

        return AccompanyStatusDto.from(accompanyStatusRepository.save(request));
    }

    /**
     * 동행 요청 취소
     * @param requestId 요청 ID
     */
    @Transactional
    public void cancelAccompanyRequest(Long requestId) {
        Accompany accompanyRequest = accompanyStatusRepository.findById(requestId)
            .orElseThrow(() -> new GlobalException(ACCOMPANY_REQUEST_NOT_FOUND));
        accompanyStatusRepository.delete(accompanyRequest);
    }

    private Post getOrElseThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));
    }

    private static Accompany makeAccompanyStatus(Member requestMember, Post post) {
        return Accompany.builder()
            .requestMember(requestMember)
            .requestedMember(post.getMember())
            .post(post)
            .status(WAITING)
            .build();
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }

    /**
     * 해당하는 postId에 대해 requestMember가 동일한 요청이 존재하는지 체크(중복요청 확인)
     */
    private void checkDuplication(Long requestMemberId, Long postId) {
        if (accompanyStatusRepository.existsByRequestMember_IdAndPost_Id(
            requestMemberId, postId)
        ) {
            throw new GlobalException(DUPLICATE_ACCOMPANY_REQUEST);
        }
    }

    private List<AccompanyStatusDto> convert(List<Accompany> requests) {
        return requests.stream().map(AccompanyStatusDto::from).collect(Collectors.toList());
    }

    private Accompany getAccompanyRequest(String email, Long requestId) {
        Accompany accompanyRequest = accompanyStatusRepository
            .findById(requestId)
            .orElseThrow(() -> new GlobalException(ACCOMPANY_REQUEST_NOT_FOUND));

        // 승인 또는 거절하려는 사용자와 accompanyRequest에 저장된 요청을 받은 사용자가 일치하지 않으면 예외 발생
        if (!Objects.equals(accompanyRequest.getRequestedMember().getEmail(), email)) {
            throw new GlobalException(USER_MISMATCH);
        }

        return accompanyRequest;
    }

}
