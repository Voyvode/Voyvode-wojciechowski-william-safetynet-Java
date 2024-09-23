package com.safetynet.alerts.medicalrecord;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

/**
 * REST controller for managing medical records.
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(value = "/medicalRecord")
public class MedicalRecordController {

	private final MedicalRecordService service;

	/**
	 * Adds a new medical record to the system. A medical record can only be created for an
	 * existing person in the system.
	 *
	 * @param newRecord the new medical record
	 * @return ResponseEntity with status CREATED if successful, CONFLICT if the record
	 *         already exists, or I_AM_A_TEAPOT if the associated person does not exist
	 */
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody @Valid MedicalRecordDTO newRecord) {
		switch (service.createMedicalRecord(newRecord)) {
			case RECORD_CREATED -> {
				log.info("{} added", newRecord.getFullName());
				return ResponseEntity.status(CREATED).build();
			}
			case PERSON_NOT_FOUND -> {
				log.error("{} does not exist in the system. Cannot create a medical record", newRecord.getFullName());
				return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
			}
			default -> {
				log.error("{} already has a medical record", newRecord.getFullName());
				ResponseEntity.status(PRECONDITION_REQUIRED);
				return ResponseEntity.status(CONFLICT).build();
			}
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
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable String id, @RequestBody @Valid MedicalRecordDTO updateRecord) {
		if (service.updateMedicalRecord(id, updateRecord)) {
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
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") String id) {
		var deletedRecord = service.deleteMedicalRecord(id);
		if (deletedRecord != null) {
			log.info("{} deleted", deletedRecord.getFullName());
			return ResponseEntity.noContent().build();
		} else {
			log.error("ID {} does not exist, nothing to delete", id);
			return ResponseEntity.notFound().build();
		}
	}

}
