package com.safetynet.alerts.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.safetynet.alerts.firestation.FirestationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * A utility class for JSON data access.
 *
 * <p>data.json is split in array nodes, each one containing a certain type a data. Given the
 * appropriate data class, JsonUtils deserialize the required JSON array into a list of objects
 * usable inside SafetyNet Alerts.
 *
 * <p>In case of modifying CRUD operations, JsonUtils can also serialize these changes by updating
 * data.json.
 */
@Component
@Slf4j
public class JsonUtils {

	private final Path dataPath;

	private final ObjectMapper objectMapper;

	private final ObjectNode root;

	/**
	 * Constructor initializing JSON mapping.
	 *
	 * @param path JSON file path
	 * @param objectMapper Jackson ObjectMapper
	 */
	public JsonUtils(@Value("${data.path}") String path, ObjectMapper objectMapper) {
		this.dataPath = Paths.get(path);
		this.objectMapper = objectMapper;
		try (var inputStream = Files.newInputStream(dataPath)) {
			root = objectMapper.readValue(inputStream, ObjectNode.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deserializes a node array into a list of objects of given type.
	 *
	 * @param valueType Given type
	 * @return List of objects
	 */
	public <T> List<T> get(Class<T> valueType) {
		var name = valueType.getSimpleName().replace("DTO", "s").toLowerCase();
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

	/**
	 * Serializes a list of objects into a node array of given name.
	 *
	 * @param name Name of node array to update
	 * @param updatedList Updated list of objects
	 */
	public <T> void update(String name, List<T> updatedList) {
		root.replace(name, objectMapper.valueToTree(updatedList));

		try (var outputStream = Files.newOutputStream(dataPath)) {
			objectMapper.writeValue(outputStream, root);
			log.info("\"{}\" updated", name);
		} catch (IOException e) {
			log.error("Cannot write JSON file: {}", e.getMessage());
			throw new RuntimeException("Cannot write JSON file: " + e.getMessage());
		}
	}

	/**
	 * Serializes values of map into a node array of given name.
	 *
	 * @param name Name of node array to update
	 * @param updatedMap Updated list of objects
	 */
	public <T> void update(String name, Map<String, T> updatedMap) {
		update(name, updatedMap.values().stream().toList());
	}

}
