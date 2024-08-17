package com.safetynet.alerts.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class JsonUtils {

	private final Path dataPath;

	private final ObjectMapper objectMapper;

	private final ObjectNode root;

	public JsonUtils(@Value("${data.path}") String path, ObjectMapper objectMapper) {
		this.dataPath = Paths.get(path);
		this.objectMapper = objectMapper;
		try (var inputStream = Files.newInputStream(dataPath)) {
			root = objectMapper.readValue(inputStream, ObjectNode.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> List<T> get(String name, Class<T> valueType) {
		var toListGeneric = TypeFactory.defaultInstance().constructCollectionType(List.class, valueType);

		try {
			List<T> list = objectMapper.treeToValue(root.get(name), toListGeneric);
			log.info("\"{}\" accessed", name);
			return list;
		} catch (JsonProcessingException e) {
			log.error("Cannot process JSON: {}", e.getMessage());
			throw new RuntimeException("Cannot process JSON: " + e.getMessage());
		}
	}

	public void update(String name, List<?> newList) {
		root.replace(name, objectMapper.valueToTree(newList));

		try (var outputStream = Files.newOutputStream(dataPath)) {
			objectMapper.writeValue(outputStream, root);
			log.info("\"{}\" updated", name);
		} catch (IOException e) {
			log.error("Cannot write JSON file: {}", e.getMessage());
			throw new RuntimeException("Cannot write JSON file: " + e.getMessage());
		}
	}

}
