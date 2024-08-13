package com.safetynet.alerts.person;

import com.safetynet.alerts.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RestController
public class PersonResource {

	private final JsonUtils jsonUtils;

	@Autowired
	public PersonResource(JsonUtils jsonUtils) {
		this.jsonUtils = jsonUtils;
	}

	/**
	 * Ajoute une nouvelle personne.
	 */
	@PostMapping("/person")
	public void create(@RequestBody Person newPerson) {
		var personList = jsonUtils.get("persons", Person.class);

		if (personList.stream().anyMatch(person -> person.getId().equals(newPerson.getId()))) {
			log.error("{} already exists", newPerson.getFullName());
		} else {
			personList.add(newPerson);
			jsonUtils.update("persons", personList);
			log.info("{} added", newPerson.getFullName());
		}
	}

	/**
	 * Liste l’ensemble des personnes couvertes par les casernes.
	 *
	 * @return
	 */
	@GetMapping("/person")
	public List<Person> read() {
		log.info("All person data accessed");
		return jsonUtils.get("persons", Person.class);
	}

	/**
	 * Renvoie une personne.
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/person/{id}")
	public Person readOne(@PathVariable String id) {
		var personMap = jsonUtils.get("persons", Person.class).stream()
				.collect(toMap(Person::getId, x -> x));

		if (!personMap.containsKey(id)) {
			log.error("ID {} does not exist", id);
			return null;
		} else {
			var person = personMap.get(id);
			log.info("{} data accessed", person.getFullName());
			return person;
		}
	}

	/**
	 * Met à jour une personne existante (la combinaison prénom-nom est réputée unique et immuable).
	 *
	 * @param id
	 */
	@PutMapping("/person/{id}")
	public void update(@PathVariable String id, @RequestBody Person updatedPerson) {
		var personMap = jsonUtils.get("persons", Person.class).stream()
				.collect(toMap(Person::getId, x -> x));

		if (!personMap.containsKey(id)) {
			log.error("ID {} does not exist, nothing to update", id);
		} else {
			personMap.replace(id, updatedPerson);
			jsonUtils.update("persons", personMap.values().stream().toList());
			log.info("{} updated", updatedPerson.getFullName());
		}
	}

	/**
	 * Supprime une personne.
	 *
	 * @param id L’identificateur unique PrénomNom de la personne
	 */
	@DeleteMapping("/person/{id}")
	public void delete(@PathVariable("id") String id) {
		var personMap = jsonUtils.get("persons", Person.class).stream()
				.collect(toMap(Person::getId, x -> x));

		if (!personMap.containsKey(id)) {
			log.error("ID {} does not exist, nothing to delete", id);
		} else {
			var deletedPerson = personMap.remove(id);
			jsonUtils.update("persons", personMap.values().stream().toList());
			log.info("{} deleted", deletedPerson.getFullName());
		}
	}

}
