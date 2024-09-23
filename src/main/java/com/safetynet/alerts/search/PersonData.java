package com.safetynet.alerts.search;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

/**
 * Convenience class aggregating Person and MedicalRecord data.
 */
public record PersonData(
	// Person
	String firstName,
	String lastName,
	String address,
	String city,
	String zip,
	String phone,
	String email,
	// MedicalRecord
	LocalDate birthdate,
	Set<String> medications,
	Set<String> allergies
) {

	public int getAge() {
		return Period.between(birthdate, LocalDate.now()).getYears();
	}

	public boolean isMinor() {
		return getAge() < 18;
	}

	public boolean isMajor() {
		return !isMinor();
	}

}
