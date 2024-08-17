package com.safetynet.alerts.firestation;

import com.safetynet.alerts.util.JsonUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
public class FirestationResource {

	private final JsonUtils jsonUtils;

	/**
	 * Ajoute une nouvelle affectation adresse/caserne.
	 */
	@PostMapping(value = "/firestation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> create(@RequestBody @Valid Firestation newFirestation) {
		var firestationList = jsonUtils.get(Firestation.class);

		if (firestationList.stream().noneMatch(firestation -> firestation.getAddress().equals(newFirestation.getAddress()))) {
			firestationList.add(newFirestation);
			jsonUtils.update("firestations", firestationList);
			log.info("{} is assigned to station {}", newFirestation.getAddress(), newFirestation.getStation());
			return ResponseEntity.status(CREATED).build();
		} else {
			log.error("{} is already assigned", newFirestation.getAddress());
			return ResponseEntity.status(CONFLICT).build();
		}
	}

	/**
	 * Met à jour une affectation.
	 *
	 * @param address
	 */
	@PutMapping("/firestation/{address}")
	public ResponseEntity<Void> update(@PathVariable String address, @RequestBody @Valid Firestation updatedFirestation) {
		var firestationMap = jsonUtils.get(Firestation.class).stream()
				.collect(toMap(Firestation::getAddress, x -> x));

		if (firestationMap.containsKey(address)) {
			firestationMap.replace(address, updatedFirestation);
			jsonUtils.update("firestations", firestationMap.values().stream().toList());
			log.info("{} is now assigned to station {}", address, updatedFirestation.getStation());
			return ResponseEntity.ok().build();
		} else {
			log.error("{} is not assigned, nothing to update", address);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Supprime une affectation.
	 *
	 * @param address
	 */
	@DeleteMapping("/firestation/{address}")
	public ResponseEntity<Void> delete(@PathVariable("address") String address) {
		var firestationMap = jsonUtils.get(Firestation.class).stream()
				.collect(toMap(Firestation::getAddress, x -> x));

		if (firestationMap.containsKey(address)) {
			var deletedFirestation = firestationMap.remove(address);
			jsonUtils.update("firestations", firestationMap.values().stream().toList());
			log.info("{} is not assigned to station {} any more", address, deletedFirestation.getStation());
			return ResponseEntity.noContent().build();
		} else {
			log.error("{} is not assigned, nothing to delete", address);
			return ResponseEntity.notFound().build();
		}
	}

}
