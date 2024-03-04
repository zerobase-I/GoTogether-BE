package com.example.gotogetherbe.accompany.review.service;

import com.example.gotogetherbe.accompany.review.dto.ReviewDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import java.util.List;

public interface ReviewService {

    boolean writeReview(ReviewWriteDto reviewDto);

    List<ReviewDto> getReviews(Long memberId);

}
