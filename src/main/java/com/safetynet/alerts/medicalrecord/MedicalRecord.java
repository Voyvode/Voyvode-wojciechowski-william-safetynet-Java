package com.safetynet.alerts.medicalrecord;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class MedicalRecord {

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotNull
	@JsonFormat(pattern = "MM/dd/yyyy")
	private LocalDate birthdate;

	private Set<String> medications;

	private Set<String> allergies;

	@JsonIgnore
	public String getId() {
		return firstName + lastName;
	}

	@JsonIgnore
	public String getFullName() {
		return firstName + " " + lastName;
	}

}
