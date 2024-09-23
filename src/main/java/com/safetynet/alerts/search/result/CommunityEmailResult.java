package com.safetynet.alerts.search.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommunityEmailResult extends HashSet<String> {

	public CommunityEmailResult(Set<String> emails) {
		super(emails);
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
