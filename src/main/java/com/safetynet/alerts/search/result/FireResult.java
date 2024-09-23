package com.safetynet.alerts.search.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FireResult {

	private final Integer firestation;
	private final Set<RecordExtract> household;

	public FireResult(Integer stationNumber, List<PersonData> personData) {
		this.firestation = stationNumber;

		household = personData.stream().map(fullRec ->
						new RecordExtract(fullRec.firstName(), fullRec.lastName(), fullRec.phone(), fullRec.getAge(), fullRec.medications(), fullRec.allergies()))
				.collect(Collectors.toUnmodifiableSet());
	}

	@JsonIgnore
	public boolean isNotEmpty() {
		return !household.isEmpty();
	}

	@JsonIgnore
	public int size() {
		return household.size();
	}

	record RecordExtract(String firstName, String lastName, String phone, int age, Set<String> medications, Set<String> allergies) { }

}
