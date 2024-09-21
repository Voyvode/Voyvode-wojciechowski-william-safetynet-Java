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

/**
 * REST controller for managing medical records.
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
public class MedicalRecordResource {

	private final JsonUtils jsonUtils;

	/**
	 * Adds a new medical record to the system. A medical record can only be created for an
	 * existing person in the system.
	 *
	 * @param newRecord the new medical record
	 * @return ResponseEntity with status CREATED if successful, CONFLICT if the record
	 *         already exists, or I_AM_A_TEAPOT if the associated person does not exist
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
	 * Updates a medical record.
	 *
	 * @param id the unique identifier of the medical record (FirstnameLastname format)
	 * @param updateRecord the updated medical record
	 * @return ResponseEntity with status OK if successful, or NOT_FOUND if the medical
	 *         record does not exist
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
	 * Deletes a medical record.
	 *
	 * @param id the unique identifier of the medical record to delete
	 *           (FirstnameLastname format)
	 * @return ResponseEntity with status NO_CONTENT if successful, or NOT_FOUND if the
	 *         medical record does not exist
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
