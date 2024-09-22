package com.safetynet.alerts.search.response;

import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PersonInfoResponse {

	private Set<PersonInfo> foundPersons;

	public PersonInfoResponse(List<PersonData> personData) {
		foundPersons = personData.stream()
				.map(data -> new PersonInfo(data.lastName(), data.firstName(),
						data.address(), data.city(), data.zip(),
						data.getAge(), data.medications(),	data.allergies()))
				.collect(Collectors.toUnmodifiableSet());
	}

	record PersonInfo(String lastName, String firstName, String address, String city, int zip, int age, Set<String> medications, Set<String> allergies) { }

}
