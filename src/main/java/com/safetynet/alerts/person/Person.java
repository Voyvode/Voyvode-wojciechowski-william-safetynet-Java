package com.safetynet.alerts.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Person {

	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private int zip;
	private String phone;
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
