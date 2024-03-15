package com.example.gotogetherbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = "com.example.gotogetherbe",
	excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.example.gotogetherbe.chat.*"))
public class GoTogetherBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoTogetherBeApplication.class, args);
	}

}
