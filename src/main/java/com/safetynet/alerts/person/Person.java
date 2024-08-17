package com.safetynet.alerts.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Person {

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	private String address;

	@NotBlank
	private String city;

	@NotBlank @Pattern(regexp = "\\d{5}", message = "ZIP code must be 5 digits")
	private int zip;

	@NotBlank @Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}", message = "Phone number must be XXX-XXX-XXXX format")
	private String phone;

	@NotBlank @Email
	private String email;

	@JsonIgnore
	public String getId() {
		return firstName + lastName;
	}

	@JsonIgnore
	public String getFullName() {
		return firstName + " " + lastName;
	}

}
