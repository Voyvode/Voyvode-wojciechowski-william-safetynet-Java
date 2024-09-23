package com.safetynet.alerts.firestation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * REST controller for managing address/firestation assignments.
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(value = "/firestation")
public class FirestationController {

	private final FirestationService service;

	/**
	 * Adds a new assignment.
	 *
	 * @param newFirestation the new address/firestation assignment to be added
	 * @return ResponseEntity with status CREATED if successful, or CONFLICT if the address
	 *         is already assigned
	 */
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody @Valid FirestationDTO newFirestation) {
		if (service.createFirestation(newFirestation)) {
			log.info("{} is assigned to station {}", newFirestation.getAddress(), newFirestation.getStation());
			return ResponseEntity.status(CREATED).build();
		} else {
			log.error("{} is already assigned", newFirestation.getAddress());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	/**
	 * Updates an assignment.
	 *
	 * @param address the assigned address to update
	 * @param updatedFirestation the new firestation assigned
	 * @return ResponseEntity with status OK if successful, or NOT_FOUND if the address is
	 *         not assigned
	 */
	@PutMapping("/{address}")
	public ResponseEntity<Void> update(@PathVariable String address, @RequestBody @Valid FirestationDTO updatedFirestation) {
		if (service.updateFirestation(address, updatedFirestation)) {
			log.info("{} is now assigned to station {}", address, updatedFirestation.getStation());
			return ResponseEntity.ok().build();
		} else {
			log.error("{} is not assigned, nothing to update", address);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Deletes an assignment.
	 *
	 * @param address the assigned address to delete
	 * @return ResponseEntity with status NO_CONTENT if successful, or NOT_FOUND if the
	 *         address is not assigned
	 */
	@DeleteMapping("/{address}")
	public ResponseEntity<Void> delete(@PathVariable("address") String address) {
		var deletedFirestation = service.deleteFirestation(address);

		if (deletedFirestation != null) {
			log.info("{} is not assigned to station {} any more", address, deletedFirestation.getStation());
			return ResponseEntity.noContent().build();
		} else {
			log.error("{} is not assigned, nothing to delete", address);
			return ResponseEntity.notFound().build();
		}
	}

}

