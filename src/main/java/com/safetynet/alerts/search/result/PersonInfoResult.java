package com.safetynet.alerts.search.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PersonInfoResult {

	private final Set<PersonInfo> foundPersons;

	public PersonInfoResult(List<PersonData> personData) {
		foundPersons = personData.stream()
				.map(data -> new PersonInfo(data.lastName(), data.firstName(),
						data.address(), data.city(), data.zip(),
						data.getAge(), data.medications(),	data.allergies()))
				.collect(Collectors.toUnmodifiableSet());
	}

	@JsonIgnore
	public boolean isNotEmpty() {
		return !foundPersons.isEmpty();
	}

	@JsonIgnore
	public int size() {
		return foundPersons.size();
	}

	record PersonInfo(String lastName, String firstName, String address, String city, String zip, int age, Set<String> medications, Set<String> allergies) { }

}
