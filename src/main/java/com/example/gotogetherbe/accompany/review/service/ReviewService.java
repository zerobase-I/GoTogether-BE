package com.example.gotogetherbe.accompany.review.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.CHATROOM_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_REVIEW;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.NOT_SAME_ACCOMPANY_MEMBER;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.UNCOMPLETED_ACCOMPANY;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;
import static com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus.COMPLETED;

import com.example.gotogetherbe.accompany.review.dto.ReviewDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.accompany.review.repository.ReviewRepository;
import com.example.gotogetherbe.chat.entity.ChatRoom;
import com.example.gotogetherbe.chat.repository.ChatMemberRepository;
import com.example.gotogetherbe.chat.repository.ChatRoomRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MemberAssessmentService travelScoreService;

    /**
     * 리뷰 작성
     *
     * @param email      로그인한 사용자 이메일
     * @param reviewWriteDtos 리뷰 작성 정보
     * @return 작성된 리뷰 정보
     */
    @Transactional
    public List<ReviewDto> writeReview(String email, List<ReviewWriteDto> reviewWriteDtos) {
        Member reviewer = getMemberByEmail(email);

        Post post = postRepository.findById(reviewWriteDtos.get(0).getPostId())
            .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));

        List<Review> reviews = getReviews(reviewWriteDtos, post, reviewer);

        List<Review> savedReviews = reviewRepository.saveAll(reviews);

        for (Review saved : savedReviews) {
            travelScoreService.updateMemberAssessment(saved.getTargetMember(), saved.getScore());
        }

        return savedReviews.stream().map(ReviewDto::from).toList();
    }

    /**
     * 리뷰 작성이 가능한 조건인지 확인(완료된 동행, 동행 참여자 확인)
     *
     * @param post         동행 게시글
     * @param reviewer     리뷰 작성자
     * @param targetMember 리뷰 대상자
     */
    private void checkAccompanyCondition(Post post, Member reviewer, Member targetMember) {
        // 리뷰어, 타겟 멤버가 동일한 동행에 참여한 멤버인지 확인
        ChatRoom chatRoom = chatRoomRepository.findByPostId(post.getId())
            .orElseThrow(() -> new GlobalException(CHATROOM_NOT_FOUND));

        if (!chatMemberRepository
            .isUsersInSameChatRoom(reviewer.getId(), targetMember.getId(), chatRoom.getId())
        ) {
            throw new GlobalException(NOT_SAME_ACCOMPANY_MEMBER);
        }

        // 동행이 완료된 상태인지 확인
        if (post.getRecruitmentStatus() != COMPLETED) {
            throw new GlobalException(UNCOMPLETED_ACCOMPANY);
        }
    }

    /**
     * 로그인 한 사용자의 리뷰 조회
     *
     * @param email 로그인한 사용자 이메일
     * @return 로그인한 사용자의 리뷰 리스트
     */
    public List<ReviewDto> getMyReviews(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
        List<Review> reviews = reviewRepository.findAllByTargetMember_Id(member.getId());

        return reviews.stream().map(ReviewDto::from).toList();
    }

    /**
     * 특정 사용자의 리뷰 조회
     *
     * @param memberId 사용자 id
     * @return 특정 사용자의 리뷰 리스트
     */
    public List<ReviewDto> getReviews(Long memberId) {
        List<Review> reviews = reviewRepository.findAllByTargetMember_Id(memberId);

        return reviews.stream().map(ReviewDto::from).toList();
    }

    /**
     * 리뷰 작성 정보로 리뷰 객체 생성
     *
     * @param post           참여한 동행 게시글
     * @param reviewWriteDtos 리뷰 작성 정보
     * @param reviewer       리뷰 작성자
     * @return 작성된 리뷰 정보
     */
    private List<Review> getReviews(List<ReviewWriteDto> reviewWriteDtos, Post post, Member reviewer) {
        List<Review> reviews = new ArrayList<>();

        for (ReviewWriteDto reviewWriteDto : reviewWriteDtos) {

            Member targetMember = memberRepository.findById(reviewWriteDto.getTargetMemberId())
                .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));

            // 리뷰 작성이 가능한 조건인지 확인(완료된 동행, 동행 참여자 확인)
            checkAccompanyCondition(post, reviewer, targetMember);

            // 리뷰 중복 확인
            checkDuplication(reviewer, targetMember, post);

            Review review = Review.builder()
                .reviewer(reviewer)
                .targetMember(targetMember)
                .post(post)
                .score(reviewWriteDto.getScore())
                .punctuality(reviewWriteDto.isPunctuality())
                .responsiveness(reviewWriteDto.isResponsiveness())
                .photography(reviewWriteDto.isPhotography())
                .manner(reviewWriteDto.isManner())
                .navigation(reviewWriteDto.isNavigation())
                .humor(reviewWriteDto.isHumor())
                .adaptability(reviewWriteDto.isAdaptability())
                .build();

            reviews.add(review);
        }
        return reviews;
    }

    /**
     * 리뷰 중복 확인(리뷰어, 타겟 멤버, postId 세가지 항목 동시 일치 여부)
     *
     * @param reviewer     리뷰 작성자
     * @param targetMember 리뷰 대상자
     * @param post         게시글 중복이 발생하면 GlobalException 발생
     */
    private void checkDuplication(Member reviewer, Member targetMember, Post post) {
        if (reviewRepository.existsByReviewerAndTargetMemberAndPost(reviewer, targetMember, post)) {
            throw new GlobalException(DUPLICATE_REVIEW);
        }
    }

    /**
     * 이메일로 사용자 조회
     *
     * @param email 사용자 이메일
     * @return member
     */
    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }
}
