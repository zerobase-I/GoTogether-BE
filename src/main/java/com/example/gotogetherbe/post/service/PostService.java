package com.example.gotogetherbe.post.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.*;

import com.example.gotogetherbe.global.exception.GlobalException;

import com.example.gotogetherbe.global.util.aws.dto.S3ImageDto;
import com.example.gotogetherbe.global.util.aws.entity.PostImage;
import com.example.gotogetherbe.global.util.aws.service.AwsS3Service;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.PostDocument;
import com.example.gotogetherbe.post.dto.PostRequest;
import com.example.gotogetherbe.post.dto.PostResponse;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import com.example.gotogetherbe.post.repository.PostSearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final PostSearchRepository postSearchRepository;
  private final AwsS3Service awsS3Service;


  /**
   * 게시물을 생성하고 그에 대한 정보를 반환
   *
   * @param requestDto 생성할 게시물 정보
   * @param email      게시물을 생성하는 회원의 이메일
   * @param images     업로드할 이미지 파일들
   * @return 생성된 게시물의 정보를 포함한 PostResponse 객체
   */
  @Transactional
  public PostResponse createPost(PostRequest requestDto, String email,
      List<MultipartFile> images) {

    Member member = getMember(email);
    Post post = requestDto.toEntity();

    if (images != null && !images.isEmpty()) {
      Post saveImagePost = uploadS3Image(requestDto, images);
      saveImagePost.getImages().forEach(post::addImage);
    }
    member.addPost(post);

    Post saved = postRepository.save(post);
    postSearchRepository.save(PostDocument.from(saved));

    return PostResponse.fromEntity(saved);
  }

  /**
   * 게시물을 읽고 그에 대한 정보를 반환
   *
   * @param id 읽을 게시물의 ID
   * @return 읽은 게시물의 정보를 포함한 PostResponse 객체
   */
  @Transactional
  public PostResponse readPost(Long id) {
    Post post = getPost(id);

    return PostResponse.fromEntity(post);
  }


  /**
   * 게시물을 업데이트하고 그에 대한 정보를 반환.
   *
   * @param id               업데이트할 게시물의 ID
   * @param requestDto       업데이트할 게시물 정보
   * @param email            게시물을 업데이트하는 회원의 이메일
   * @param newImages        업로드할 이미지 파일들
   * @param imageIdsToDelete 삭제할 이미지의 ID들
   * @return 업데이트된 게시물의 정보를 포함한 PostResponse 객체
   */
  @Transactional
  public PostResponse updatePost(Long id, PostRequest requestDto, String email,
      List<MultipartFile> newImages, List<Long> imageIdsToDelete) {

    Post post = getPost(id);
    Member member = getMember(email);

    validationPost(post, member);

    // PostRequest 로부터 값을 받아와 Post 객체의 상태 변경
    requestDto.updatePostEntity(post);

    // 새로운 이미지가 제공될 경우 해당 이미지 추가
    if (newImages != null && !newImages.isEmpty()) {
      Post updatePost = uploadS3Image(requestDto, newImages);
      updatePost.getImages().forEach(post::addImage);
    }

    // 삭제할 이미지 Id가 제공될 경우 해당 이미지 삭제(특정 이미지만 삭제)
    if (imageIdsToDelete != null && !imageIdsToDelete.isEmpty()) {
      List<PostImage> imagesToDelete = post.getImages().stream()
          .filter(image -> imageIdsToDelete.contains(image.getId()))
          .toList();
      imagesToDelete.forEach(image -> {
        log.info("delete image: {}", image.getFileName());
        awsS3Service.deleteFile(image.getFileName());
        post.removeImage(image);
      });
    }

    return PostResponse.fromEntity(post);
  }


  /**
   * 게시물을 삭제.
   *
   * @param id    삭제할 게시물의 ID
   * @param email 게시물을 삭제하는 회원의 이메일
   */
  public void deletePost(Long id, String email) {
    Post post = getPost(id);
    Member member = getMember(email);

    validationPost(post, member);
    List<PostImage> images = post.getImages();
    images.forEach(image -> awsS3Service.deleteFile(image.getFileName()));

    member.removePost(post);
    postRepository.delete(post);
  }

  public Slice<PostResponse> getMyPostList(Long userId, Long postId, Pageable pageable) {

   return postRepository.getMyPosts(userId, postId, pageable)
        .map(PostResponse::fromEntity);
  }


  /**
   * 게시물에 대한 유효성을 검사. 게시물 작성자와 현재 사용자가 같은지 검사.
   *
   * @param post   유효성을 검사할 게시물
   * @param member 현재 사용자
   */
  private void validationPost(Post post, Member member) {
    if (!post.getMember().getEmail().equals(member.getEmail())) {
      throw new GlobalException(WRITE_NOT_YOURSELF);
    }
  }


  private Post getPost(Long id) {

    return postRepository.findById(id)
        .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));
  }

  private Member getMember(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
  }


  /**
   * 게시물과 이미지 파일을 AWS S3에 업로드하고, 업로드된 게시물에 대한 정보를 반환.
   *
   * @param requestDto     생성할 게시물 정보
   * @param multipartFiles 업로드할 이미지 파일들
   * @return 업로드된 게시물
   */
  private Post uploadS3Image(PostRequest requestDto, List<MultipartFile> multipartFiles) {
    Post post = requestDto.toEntity();

    List<S3ImageDto> list = multipartFiles.stream().filter(image -> image.getSize() > 0)
        .map(awsS3Service::uploadImage).toList();
    List<PostImage> imageList = list.stream().map(S3ImageDto::toEntity).toList();

    imageList.forEach(post::addImage);
    return post;
  }


}
