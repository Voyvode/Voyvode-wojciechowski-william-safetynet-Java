package com.safetynet.alerts.search.response;

import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FirestationResponse {

	private long adultCount;
	private long childCount;
	private Set<DataExtract> coveredPeopleDataExtract;

	public FirestationResponse(List<PersonData> coveredPeopleData) {
		coveredPeopleDataExtract = coveredPeopleData.stream().map(person ->
				new DataExtract(person.firstName(), person.lastName(), person.address(), person.phone()))
				.collect(Collectors.toUnmodifiableSet());

		childCount = coveredPeopleData.stream().filter(PersonData::isMinor).count();
		adultCount = coveredPeopleData.size() - childCount;
	}

	record DataExtract(String firstName, String lastName, String address, String phoneNumber) { }

}
