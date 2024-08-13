package com.safetynet.alerts.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
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
		List<T> list = null;
		try {
			var arrayNode = root.get(name);
			var toListGeneric = TypeFactory.defaultInstance().constructCollectionType(List.class, valueType);
			list = objectMapper.treeToValue(arrayNode, toListGeneric);
		} catch (JsonProcessingException e) {
			log.warn("Cannot process JSON in {}", e.getMessage());
		}
		return list;
	}

	public void update(String name, List<?> newList) {
		root.replace(name, objectMapper.valueToTree(newList));

		try (var outputStream = Files.newOutputStream(dataPath)) {
			objectMapper.writeValue(outputStream, root);
		} catch (IOException e) {
			log.warn("Cannot write JSON file {}", e.getMessage());
		}
	}

}
