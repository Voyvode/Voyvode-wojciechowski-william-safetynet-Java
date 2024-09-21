package com.safetynet.alerts.person;

import com.safetynet.alerts.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * REST controller for managing person information.
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
public class PersonResource {

	private final JsonUtils jsonUtils;

	/**
	 * Adds a new person to the system.
	 *
	 * @param newPerson the new person
	 * @return ResponseEntity with status CREATED if successful, or CONFLICT if the person
	 *         already exists in the system
	 */
	@PostMapping("/person")
	public ResponseEntity<Void> create(@RequestBody Person newPerson) {
		var personList = jsonUtils.get(Person.class);

		if (personList.stream().noneMatch(person -> person.getId().equals(newPerson.getId()))) {
			personList.add(newPerson);
			jsonUtils.update("persons", personList);
			log.info("{} added", newPerson.getFullName());
			log.warn("Please create a medical record for {} to fill in birthdate.", newPerson.getFullName());
			return ResponseEntity.status(CREATED).build();
		} else {
			log.error("{} already exists", newPerson.getFullName());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	/**
	 * Updates person's information.
	 *
	 * @param id the unique identifier of the person (FirstnameLastname format)
	 * @param updatedPerson the updated person information
	 * @return ResponseEntity with status OK if successful, or NOT_FOUND if the person
	 *         does not exist in the system
	 */
	@PutMapping("/person/{id}")
	public ResponseEntity<Void> update(@PathVariable String id, @RequestBody Person updatedPerson) {
		var personMap = jsonUtils.get(Person.class).stream()
				.collect(toMap(Person::getId, x -> x));

		if (personMap.containsKey(id)) {
			personMap.replace(id, updatedPerson);
			jsonUtils.update("persons", personMap.values().stream().toList());
			log.info("{} updated", updatedPerson.getFullName());
			return ResponseEntity.ok().build();
		} else {
			log.error("ID {} does not exist, nothing to update", id);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Deletes a person from the system.
	 *
	 * @param id the unique identifier of the person to delete (FirstnameLastname format)
	 * @return ResponseEntity with status NO_CONTENT if successful, or NOT_FOUND if the
	 *         person does not exist in the system
	 */
	@DeleteMapping("/person/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") String id) {
		var personMap = jsonUtils.get(Person.class).stream()
				.collect(toMap(Person::getId, x -> x));

		if (personMap.containsKey(id)) {
			var deletedPerson = personMap.remove(id);
			jsonUtils.update("persons", personMap.values().stream().toList());
			log.info("{} deleted", deletedPerson.getFullName());
			return ResponseEntity.noContent().build();
		} else {
			log.error("ID {} does not exist, nothing to delete", id);
			return ResponseEntity.notFound().build();
		}
	}

}
