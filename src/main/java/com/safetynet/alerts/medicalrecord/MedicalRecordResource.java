package com.safetynet.alerts.medicalrecord;

import com.safetynet.alerts.person.Person;
import com.safetynet.alerts.util.JsonUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
public class MedicalRecordResource {

	private final JsonUtils jsonUtils;

	/**
	 * Ajoute un nouveau dossier médical.
	 */
	@PostMapping("/medicalRecord")
	public ResponseEntity<Void> create(@RequestBody @Valid MedicalRecord newRecord) {
		var recordList = jsonUtils.get(MedicalRecord.class);
		var personList = jsonUtils.get(Person.class);

		// Create a medical record only for an existing person
		if (personList.stream().anyMatch(person -> person.getId().equals(newRecord.getId()))) {
			if (recordList.stream().noneMatch(record -> record.getId().equals(newRecord.getId()))) {
				recordList.add(newRecord);
				jsonUtils.update("medicalrecords", recordList);
				log.info("{} added", newRecord.getFullName());
				return ResponseEntity.status(CREATED).build();
			} else {
				log.error("{} already exists", newRecord.getFullName());
				return ResponseEntity.status(CONFLICT).build();
			}
		} else {
			log.error("{} does not exist. Cannot create a medical record.", newRecord.getFullName());
			return ResponseEntity.status(I_AM_A_TEAPOT).build();
		}

	}

	/**
	 * Met à jour un dossier médical (la combinaison prénom-nom est réputée unique et immuable).
	 *
	 * @param id
	 */
	@PutMapping("/medicalRecord/{id}")
	public ResponseEntity<Void> update(@PathVariable String id, @RequestBody @Valid MedicalRecord updateRecord) {
		var recordMap = jsonUtils.get(MedicalRecord.class).stream()
				.collect(toMap(MedicalRecord::getId, x -> x));

		if (recordMap.containsKey(id)) {
			recordMap.replace(id, updateRecord);
			jsonUtils.update("medicalrecords", recordMap.values().stream().toList());
			log.info("{} updated", updateRecord.getFullName());
			return ResponseEntity.ok().build();
		} else {
			log.error("ID {} does not exist, nothing to update", id);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Supprime un dossier médical.
	 *
	 * @param id L’identificateur unique PrénomNom de la personne associée au dossier médical
	 */
	@DeleteMapping("/medicalRecord/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") String id) {
		var recordMap = jsonUtils.get(MedicalRecord.class).stream()
				.collect(toMap(MedicalRecord::getId, x -> x));

		if (recordMap.containsKey(id)) {
			var deletedRecord = recordMap.remove(id);
			jsonUtils.update("medicalrecords", recordMap.values().stream().toList());
			log.info("{} deleted", deletedRecord.getFullName());
			return ResponseEntity.noContent().build();
		} else {
			log.error("ID {} does not exist, nothing to delete", id);
			return ResponseEntity.notFound().build();
		}
	}

}
