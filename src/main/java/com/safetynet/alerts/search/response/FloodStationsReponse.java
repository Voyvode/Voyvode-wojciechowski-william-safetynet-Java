package com.safetynet.alerts.search.response;

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
public class FloodStationsReponse {

	private Map<String, List<DataExtract>> households;

	public FloodStationsReponse(List<String> coveredAddresses, List<PersonData> coveredPersonData) {
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

	record DataExtract(String firstName, String lastName, String phoneNumber, int age, Set<String> medications, Set<String> allergies) { }

}
