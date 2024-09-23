package com.safetynet.alerts.person;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * REST controller for managing person information.
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(value = "/person")
public class PersonController {

	private final PersonService service;

	/**
	 * Adds a new person to the system.
	 *
	 * @param newPerson the new person
	 * @return ResponseEntity with status CREATED if successful, or CONFLICT if the person
	 *         already exists in the system
	 */
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody @Valid PersonDTO newPerson) {
		if (service.createPerson(newPerson)) {
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
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable String id, @RequestBody PersonDTO updatedPerson) {
		if (service.updatePerson(id, updatedPerson)) {
			log.info("{} information updated", updatedPerson.getFullName());
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
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") String id) {
		var deletedPerson = service.deletePerson(id);

		if (deletedPerson != null) {
			log.info("{} deleted", deletedPerson.getFullName());
			return ResponseEntity.noContent().build();
		} else {
			log.error("ID {} does not exist, nothing to delete", id);
			return ResponseEntity.notFound().build();
		}
	}

}
