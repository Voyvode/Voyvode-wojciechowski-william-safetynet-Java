package com.safetynet.alerts.search;

import com.safetynet.alerts.firestation.Firestation;
import com.safetynet.alerts.medicalrecord.MedicalRecord;
import com.safetynet.alerts.person.Person;
import com.safetynet.alerts.search.response.*;
import com.safetynet.alerts.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

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
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
public class SearchResource {

	private final JsonUtils jsonUtils;

	/**
	 * Return a stream of PersonData.
	 *
	 * <p>PersonData aggregates Person and MedicalRecord for easier search into known personal
	 * data.
	 *
	 * @return a Stream of PersonData objects
	 */
	private Stream<PersonData> getPersonDataStream() {
		var persons = jsonUtils.get(Person.class);
		var records = jsonUtils.get(MedicalRecord.class).stream()
				.collect(toMap(MedicalRecord::getFullName, x -> x));

		return persons.stream().map(person -> new PersonData(
				person.getFirstName(), person.getLastName(),
				person.getAddress(), person.getCity(), person.getZip(),
				person.getPhone(), person.getEmail(),
				records.get(person.getFullName()).getBirthdate(),
				records.get(person.getFullName()).getMedications(),
				records.get(person.getFullName()).getAllergies()
		));
	}

	/**
	 * Retrieves people information in a fire station's coverage area.
	 *
	 * <p>This method retrieves a list of addresses covered by the specified fire station number,
	 * then returns a {@link FirestationResponse} containing child/adult counts and a list of
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
	public ResponseEntity<FirestationResponse> getFirestation(@RequestParam("stationNumber") int stationNumber) {
		log.info("Searching fire station #{} coverage", stationNumber);

		var stationCoverage = jsonUtils.get(Firestation.class).stream()
				.filter(firestation -> firestation.getStation() == stationNumber)
				.map(Firestation::getAddress).toList();
		log.debug("Covered addresses: {}", stationCoverage);

		var coveredPeople = getPersonDataStream()
				.filter(personData -> stationCoverage.contains(personData.address())).toList();
		log.debug("Covered people: {}", coveredPeople);

		if (!coveredPeople.isEmpty()) {
			var response = new FirestationResponse(coveredPeople);
			log.info("{} persons covered", coveredPeople.size());
			return ResponseEntity.ok(response);
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
	public ResponseEntity<ChildAlertResponse> getChildAlert(@RequestParam("address") String address) {
		log.info("Searching for children at {}", address);

		var household = getPersonDataStream()
				.filter(person -> person.address().equals(address)).toList();
		log.debug("Household: {}", household);

		if (!household.isEmpty()) {
            if (household.stream().anyMatch(PersonData::isMinor)) {
				var response = new ChildAlertResponse(household);
				log.info("{} children found", response.getChildren().size());
				return ResponseEntity.ok(response);
			} else {
				log.info("No children found");
				return ResponseEntity.ok().build();
			}
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
	 * <li>or 404 Not Found if no addresses are associated with the given fire station number.
	 * </ul>
	 */
	@GetMapping("/phoneAlert")
	public ResponseEntity<Set<String>> getPhoneAlert(@RequestParam("firestation") int firestationNumber) {
		log.info("Searching all phone numbers covered by fire station #{}", firestationNumber);

		var stationCoverage = jsonUtils.get(Firestation.class).stream()
				.filter(firestation -> firestation.getStation() == firestationNumber)
				.map(Firestation::getAddress)
				.collect(toUnmodifiableSet());
		log.debug("Searched addresses: {}", stationCoverage);

		if (!stationCoverage.isEmpty()) {
			var response = jsonUtils.get(Person.class).stream()
					.filter(person -> stationCoverage.contains(person.getAddress()))
					.map(Person::getPhone)
					.collect(toUnmodifiableSet());
			log.info("{} phone numbers found", response.size());
			return ResponseEntity.ok(response);
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
	public ResponseEntity<FireResponse> getFire(@RequestParam("address") String address) {
		log.info("Searching people and covering fire station at {}", address);

		var firestations = jsonUtils.get(Firestation.class).stream()
				.collect(toMap(Firestation::getAddress, Firestation::getStation));

		if (firestations.containsKey(address)) {
			var coveringStation = firestations.get(address);
			var household = getPersonDataStream()
					.filter(personData -> personData.address().equals(address)).toList();
			var response = new FireResponse(coveringStation, household);
			log.info("{} persons covered by fire station #{} found", household.size(), coveringStation);
			log.debug("Household: {}", household);

			return ResponseEntity.ok(response);
		} else {
			log.warn("No people found");
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Retrieves a list of all households served by the specified fire station(s).
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
	public ResponseEntity<FloodStationsReponse> getStations(@RequestParam("stations") Set<Integer> stationNumbers) {
		var coveredAddresses = jsonUtils.get(Firestation.class).stream()
				.filter(firestation -> stationNumbers.contains(firestation.getStation()))
				.map(Firestation::getAddress).sorted().toList();

		var coveredPeople = getPersonDataStream()
				.filter(person -> coveredAddresses.contains(person.address())).toList();

		return ResponseEntity.ok(new FloodStationsReponse(coveredAddresses, coveredPeople)); // TODO
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
	public ResponseEntity<PersonInfoResponse> getPersonInfo(@RequestParam("lastName") String lastName) {
		log.info("Searching people with last name {}", lastName);

		var matchingLastName = getPersonDataStream()
				.filter(personData -> personData.lastName().equals(lastName)).toList();
		log.debug("Matching last names: {}", matchingLastName);

		if (!matchingLastName.isEmpty()) {
			var response = new PersonInfoResponse(matchingLastName);
			log.info("{} with last name {} found", response.getFoundPersons().size(), lastName);

			return ResponseEntity.ok(response);
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
	public ResponseEntity<Set<String>> getCommunityEmail(@RequestParam("city") String city) {
		var communityEmail = jsonUtils.get(Person.class).stream()
				.filter(person -> person.getCity().equals(city))
				.map(Person::getEmail)
				.collect(toUnmodifiableSet());

		if (!communityEmail.isEmpty()) {
			return ResponseEntity.ok(communityEmail);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
