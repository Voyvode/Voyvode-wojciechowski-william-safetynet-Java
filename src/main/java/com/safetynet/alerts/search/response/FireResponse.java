package com.safetynet.alerts.search.response;

import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FireResponse {

	private Integer firestation;
	private Set<RecordExtract> household;

	public FireResponse(Integer stationNumber, List<PersonData> personData) {
		this.firestation = stationNumber;
		household = personData.stream().map(fullRec ->
						new RecordExtract(fullRec.firstName(), fullRec.lastName(), fullRec.phone(), fullRec.getAge(), fullRec.medications(), fullRec.allergies()))
				.collect(Collectors.toUnmodifiableSet());
	}

	record RecordExtract(String firstName, String lastName, String phone, int age, Set<String> medications, Set<String> allergies) { }

}
