package com.example.gotogetherbe.post.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.*;

import com.example.gotogetherbe.global.exception.GlobalException;

import com.example.gotogetherbe.global.util.aws.dto.S3ImageDto;
import com.example.gotogetherbe.global.util.aws.entity.PostImage;
import com.example.gotogetherbe.global.util.aws.service.AwsS3Service;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.dto.PostRequest;
import com.example.gotogetherbe.post.dto.PostResponse;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final AwsS3Service awsS3Service;

  @Transactional
  public PostResponse createPost(PostRequest requestDto, String email,
      List<MultipartFile> files) {
    log.info("[createPost] start");

    Member member = getMember(email);

    Post post = uploadS3Image(requestDto, files);

    member.addPost(post);

    return PostResponse.fromEntity(postRepository.save(post));
  }

  @Transactional
  public PostResponse readPost(Long id){
    Post post = getPost(id);

    return PostResponse.fromEntity(post);
  }
  private Post getPost(Long id) {

    return postRepository.findById(id)
        .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));
  }

  private Member getMember(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
  }
  private Post uploadS3Image(PostRequest requestDto, List<MultipartFile> multipartFiles) {
    Post post = requestDto.toEntity();

    List<S3ImageDto> list = multipartFiles.stream().map(awsS3Service::uploadPostImage).toList();
    List<PostImage> imageList = list.stream().map(S3ImageDto::toEntity).toList();

    imageList.forEach(post::addImage);
    return post;
  }
}
