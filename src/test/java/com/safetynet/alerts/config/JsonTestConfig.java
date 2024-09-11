package com.safetynet.alerts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonTestConfig {

	public static final String SAMPLE_PATH = "data/test/sample.json";
	public static final String SAMPLE_ORIG_PATH = "data/test/sample.json.orig";

	@Bean
	@Qualifier("testObjectMapper")
	public ObjectMapper testObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()); // JSR-310
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		return objectMapper;
	}

}