package com.safetynet.alerts.medicalrecord;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class MedicalRecord {

	private String firstName;
	private String lastName;
	@JsonFormat(pattern = "MM/dd/yyyy")
	private LocalDate birthdate;
	private Set<String> medications;
	private Set<String> allergies;

	public String getId() {
		return firstName + lastName;
	}

	@JsonIgnore
	public String getFullName() {
		return firstName + " " + lastName;
	}

}
