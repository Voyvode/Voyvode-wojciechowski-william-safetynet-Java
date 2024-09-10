package com.safetynet.alerts.search.response;

import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ChildAlertResponse {

	private Set<ChildDataExtract> children;
	private Set<OtherDataExtract> otherHouseholders;

	public ChildAlertResponse(List<PersonData> household) {
		children = household.stream()
				.filter(PersonData::isMinor)
				.map(child -> new ChildDataExtract(child.firstName(), child.lastName(), child.getAge()))
				.collect(Collectors.toUnmodifiableSet());

		otherHouseholders = household.stream()
				.filter(PersonData::isMajor)
				.map(other -> new OtherDataExtract(other.firstName(), other.lastName()))
				.collect(Collectors.toUnmodifiableSet());
	}

	record ChildDataExtract(String firstName, String lastName, int age) { }

	record OtherDataExtract(String firstName, String lastName) { }

}
