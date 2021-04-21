package ex.home365.airlines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "")
@RestController
@RequestMapping("/api")
public class MainSystem {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainSystem.class);
	
	@Autowired
	private Destinations destinations;
	
	@Autowired
	private Airlines airlines;
	
	@Autowired
	private Aircrafts aircrafts;
	
	void saveDestination(Destination destination) {
		destinations.saveAndFlush(destination);
	}
	
	void saveAirline(Airline airline) {
		airlines.saveAndFlush(airline);
	}
	
	void saveAircraft(Aircraft aircraft) {
		aircrafts.saveAndFlush(aircraft);
	}
	
	private Airline retrieveAirline(String name) throws IllegalArgumentException {
		Optional<Airline> airline = airlines.findById(name);
		
		if (airline == null || airline == Optional.<Airline>empty()) {
			throw new IllegalArgumentException("Airline " + name + " not found!");
		}
		
		return airline.get().setMainSystem(this);
	}
	
	private Aircraft retrieveAircraft(int id) {
		Aircraft aircraft = aircrafts.getOne(id);
		
		if (aircraft == null) {
			throw new IllegalArgumentException("Aircraft with ID " + id + " not found!");
		}
		
		return aircraft;
	}
	
	private Destination retrieveDestination(String name) {
		return destinations.getOne(name);
	}
	
	List<Destination> retrieveDestinations() {
		return destinations.findAll();
	}
	
	@RequestMapping(value = "/add/airline")
	public ResponseEntity<Object> addAirline(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "initial", required = true) long initialBudget,
			@RequestParam(value = "altitude", required = true) double altitude,
			@RequestParam(value = "longitude", required = true) double longitude) {
		try {
			Airline airline = new Airline(name, initialBudget, altitude, longitude, this);
			saveAirline(airline);
		
			return new ResponseEntity<Object>(airline, HttpStatus.OK);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private static ResponseEntity<Object> generateAndLogThrowable(Throwable throwable, HttpStatus status) {
		String message = throwable.getMessage();
		LOGGER.error(message);
		return new ResponseEntity<Object>(message, status);
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	private static ResponseEntity<Object> handleMissingParams(MissingServletRequestParameterException exception) {
		return generateAndLogThrowable(exception, HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value = "/list/airlines")
	public ResponseEntity<Object> retrieveAirlines() {
		try {
			return new ResponseEntity<Object>(airlines.findAll(), HttpStatus.OK);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/add/aircraft")
	public ResponseEntity<Object> addAircraft(@RequestParam(value = "to", required = true) String to, @RequestParam(value = "price", required = true) long price, @RequestParam(value = "maxDistanceKm", required = true) int maxDistanceKm) {
		try {
			Aircraft aircraft = new Aircraft(price, maxDistanceKm);
			retrieveAirline(to).addAircraft(aircraft);
		
			return new ResponseEntity<Object>(aircraft, HttpStatus.OK);
		} catch (IllegalArgumentException illegalArgumentException) {
			return generateAndLogThrowable(illegalArgumentException, HttpStatus.BAD_REQUEST);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/sell/aircraft")
	public ResponseEntity<Object> sellAircraft(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to, @RequestParam(value = "aircraft", required = true) int aircraftId) {
		try {
			retrieveAirline(from).sellAircraft(retrieveAircraft(aircraftId), retrieveAirline(to));
			
			return new ResponseEntity<Object>(to + " bought aircraft with id " + aircraftId + " from " + from , HttpStatus.OK);
		} catch (IllegalArgumentException illegalArgumentException) {
			return generateAndLogThrowable(illegalArgumentException, HttpStatus.BAD_REQUEST);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/add/destination")
	public ResponseEntity<Object> addDestination(@RequestParam(value = "name", required = true) String name,
											@RequestParam(value = "altitude", required = true) double altitude,
											@RequestParam(value = "longitude", required = true) double longitude) {	
		try {
			Destination destination = new Destination(name, altitude, longitude);
			saveDestination(destination);
		
			return new ResponseEntity<Object>(destination, HttpStatus.OK);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/list/airline/distances")
	public ResponseEntity<Object> listDistances(@RequestParam(value = "airline", required = true) String airline) {
		try {
			return new ResponseEntity<Object>(retrieveAirline(airline).listDistances(), HttpStatus.OK);
		} catch (IllegalArgumentException illegalArgumentException) {
			return generateAndLogThrowable(illegalArgumentException, HttpStatus.BAD_REQUEST);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/list/airline/reachables")
	public ResponseEntity<Object> findReachableDestinations(@RequestParam(value = "airline", required = true) String airline) {		
		try {
			retrieveAirline(airline).updateDistances();
			List<String> reachableDestinationsNames = airlines.findReachableDestinations(airline);
			Collection<Destination> reachableDestinations = new ArrayList<Destination>();
			
			for (String destination : reachableDestinationsNames) {
				reachableDestinations.add(retrieveDestination(destination));
			}
			
			return new ResponseEntity<Object>(reachableDestinations, HttpStatus.OK);
		} catch (IllegalArgumentException illegalArgumentException) {
			return generateAndLogThrowable(illegalArgumentException, HttpStatus.BAD_REQUEST);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/buy/aircraft")
	public ResponseEntity<Object> buyAircraft(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "buyer", required = true) String buyer, @RequestParam(value = "aircraft", required = true) int aircraftId) {
		try {
			retrieveAirline(from).sellAircraft(retrieveAircraft(aircraftId), retrieveAirline(buyer));
		
			return new ResponseEntity<Object>(HttpStatus.OK);
		} catch (IllegalArgumentException illegalArgumentException) {
			return generateAndLogThrowable(illegalArgumentException, HttpStatus.BAD_REQUEST);
		} catch (Throwable throwable) {
			return generateAndLogThrowable(throwable, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}