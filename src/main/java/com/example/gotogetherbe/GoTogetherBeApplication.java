package com.example.gotogetherbe;

import com.example.gotogetherbe.post.repository.PostSearchRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(excludeFilters =
@ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = PostSearchRepository.class))
public class GoTogetherBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoTogetherBeApplication.class, args);
	}

}
