package com.safetynet.alerts.firestation;

import com.safetynet.alerts.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RestController
public class FirestationResource {

	private final JsonUtils jsonUtils;

	@Autowired
	public FirestationResource(JsonUtils jsonUtils) {
		this.jsonUtils = jsonUtils;
	}

	/**
	 * Ajoute une nouvelle affectation adresse/caserne.
	 */
	@PostMapping("/firestation")
	public void create(@RequestBody Firestation newFirestation) {
		var firestationList = jsonUtils.get("firestations", Firestation.class);

		if (firestationList.stream().anyMatch(firestation -> firestation.getAddress().equals(newFirestation.getAddress()))) {
			log.error("{} is already assigned", newFirestation.getAddress());
		} else {
			firestationList.add(newFirestation);
			jsonUtils.update("firestations", firestationList);
			log.info("{} is assigned to station {}", newFirestation.getAddress(), newFirestation.getStation());
		}
	}

	/**
	 * Liste l’ensemble des affectations.
	 *
	 * @return
	 */
	@GetMapping("/firestation")
	public List<Firestation> read() {
		log.info("All address/firestation assignments accessed");
		return jsonUtils.get("firestations", Firestation.class);
	}

	/**
	 * Renvoie l’affectation associée à l’adresse.
	 *
	 * @param address
	 * @return
	 */
	@GetMapping("/firestation/{address}")
	public Firestation readOne(@PathVariable String address) {
		var firestationMap = jsonUtils.get("firestations", Firestation.class).stream()
				.collect(toMap(Firestation::getAddress, x -> x));

		if (!firestationMap.containsKey(address)) {
			log.error("{} is not assigned to any station", address);
			return null;
		} else {
			var firestation = firestationMap.get(address);
			log.info("{} assignment accessed", address);
			return firestation;
		}
	}

	/**
	 * Met à jour une affectation.
	 *
	 * @param address
	 */
	@PutMapping("/firestation/{address}")
	public void update(@PathVariable String address, @RequestBody Firestation updatedFirestation) {
		var firestationMap = jsonUtils.get("firestations", Firestation.class).stream()
				.collect(toMap(Firestation::getAddress, x -> x));

		if (!firestationMap.containsKey(address)) {
			log.error("{} is not assigned, nothing to update", address);
		} else {
			firestationMap.replace(address, updatedFirestation);
			jsonUtils.update("firestations", firestationMap.values().stream().toList());
			log.info("{} is now assigned to station {}", address, updatedFirestation.getStation());
		}
	}

	/**
	 * Supprime une affectation.
	 *
	 * @param address
	 */
	@DeleteMapping("/firestation/{address}")
	public void delete(@PathVariable("address") String address) {
		var firestationMap = jsonUtils.get("firestations", Firestation.class).stream()
				.collect(toMap(Firestation::getAddress, x -> x));

		if (!firestationMap.containsKey(address)) {
			log.error("{} is not assigned, nothing to delete", address);
		} else {
			var deletedFirestation = firestationMap.remove(address);
			jsonUtils.update("persons", firestationMap.values().stream().toList());
			log.info("{} is not assigned to station {} any more", address, deletedFirestation.getStation());
		}
	}

}
