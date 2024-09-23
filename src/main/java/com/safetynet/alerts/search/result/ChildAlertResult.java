package com.safetynet.alerts.search.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ChildAlertResult {

	private final Set<ChildDataExtract> children;
	private final Set<OtherDataExtract> otherHouseholders;

	public ChildAlertResult(List<PersonData> household) {
		children = household.stream()
				.filter(PersonData::isMinor)
				.map(child -> new ChildDataExtract(child.firstName(), child.lastName(), child.getAge()))
				.collect(Collectors.toUnmodifiableSet());

		otherHouseholders = household.stream()
				.filter(PersonData::isMajor)
				.map(other -> new OtherDataExtract(other.firstName(), other.lastName()))
				.collect(Collectors.toUnmodifiableSet());
	}

	@JsonIgnore
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@JsonIgnore
	public boolean hasAdultsOnly() {
		return children.isEmpty() && otherHouseholders.isEmpty();
	}

	@JsonIgnore
	public int size() {
		return children.size();
	}

	record ChildDataExtract(String firstName, String lastName, int age) { }

	record OtherDataExtract(String firstName, String lastName) { }

}
