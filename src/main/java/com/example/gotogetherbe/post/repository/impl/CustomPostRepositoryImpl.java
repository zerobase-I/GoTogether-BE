package com.example.gotogetherbe.post.repository.impl;

import static com.example.gotogetherbe.post.entity.QPost.post;

import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.CustomPostRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;


@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {
  private final JPAQueryFactory jpa;

  /**
   * 제목에 특정 텍스트가 포함된, 게시물의 ID가 특정 값보다 작은 게시물들을 검색하고,
   * 그 결과를 Slice 로 반환. 검색된 게시물들은 ID의 내림차순으로 정렬.
   * @param postId 게시물의 ID가 이 값보다 작은 게시물을 검색.
   * @param title 게시물의 제목에 이 텍스트가 포함된 게시물을 검색.
   * @param pageable 페이지네이션 정보.
   * @return 검색된 게시물들과 페이지 정보를 가진 Slice 를 반환.sdsd
   */
  @Override
  public Slice<Post> searchByTitle(Long postId, String title, Pageable pageable) {
    List<Post> postList = jpa
        .selectFrom(post)
        .where(
            ltPostId(postId)
            , post.title.contains(title)
        )
        .orderBy(
            post.id.desc()
        )
        .limit(pageable.getPageSize() + 1)
        .fetch();

    // 검색된 결과를 바탕으로 Slice 를 생성하고 반환.
    // 이때, 검색된 게시물의 수가 페이지 크기보다 크면 다음 페이지가 있는 것으로 판단.
    return checkLastPage(pageable, postList);
  }


  /**
   * postId 보다 작은 ID를 가진 게시물을 검색하는 조건을 생성
   * 만약 postId가 null 이나 0이면 null 을 반환하고, 그렇지 않으면 post.id.lt(postId)를 반환.
   *
   * @param postId 게시물의 ID가 이 값보다 작은 게시물을 검색하는 조건을 생성.
   * @return 조건을 표현하는 BooleanExpression 을 반환. postId가 null 이나 0인 경우 null 을 반환.
   */
  private BooleanExpression ltPostId(Long postId){
    if (postId == null || postId == 0L) {
      return null;
    }
    return post.id.lt(postId);
  }


  /**
   * 무한 스크롤 - 검색된 게시물의 수를 바탕으로 다음 페이지가 있는지 확인하고, Slice 를 생성.
   * @param pageable 페이지네이션 정보를 가지고 있습니다.
   * @param results 검색된 게시물들의 목록입니다.
   * @return 검색된 게시물들과 페이지 정보를 가진 Slice를 반환합니다.
   */
  private Slice<Post> checkLastPage(Pageable pageable, List<Post> results){
    boolean hasNext = false;

    // 검색된 게시물의 수가 페이지 크기보다 크면, 다음 페이지가 있다고 판단하고 hasNext 를 true 로 설정.
    if (results.size() > pageable.getPageSize()) {
      hasNext = true;
      //마지막 게시물을 제거하여 현재 페이지의 게시물만 남김.
      results.remove(results.size() - 1);
    }

    // 검색된 게시물들과 페이지 정보, 다음 페이지 존재 여부를 바탕으로 Slice 를 생성하고 반환.
    return new SliceImpl<>(results, pageable, hasNext);
  }
}
