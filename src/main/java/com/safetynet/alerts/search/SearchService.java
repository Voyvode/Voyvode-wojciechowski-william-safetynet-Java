package com.safetynet.alerts.search;

import com.safetynet.alerts.firestation.FirestationDTO;
import com.safetynet.alerts.medicalrecord.MedicalRecordDTO;
import com.safetynet.alerts.person.PersonDTO;
import com.safetynet.alerts.search.result.*;
import com.safetynet.alerts.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

	private final JsonUtils jsonUtils;

	/**
	 *
	 * @param stationNumber
	 * @return
	 */
	public FirestationResult getFirestation(int stationNumber) {
		log.info("Searching fire station #{} coverage", stationNumber);

		var firestationCoverage = jsonUtils.get(FirestationDTO.class).stream()
				.filter(firestation -> firestation.getStation() == stationNumber)
				.map(FirestationDTO::getAddress).toList();
		log.debug("Covered addresses: {}", firestationCoverage);

		var coveredPeople = getPersonDataStream()
				.filter(personData -> firestationCoverage.contains(personData.address())).toList();
		log.debug("Covered people: {}", coveredPeople);

		return new FirestationResult(coveredPeople);
	}

	/**
	 *
	 *
	 * @param address
	 * @return
	 */
	public ChildAlertResult getChildAlert(String address) {
		log.info("Searching for children at {}", address);

		var household = getPersonDataStream()
				.filter(person -> person.address().equals(address)).toList();
		log.debug("Household: {}", household);

		return new ChildAlertResult(household);
	}

	/**
	 *
	 * @param firestationNumber
	 * @return
	 */
	public PhoneAlertResult getPhoneAlert(int firestationNumber) {
		log.info("Searching all phone numbers covered by fire station #{}", firestationNumber);

		var firestationCoverage = jsonUtils.get(FirestationDTO.class).stream()
				.filter(firestation -> firestation.getStation() == firestationNumber)
				.map(FirestationDTO::getAddress)
				.collect(toUnmodifiableSet());
		log.debug("Searched addresses: {}", firestationCoverage);

		var phoneNumbers = jsonUtils.get(PersonDTO.class).stream()
				.filter(person -> firestationCoverage.contains(person.getAddress()))
				.map(PersonDTO::getPhone)
				.collect(toUnmodifiableSet());

		return new PhoneAlertResult(phoneNumbers);
	}

	public FireResult getFire(String address) {
		log.info("Searching people and covering fire station at {}", address);

		var firestations = jsonUtils.get(FirestationDTO.class).stream()
				.collect(toMap(FirestationDTO::getAddress, FirestationDTO::getStation));

		var coveringStation = firestations.get(address);

		var household = getPersonDataStream()
				.filter(personData -> personData.address().equals(address)).toList();
		log.debug("Household: {}", household);

		return new FireResult(coveringStation, household);
	}

	/**
	 *
	 * @param stationNumbers
	 * @return
	 */
	public FloodStationsResult getFloodStations(Set<Integer> stationNumbers) {
		log.info("Searching all households covered by fire station(s) {}", stationNumbers);

		var coveredAddresses = jsonUtils.get(FirestationDTO.class).stream()
				.filter(firestation -> stationNumbers.contains(firestation.getStation()))
				.map(FirestationDTO::getAddress).sorted().toList();
		log.debug("Covered addresses: {}", coveredAddresses);

		var coveredPeople = getPersonDataStream()
				.filter(person -> coveredAddresses.contains(person.address())).toList();
		log.debug("Covered people: {}", coveredPeople);

		return new FloodStationsResult(coveredAddresses, coveredPeople);
	}

	/**
	 *
	 * @param lastName
	 * @return
	 */
	public PersonInfoResult getPersonInfo(String lastName) {
		log.info("Searching people with last name {}", lastName);

		var matchingLastName = getPersonDataStream()
				.filter(personData -> personData.lastName().equals(lastName)).toList();

		log.debug("Matching last names: {}", matchingLastName);

		return new PersonInfoResult(matchingLastName);
	}

	/**
	 *
	 * @param city
	 * @return
	 */
	public CommunityEmailResult getCommunityEmail(String city) {
		log.info("Searching all email addresses in {}", city);

		var emails = jsonUtils.get(PersonDTO.class).stream()
				.filter(person -> person.getCity().equals(city))
				.map(PersonDTO::getEmail)
				.collect(toUnmodifiableSet());
		log.debug("Email addresses found: {}", emails);

		return new CommunityEmailResult(emails);
	}

	/**
	 * Return a stream of PersonData.
	 *
	 * <p>PersonData aggregates Person and MedicalRecord for easier search into known personal
	 * data.
	 *
	 * @return a Stream of PersonData objects
	 */
	private Stream<PersonData> getPersonDataStream() {
		var persons = jsonUtils.get(PersonDTO.class);
		var records = jsonUtils.get(MedicalRecordDTO.class).stream()
				.collect(toMap(MedicalRecordDTO::getFullName, x -> x));

		return persons.stream().map(person -> new PersonData(
				person.getFirstName(), person.getLastName(),
				person.getAddress(), person.getCity(), person.getZip(),
				person.getPhone(), person.getEmail(),
				records.get(person.getFullName()).getBirthdate(),
				records.get(person.getFullName()).getMedications(),
				records.get(person.getFullName()).getAllergies()
		));
	}

}