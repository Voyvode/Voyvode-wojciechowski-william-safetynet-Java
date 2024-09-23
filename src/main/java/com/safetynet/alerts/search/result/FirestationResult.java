package com.safetynet.alerts.search.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FirestationResult {

	private final long adultCount;
	private final long childCount;
	private final Set<DataExtract> coveredPersons;

	public FirestationResult(List<PersonData> coveredPeopleData) {
		coveredPersons = coveredPeopleData.stream().map(person ->
				new DataExtract(person.firstName(), person.lastName(), person.address(), person.phone()))
				.collect(Collectors.toUnmodifiableSet());

		childCount = coveredPeopleData.stream().filter(PersonData::isMinor).count();
		adultCount = coveredPeopleData.size() - childCount;
	}

	@JsonIgnore
	public boolean isNotEmpty() {
		return !coveredPersons.isEmpty();
	}

	@JsonIgnore
	public int size() {
		return coveredPersons.size();
	}

	record DataExtract(String firstName, String lastName, String address, String phoneNumber) { }

}
