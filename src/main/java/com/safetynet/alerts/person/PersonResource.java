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

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
public class PersonResource {

	private final JsonUtils jsonUtils;

	/**
	 * Ajoute une nouvelle personne.
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
	 * Met à jour une personne existante (la combinaison prénom-nom est réputée unique et immuable).
	 *
	 * @param id
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
	 * Supprime une personne.
	 *
	 * @param id L’identificateur unique PrénomNom de la personne
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
