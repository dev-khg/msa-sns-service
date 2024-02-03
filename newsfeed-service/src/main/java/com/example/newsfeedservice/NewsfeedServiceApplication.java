package com.example.newsfeedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class NewsfeedServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsfeedServiceApplication.class, args);
	}

}
