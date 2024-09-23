package com.safetynet.alerts.medicalrecord;

import com.safetynet.alerts.person.PersonDTO;
import com.safetynet.alerts.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

	private final JsonUtils jsonUtils;

	public enum CreateResult {
		RECORD_CREATED, PERSON_NOT_FOUND, ALREADY_EXISTS,
	}

	public CreateResult createMedicalRecord(MedicalRecordDTO newMedicalRecord) {
		var medicalRecordMap = getMedicalRecordMap();
		var personList = jsonUtils.get(PersonDTO.class);

		if (personList.stream().noneMatch(person -> person.getId().equals(newMedicalRecord.getId()))) {
			return CreateResult.PERSON_NOT_FOUND;
		}

		if (medicalRecordMap.putIfAbsent(newMedicalRecord.getId(), newMedicalRecord) == null) {
			updateJSON(medicalRecordMap);
			return CreateResult.RECORD_CREATED;
		} else {
			return CreateResult.ALREADY_EXISTS;
		}
	}

	public boolean updateMedicalRecord(String id, MedicalRecordDTO updatedMedicalRecord) {
		var medicalRecordMap = getMedicalRecordMap();

		if (medicalRecordMap.containsKey(id)) {
			medicalRecordMap.replace(id, updatedMedicalRecord);
			updateJSON(medicalRecordMap);
			return true;
		}
		return false;
	}

	public MedicalRecordDTO deleteMedicalRecord(String id) {
		var medicalRecordMap = getMedicalRecordMap();
		var deletedMedicalRecord = medicalRecordMap.remove(id);

		if (deletedMedicalRecord != null) {
			updateJSON(medicalRecordMap);
		}
		return deletedMedicalRecord;
	}

	private Map<String, MedicalRecordDTO> getMedicalRecordMap() {
		return jsonUtils.get(MedicalRecordDTO.class).stream()
				.collect(Collectors.toMap(MedicalRecordDTO::getId, x -> x));
	}

	private void updateJSON(Map<String, MedicalRecordDTO> firestationMap) {
		jsonUtils.update("medicalrecords", firestationMap);
	}

}
