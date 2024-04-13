package com.springwebservices.socialmediapostservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SocialMediaPostServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaPostServicesApplication.class, args);
	}

}
