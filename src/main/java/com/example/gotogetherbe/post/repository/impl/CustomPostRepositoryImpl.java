package com.example.gotogetherbe.post.repository.impl;

import static com.example.gotogetherbe.post.entity.QPost.post;


import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.PostDocument;
import com.example.gotogetherbe.post.repository.CustomPostRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

@RequiredArgsConstructor
@Slf4j
public class CustomPostRepositoryImpl implements CustomPostRepository {

  private final JPAQueryFactory jpa;
  private final ElasticsearchOperations elasticsearchOperations;

  /**
   * 제목에 특정 텍스트가 포함된, 게시물의 ID가 특정 값보다 작은 게시물들을 검색하고, 그 결과를 Slice 로 반환. 검색된 게시물들은 ID의 내림차순으로 정렬.
   *
   * @param postId   게시물의 ID가 이 값보다 작은 게시물을 검색.
   * @param title    게시물의 제목에 이 텍스트가 포함된 게시물을 검색.
   * @param pageable 페이지네이션 정보.
   * @return 검색된 게시물들과 페이지 정보를 가진 Slice 를 반환.
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

  @Override
  public Slice<Post> getMyPosts(Long memberId, Long postId, Pageable pageable) {
    List<Post> postList = jpa
        .selectFrom(post)
        .where(
            post.member.id.eq(memberId), // 회원 ID가 일치하는 조건
            ltPostId(postId) // 게시물 ID가 주어진 postId 값보다 작은 조건
        )
        .orderBy(post.id.desc()) // 게시물 ID 내림차순 정렬
        .limit(pageable.getPageSize() + 1) // 페이지네이션 처리를 위해 요청된 페이지 사이즈보다 1 더 큰 수의 게시물을 요청
        .fetch();

    // 검색된 결과를 바탕으로 Slice 생성 및 반환
    return checkLastPage(pageable, postList);
  }


  /**
   * 특정 키워드들이 포함된 게시물들을 검색하고, 그 결과를 Slice 로 반환. 검색은 Elasticsearch 를 사용하며,
   * 여행 국가, 여행 도시, 성별, 카테고리 필드에서의 매칭을 시도.
   *
   * @param keywords 검색할 키워드 목록.
   * @param pageable 페이지네이션 정보.
   * @return 검색된 게시물들과 페이지 정보를 가진 Slice 를 반환.
   */
  @Override
  public Slice<PostDocument> searchByKeyword(List<String> keywords,
      LocalDateTime userStartDate, LocalDateTime userEndDate,
      Pageable pageable) {

    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    for (String keyword : keywords) {
      boolQueryBuilder.must(QueryBuilders.multiMatchQuery(keyword)
          .field("travelCountry")
          .field("travelCity")
          .field("gender")
          .field("category")
      );
    }

    // 날짜 관련 조건 추가
    addDateRangeConditions(boolQueryBuilder, userStartDate, userEndDate);

    SearchHits<PostDocument> searchHits = elasticsearchOperations.search(
        new NativeSearchQueryBuilder()
            .withQuery(boolQueryBuilder)
            .withPageable(pageable)
            .build(),
        PostDocument.class
    );

    List<PostDocument> searchResults = searchHits.stream()
        .map(SearchHit::getContent).toList();

    return checkLastPage(pageable, searchResults);
  }

  /**
   * postId 보다 작은 ID를 가진 게시물을 검색하는 조건을 생성 만약 postId가 null 이나 0이면 null 을 반환하고, 그렇지 않으면
   * post.id.lt(postId)를 반환.
   *
   * @param postId 게시물의 ID가 이 값보다 작은 게시물을 검색하는 조건을 생성.
   * @return 조건을 표현하는 BooleanExpression 을 반환. postId가 null 이나 0인 경우 null 을 반환.
   */
  private BooleanExpression ltPostId(Long postId) {
    if (postId == null || postId == 0L) {
      return null;
    }
    return post.id.lt(postId);
  }


  /**
   * 무한 스크롤 - 검색된 게시물의 수를 바탕으로 다음 페이지가 있는지 확인하고, Slice 를 생성.
   *
   * @param pageable 페이지네이션 정보
   * @param results  검색된 게시물들의 목록입니다.
   * @return 검색된 게시물들과 페이지 정보를 가진 Slice 를 반환.
   */
  private <T> Slice<T> checkLastPage(Pageable pageable, List<T> results) {
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

  /**
   * 사용자가 선택한 날짜 범위에 따라 날짜 조건을 추가하는 메서드
   *
   * @param boolQueryBuilder 날짜 조건을 추가할 BoolQueryBuilder
   * @param userStartDate   사용자가 선택한 검색 시작 날짜
   * @param userEndDate     사용자가 선택한 검색 종료 날짜
   */
  private void addDateRangeConditions(BoolQueryBuilder boolQueryBuilder,
      LocalDateTime userStartDate, LocalDateTime userEndDate) {
    // 날짜 조건을 must 쿼리로 변경
    BoolQueryBuilder dateRangeQuery = QueryBuilders.boolQuery();

    if (userStartDate != null && userEndDate != null) {
      // LocalDateTime 을 ISO-8601 형식의 문자열로 변환 (밀리초 단위 포함)
      String formattedStartDate = userStartDate.toLocalDate().atStartOfDay()
          .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".000";
      String formattedEndDate = userEndDate.toLocalDate().atStartOfDay()
          .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".000";

      // 시작 날짜가 사용자가 지정한 범위 안에 있는 경우
      dateRangeQuery.should(
          QueryBuilders.rangeQuery("startDate")
              .gte(formattedStartDate)
              .lte(formattedEndDate)
      );

      // 종료 날짜가 사용자가 지정한 범위 안에 있는 경우
      dateRangeQuery.should(
          QueryBuilders.rangeQuery("endDate")
              .gte(formattedStartDate)
              .lte(formattedEndDate)
      );

      // 시작 날짜가 사용자가 지정한 범위 이전이고 종료 날짜가 그 범위 이후인 경우
      dateRangeQuery.should(
          QueryBuilders.boolQuery()
              .must(QueryBuilders.rangeQuery("startDate").lt(formattedStartDate))
              .must(QueryBuilders.rangeQuery("endDate").gt(formattedEndDate))
      );
    }

    // 날짜 조건을 must 쿼리의 일부로 추가
    if (dateRangeQuery.hasClauses()) {
      boolQueryBuilder.must(dateRangeQuery);
    }
  }

}
