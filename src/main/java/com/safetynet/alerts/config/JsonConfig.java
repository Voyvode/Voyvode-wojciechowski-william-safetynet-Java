package com.safetynet.alerts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for JSON serialization.
 */
@Configuration
public class JsonConfig {

	/**
	 * Creates and configures an ObjectMapper bean.
	 *
	 * @return a configured ObjectMapper instance
	 */
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()); // JSR-310
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setDefaultPrettyPrinter(new CustomPrettyPrinter());

		return objectMapper;
	}

}