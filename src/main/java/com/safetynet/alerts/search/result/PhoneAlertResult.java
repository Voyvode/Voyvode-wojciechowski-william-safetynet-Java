package com.safetynet.alerts.search.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class PhoneAlertResult extends HashSet<String> {

	public PhoneAlertResult(Set<String> phoneNumbers) {
		super(phoneNumbers);
	}

	@JsonIgnore
	public boolean isNotEmpty() {
		return !super.isEmpty();
	}

	@Override
	public int size() {
		return super.size();
	}

}
