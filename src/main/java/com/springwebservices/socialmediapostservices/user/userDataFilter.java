package com.springwebservices.socialmediapostservices.user;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class userDataFilter {
	
	private postServiceConfiguration postConfig;
	
	protected userDataFilter(postServiceConfiguration postConfig) {
		super();
		this.postConfig = postConfig;
	}

	public FilterProvider getUserFilter() {
			
		SimpleBeanPropertyFilter userFilterCriteria;
		userFilterCriteria = 
				SimpleBeanPropertyFilter.serializeAllExcept(postConfig.getUserFilter());   //filterOutAllExcept("field1","field3");

		FilterProvider userFilter = 
				new SimpleFilterProvider().addFilter("UserDataFilter", userFilterCriteria );

		return userFilter;
		}

}
