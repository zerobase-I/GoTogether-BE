package com.example.gotogetherbe.global.config;

import com.example.gotogetherbe.post.repository.PostSearchRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories(basePackageClasses = PostSearchRepository.class)
@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

  @Value("${spring.elasticsearch.uris}")
  private String uris;


  @Override
  public RestHighLevelClient elasticsearchClient() {
    ClientConfiguration configuration = ClientConfiguration.builder().
        connectedTo(uris).build();
    return RestClients.create(configuration).rest();
  }
}

