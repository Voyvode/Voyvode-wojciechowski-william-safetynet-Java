package com.safetynet.alerts.medicalrecord;

import com.safetynet.alerts.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RestController
public class MedicalRecordResource {

	private final JsonUtils jsonUtils;

	@Autowired
	public MedicalRecordResource(JsonUtils jsonUtils) {
		this.jsonUtils = jsonUtils;
	}

	/**
	 * Ajoute un nouveau dossier médical.
	 */
	@PostMapping("/medicalRecord")
	public void create(@RequestBody MedicalRecord newRecord) {
		var recordList = jsonUtils.get("medicalrecords", MedicalRecord.class);

		if (recordList.stream().anyMatch(record -> record.getId().equals(newRecord.getId()))) {
			log.error("{} already exists", newRecord.getFullName());
		} else {
			recordList.add(newRecord);
			jsonUtils.update("medicalrecords", recordList);
			log.info("{} added", newRecord.getFullName());
		}
	}

	/**
	 * Liste l’ensemble des dossiers médicaux.
	 *
	 * @return
	 */
	@GetMapping("/medicalRecord")
	public List<MedicalRecord> read() {
		log.info("All medical records accessed");
		return jsonUtils.get("medicalrecords", MedicalRecord.class);
	}

	/**
	 * Renvoie un dossier médical.
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/medicalRecord/{id}")
	public MedicalRecord readOne(@PathVariable String id) {
		var recordMap = jsonUtils.get("medicalrecords", MedicalRecord.class).stream()
				.collect(toMap(MedicalRecord::getId, x -> x));

		if (!recordMap.containsKey(id)) {
			log.error("ID {} does not exist", id);
			return null;
		} else {
			var record = recordMap.get(id);
			log.info("{} data accessed", record.getFullName());
			return record;
		}
	}

	/**
	 * Met à jour un dossier médical (la combinaison prénom-nom est réputée unique et immuable).
	 *
	 * @param id
	 */
	@PutMapping("/medicalRecord/{id}")
	public void update(@PathVariable String id, @RequestBody MedicalRecord updateRecord) {
		var recordMap = jsonUtils.get("medicalrecords", MedicalRecord.class).stream()
				.collect(toMap(MedicalRecord::getId, x -> x));

		if (!recordMap.containsKey(id)) {
			log.error("ID {} does not exist, nothing to update", id);
		} else {
			recordMap.replace(id, updateRecord);
			jsonUtils.update("medicalrecords", recordMap.values().stream().toList());
			log.info("{} updated", updateRecord.getFullName());
		}
	}

	/**
	 * Supprime un dossier médical.
	 *
	 * @param id L’identificateur unique PrénomNom de la personne associée au dossier médical
	 */
	@DeleteMapping("/medicalRecord/{id}")
	public void delete(@PathVariable("id") String id) {
		var recordMap = jsonUtils.get("medicalrecords", MedicalRecord.class).stream()
				.collect(toMap(MedicalRecord::getId, x -> x));

		if (!recordMap.containsKey(id)) {
			log.error("ID {} does not exist, nothing to delete", id);
		} else {
			var deletedRecord = recordMap.remove(id);
			jsonUtils.update("medicalrecords", recordMap.values().stream().toList());
			log.info("{} deleted", deletedRecord.getFullName());
		}
	}

}
