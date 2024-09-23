package com.safetynet.alerts.search;

import com.safetynet.alerts.search.result.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * REST controller for handling various search and alert-related endpoints in the SafetyNet Alerts
 * system. This class provides methods to retrieve information about people, fire stations, and
 * medical records for emergency services and community alerts.
 *
 * <p>This controller includes endpoints for:
 * <ul>
 * <li>Retrieving fire station coverage information
 * <li>Generating child alerts for specific addresses
 * <li>Creating phone alerts for areas covered by specific fire stations
 * <li>Fetching person information by last name
 * <li>Retrieving community email lists
 * </ul>
 *
 * <p>Each endpoint is designed to return specific data formats to support various emergency and
 * community service needs.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SearchController {

	private final SearchService service;

	/**
	 * Retrieves people information in a fire station's coverage area.
	 *
	 * <p>This method retrieves a list of addresses covered by the specified fire station number,
	 * then returns a {@link FirestationResult} containing child/adult counts and a list of
	 * covered people with their first name, last name, address, and phone number.
	 *
	 * @param stationNumber the fire station number for which coverage information is
	 *                      requested.
	 * @return a ResponseEntity containing:
	 * <ul>
	 * <li>200 OK and a FirestationResponse,
	 * <li>or 404 Not Found if the station number doesn't exist.
	 * </ul>
	 */
	@GetMapping("/firestation")
	public ResponseEntity<FirestationResult> getFirestation(@RequestParam("stationNumber") int stationNumber) {
		var firestationResult = service.getFirestation(stationNumber);

		if (firestationResult.isNotEmpty()) {
			log.info("{} persons covered by fire station #{}", firestationResult.size(), stationNumber);
			return ResponseEntity.ok(firestationResult);
		} else {
			log.warn("Fire station #{} not found", stationNumber);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Retrieves child alert information for a given address.
	 *
	 * <p>Returns a list of children (under 18 years old) living at the specified address. The response
	 * includes each child's first name, last name, age, and a list of other household members.
	 *
	 * @param address the address to search for children
	 * @return a ResponseEntity containing:
	 * <ul>
	 * <li>200 OK and a ChildAlertResponse,
	 * <li>or 200 OK and an empty body if no children are found at the address,
	 * <li>or 404 Not Found if the address doesn't exist.
	 * </ul>
	 */
	@GetMapping("/childAlert")
	public ResponseEntity<ChildAlertResult> getChildAlert(@RequestParam("address") String address) {
		var childAlertResult = service.getChildAlert(address);

		if (childAlertResult.hasChildren()) {
			log.info("{} children found", childAlertResult.size());
			return ResponseEntity.ok(childAlertResult);
		} else if (childAlertResult.hasAdultsOnly()) {
			log.info("No children found");
			return ResponseEntity.ok().build();
        } else {
			log.warn("No household found");
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Retrieves a list of phone numbers for people covered by a fire station.
	 *
	 * <p>This information can be used to send emergency text messages to households.
	 *
	 * @param firestationNumber the fire station number for which covered phone
	 *                          numbers are requested.
	 * @return a ResponseEntity containing:
	 * <ul>
	 * <li>200 OK and a Set of phone numbers,
	 * <li>or 404 Not Found if no phone numbers are found.
	 * </ul>
	 */
	@GetMapping("/phoneAlert")
	public ResponseEntity<PhoneAlertResult> getPhoneAlert(@RequestParam("firestation") int firestationNumber) {
		var phoneAlertResult = service.getPhoneAlert(firestationNumber);

		if (phoneAlertResult.isNotEmpty()) {
			log.info("{} phone numbers found", phoneAlertResult.size());
			return ResponseEntity.ok(phoneAlertResult);
		} else {
			log.warn("No phone numbers found");
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Retrieves a list of persons living at the given address along with the fire station number
	 * serving them.
	 *
	 * <p>The list includes the name, phone number, age, and medical history (medications and
	 * allergies) of each person.
	 *
	 * @param address the address for which to retrieve inhabitant information
	 * @return a ResponseEntity containing:
	 * <ul>
	 * <li>200 OK and a FireResponse object,
	 * <li>or 404 Not Found if no persons with the specified last name are found.
	 * </ul>
	 */
	@GetMapping("/fire")
	public ResponseEntity<FireResult> getFire(@RequestParam("address") String address) {
		var fireResult = service.getFire(address);

		if (fireResult.isNotEmpty()) {
			log.info("{} persons covered by fire station #{} found", fireResult.size(), fireResult.getFirestation());
			return ResponseEntity.ok(fireResult);
		} else {
			log.warn("No people found");
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Retrieves a list of all households covered by the specified fire station(s).
	 *
	 * <p>The list groups people by address and includes the name, phone number, and age of the
	 * householders, along with their medical history (medications and allergies).
	 *
	 * @param stationNumbers a set of fire station numbers for which to retrieve
	 *                       household information
	 * @return ResponseEntity containing:
	 * <ul>
	 * <li>200 OK and a FloodStationsResponse object,
	 * <li>or 404 Not Found if no fire stations are found.
	 * </ul>
	 */
	@GetMapping("/flood/stations")
	public ResponseEntity<FloodStationsResult> getFloodStations(@RequestParam("stations") Set<Integer> stationNumbers) {
		var floodStationResult = service.getFloodStations(stationNumbers);

		if (floodStationResult.isNotEmpty()) {
			log.info("{} persons in {} households found",
					floodStationResult.getNumberOfCoveredPersons(), floodStationResult.getNumberOfCoveredHouseholds());
			return ResponseEntity.ok(floodStationResult);
		} else {
			log.warn("There is(are) no fire station(s) {}", stationNumbers);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Retrieves all persons with this last name with first name, address, age, email, and medical
	 * history (medications and allergies).
	 *
	 * @param lastName The last name to search for
	 * @return a ResponseEntity containing:
	 * <ul>
	 * <li>200 OK and a Set of PersonInfoLastNameResponse objects,
	 * <li>or 404 Not Found if no persons with the specified last name are found.
	 * </ul>
	 */
	@GetMapping("/personInfo")
	public ResponseEntity<PersonInfoResult> getPersonInfo(@RequestParam("lastName") String lastName) {
		var personInfoResult = service.getPersonInfo(lastName);

		if (personInfoResult.isNotEmpty()) {
			log.info("{} with last name {} found", personInfoResult.size(), lastName);
			return ResponseEntity.ok(personInfoResult);
		} else {
			log.warn("No people found with last name {} found", lastName);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Retrieves email addresses from all residents of a city.
	 *
	 * @param city the city for which to retrieve email addresses
	 * @return a ResponseEntity with:
	 * <ul>
	 * <li>200 OK and a Set of unique email addresses from residents of the city,
	 * <li>or 404 Not Found if the city is unknown.
	 * </ul>
	 */
	@GetMapping("/communityEmail")
	public ResponseEntity<CommunityEmailResult> getCommunityEmail(@RequestParam("city") String city) {
		var communityEmailResult = service.getCommunityEmail(city);

		if (communityEmailResult.isNotEmpty()) {
			log.info("{} email addresses found", communityEmailResult.size());
			return ResponseEntity.ok(communityEmailResult);
		} else {
			log.warn("{} is not a known city", city);
			return ResponseEntity.notFound().build();
		}
	}

}
