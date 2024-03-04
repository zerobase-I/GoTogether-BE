package com.example.gotogetherbe.accompany.request.service;

import static com.example.gotogetherbe.accompany.request.type.RequestStatus.APPROVED;
import static com.example.gotogetherbe.accompany.request.type.RequestStatus.REJECTED;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.ACCOMPANY_REQUEST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_ACCOMPANY_REQUEST;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_MISMATCH;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestDto;
import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestSendDto;
import com.example.gotogetherbe.accompany.request.dto.RequestConfirmDto;
import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import com.example.gotogetherbe.accompany.request.repository.AccompanyRequestRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.member.repository.MemberRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class AccompanyRequestServiceImpl implements AccompanyRequestService {

    private final AccompanyRequestRepository accompanyRequestRepository;
    private final MemberRepository memberRepository;

    @Override
    public boolean sendAccompanyRequest(AccompanyRequestSendDto accompanyRequestSendDto) {
        existMember(accompanyRequestSendDto.getRequestMemberId());
        existMember(accompanyRequestSendDto.getRequestedMemberId());

        checkDuplication(accompanyRequestSendDto);
        AccompanyRequest accompanyRequest = accompanyRequestSendDto.toEntity();

        return !ObjectUtils.isEmpty(accompanyRequestRepository.save(accompanyRequest));
    }

    /**
     * 동일한 postId에 대해 중복되는 requestedMemberId & requestMemberId 체크(중복요청인지 확인)
     */
    private void checkDuplication(AccompanyRequestSendDto accompanyRequestSendDto) {
        //TODO postRepository에서 postId에 대한 존재 여부 확인

        // 중복된 요청이 존재하는 경우 예외 발생
        if (accompanyRequestRepository.existByRequestMemberIdAndRequestedMemberIdAndPostId(
            accompanyRequestSendDto.getRequestMemberId(),
            accompanyRequestSendDto.getRequestedMemberId(),
            accompanyRequestSendDto.getPostId()
        )) {
            throw new GlobalException(DUPLICATE_ACCOMPANY_REQUEST);
        }
    }

    private void existMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new GlobalException(USER_NOT_FOUND);
        }
    }

    @Override
    public List<AccompanyRequestDto> getSentAccompanyRequests(Long memberId) {
        existMember(memberId);
        List<AccompanyRequest> sentRequests =
            accompanyRequestRepository.findAllByRequestMemberId(memberId);

        return convert(sentRequests);
    }

    @Override
    public List<AccompanyRequestDto> getReceivedAccompanyRequests(Long memberId) {
        existMember(memberId);
        List<AccompanyRequest> receivedRequests =
            accompanyRequestRepository.findAllByRequestedMemberId(memberId);

        return convert(receivedRequests);
    }

    private List<AccompanyRequestDto> convert(List<AccompanyRequest> requests) {
        return requests.stream().map(AccompanyRequestDto::from).collect(Collectors.toList());
    }

    @Override
    public boolean approveAccompanyRequest(RequestConfirmDto requestConfirmDto) {
        AccompanyRequest request = getAccompanyRequest(requestConfirmDto);
        request.setRequestStatus(APPROVED);

        return !ObjectUtils.isEmpty(accompanyRequestRepository.save(request));
    }

    @Override
    public boolean rejectAccompanyRequest(RequestConfirmDto requestConfirmDto) {
        AccompanyRequest request = getAccompanyRequest(requestConfirmDto);
        request.setRequestStatus(REJECTED);

        return !ObjectUtils.isEmpty(accompanyRequestRepository.save(request));
    }

    private AccompanyRequest getAccompanyRequest(RequestConfirmDto requestConfirmDto) {
        AccompanyRequest accompanyRequest = accompanyRequestRepository
            .findById(requestConfirmDto.getRequestId())
            .orElseThrow(() -> new GlobalException(ACCOMPANY_REQUEST_NOT_FOUND));

        // 승인 또는 거절하려는 사용자와 accompanyRequest에 저장된 요청을 받은 사용자가 일치하지 않으면 예외 발생
        if (!Objects.equals(requestConfirmDto.getMemberId(),
            accompanyRequest.getRequestedMemberId())) {
            throw new GlobalException(USER_MISMATCH);
        }

        return accompanyRequest;
    }
}
