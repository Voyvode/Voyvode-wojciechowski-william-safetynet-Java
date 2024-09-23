package com.safetynet.alerts.firestation;

import com.safetynet.alerts.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirestationService {

	private final JsonUtils jsonUtils;

	public boolean createFirestation(FirestationDTO newFirestation) {
		var firestationMap = getFirestationMap();

		if (firestationMap.putIfAbsent(newFirestation.getAddress(), newFirestation) == null) {
			updateJSON(firestationMap);
			return true;
		} else {
			return false;
		}
	}

	public boolean updateFirestation(String address, FirestationDTO updatedFirestation) {
		var firestationMap = getFirestationMap();

		if (firestationMap.containsKey(address)) {
			firestationMap.replace(address, updatedFirestation);
			updateJSON(firestationMap);
			return true;
		}
		return false;
	}

	public FirestationDTO deleteFirestation(String address) {
		var firestationMap = getFirestationMap();
		var deletedFirestation = firestationMap.remove(address);

		if (deletedFirestation != null) {
			updateJSON(firestationMap);
		}
		return deletedFirestation;
	}

	private Map<String, FirestationDTO> getFirestationMap() {
		return jsonUtils.get(FirestationDTO.class).stream()
				.collect(Collectors.toConcurrentMap(FirestationDTO::getAddress, x -> x));
	}

	private void updateJSON(Map<String, FirestationDTO> firestationMap) {
		jsonUtils.update("firestations", firestationMap);
	}

}
