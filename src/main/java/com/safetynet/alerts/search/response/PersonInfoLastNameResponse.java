package com.safetynet.alerts.search.response;

import com.safetynet.alerts.medicalrecord.MedicalRecord;
import com.safetynet.alerts.person.Person;
import lombok.Data;

import java.util.Set;

@Data
public class PersonInfoLastNameResponse {

	private String lastName;
	private String firstName;
	private String address;
	private String city;
	private int zip;
	private int age;
	private Set<String> medication;
	private Set<String> allergies;

	public PersonInfoLastNameResponse(Person person, MedicalRecord medicalRecord) { //TODO: exploiter PersonDataStream
		lastName = person.getLastName();
		firstName = person.getFirstName();
		address = person.getAddress();
		city = person.getCity();
		zip = person.getZip();
		age = medicalRecord.getAge();
		medication = medicalRecord.getMedications();
		allergies = medicalRecord.getAllergies();
	}

}
