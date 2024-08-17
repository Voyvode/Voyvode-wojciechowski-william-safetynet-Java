package com.safetynet.alerts.firestation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Firestation {

	@NotBlank
	private String address;

	@Min(1)
	private int station;

}
