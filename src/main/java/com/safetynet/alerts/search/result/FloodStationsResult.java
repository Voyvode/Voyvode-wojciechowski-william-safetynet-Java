package com.safetynet.alerts.search.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetynet.alerts.search.PersonData;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Retourne une liste de tous les foyers desservis par la caserne. Cette
 * liste doit regrouper les personnes par adresse. Elle doit aussi inclure le nom, le
 * numéro de téléphone et l'âge des habitants, et faire figurer leurs antécédents
 * médicaux (médicaments, posologie et allergies) à côté de chaque nom.
 */
@Data
public class FloodStationsResult {

	private final Map<String, List<DataExtract>> households;

	public FloodStationsResult(List<String> coveredAddresses, List<PersonData> coveredPersonData) {
		households = coveredPersonData.stream()
				.filter(person -> coveredAddresses.contains(person.address()))
				.collect(Collectors.groupingBy(
						PersonData::address,
						HashMap::new,
						Collectors.mapping(
								person -> new DataExtract(
										person.firstName(),
										person.lastName(),
										person.phone(),
										person.getAge(),
										person.medications(),
										person.allergies()
								),
								Collectors.toList()
						)
				));
	}

	@JsonIgnore
	public boolean isNotEmpty() {
		return !households.isEmpty();
	}

	@JsonIgnore
	public int getNumberOfCoveredPersons() {
		return households.values().stream()
				.mapToInt(List::size)
				.sum();
	}

	@JsonIgnore
	public int getNumberOfCoveredHouseholds() {
		return households.size();
	}

	record DataExtract(String firstName, String lastName, String phoneNumber, int age, Set<String> medications, Set<String> allergies) { }

}
