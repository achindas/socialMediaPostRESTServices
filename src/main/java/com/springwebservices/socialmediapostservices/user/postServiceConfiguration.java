package com.springwebservices.socialmediapostservices.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("posts-service")
@RefreshScope 	// Beans in RefreshScope picks up latest properties on receiving Refresh Event
				// OnRefreshEvent is triggered by triggering a blank POST event to http://localhost:<Port for this Microservice>/actuator/refresh
public class postServiceConfiguration {

	@Value("${posts-service.userfilter}")
	private String[] userfilter;

	public String[] getUserFilter() {
		return userfilter;
	}

	public void setUserFilter(String[] userfilter) {
		this.userfilter = userfilter;
	}
}
