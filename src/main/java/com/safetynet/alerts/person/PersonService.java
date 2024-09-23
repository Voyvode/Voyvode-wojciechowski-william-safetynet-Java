package com.safetynet.alerts.person;

import com.safetynet.alerts.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

	private final JsonUtils jsonUtils;

	public boolean createPerson(PersonDTO newPerson) {
		var personMap = getPersonMap();

		if (personMap.putIfAbsent(newPerson.getId(), newPerson) == null) {
			updateJSON(personMap);
			return true;
		} else {
			return false;
		}
	}

	public boolean updatePerson(String id, PersonDTO updatedPerson) {
		var personMap = getPersonMap();

		if (personMap.containsKey(id)) {
			personMap.replace(id, updatedPerson);
			updateJSON(personMap);
			return true;
		}
		return false;
	}

	public PersonDTO deletePerson(String id) {
		var personMap = getPersonMap();
		var deletedPerson = personMap.remove(id);

		if (deletedPerson != null) {
			updateJSON(personMap);
		}
		return deletedPerson;
	}

	private Map<String, PersonDTO> getPersonMap() {
		return jsonUtils.get(PersonDTO.class).stream()
				.collect(Collectors.toMap(PersonDTO::getId, x -> x));
	}

	private void updateJSON(Map<String, PersonDTO> firestationMap) {
		jsonUtils.update("persons", firestationMap);
	}

}
