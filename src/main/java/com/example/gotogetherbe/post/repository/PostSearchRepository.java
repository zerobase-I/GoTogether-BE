package com.example.gotogetherbe.post.repository;

import com.example.gotogetherbe.post.entity.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, Long>
    , CustomPostRepository, CrudRepository<PostDocument, Long>{



}
