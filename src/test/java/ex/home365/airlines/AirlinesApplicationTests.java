package ex.home365.airlines;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AirlinesApplicationTests {
	private static final String ADD_DESTINATION_API = "http://localhost:8080/api/add/destination?name=%s&altitude=%s&longitude=%s";
	private static final String ADD_AIRLINE_API = "http://localhost:8080/api/add/airline?name=%s&initial=%s&altitude=%s&longitude=%s";
	
	private void assertDestination(JsonObject destinationJson, String name, double altitude, double longitude) {
		String responseName = destinationJson.get(ResponseJsonKeys.DESTINATION_NAME).getAsString();
		double responseAltitude = destinationJson.get(ResponseJsonKeys.DESTINATION_ALTITUDE).getAsDouble();
		double responseLongitude = destinationJson.get(ResponseJsonKeys.DESTINATION_LONGITUDE).getAsDouble();
		Assertions.assertEquals(responseName, name);
		Assertions.assertEquals(responseAltitude, altitude);
		Assertions.assertEquals(responseLongitude, longitude);
		Assertions.assertEquals(destinationJson.keySet().size(), 3);
	}
	
	private void testAddDestination(String name, double altitude, double longitude) {
		RestTemplate restTemplate = new RestTemplateBuilder().build();
		JsonObject destinationJson = JsonParser.parseString(restTemplate.getForObject(String.format(ADD_DESTINATION_API, name, altitude, longitude), String.class)).getAsJsonObject();
		assertDestination(destinationJson, name, altitude, longitude);
	}
	
	private void assertAirline(JsonObject airlineJson, String name, long initial, double altitude, double longitude) {
		String responseName = airlineJson.get(ResponseJsonKeys.DESTINATION_NAME).getAsString();
		long responseInitial = airlineJson.get(ResponseJsonKeys.AIRLINE_BALANCE).getAsLong();
		double responseAltitude = airlineJson.get(ResponseJsonKeys.DESTINATION_ALTITUDE).getAsDouble();
		double responseLongitude = airlineJson.get(ResponseJsonKeys.DESTINATION_LONGITUDE).getAsDouble();
		Assertions.assertEquals(responseName, name);
		Assertions.assertEquals(responseInitial, initial);
		Assertions.assertEquals(responseAltitude, altitude);
		Assertions.assertEquals(responseLongitude, longitude);
		Assertions.assertEquals(airlineJson.keySet().size(), 4);
	}
	
	private void testAddAirline(String name, long initial, double altitude, double longitude) {
		RestTemplate restTemplate = new RestTemplateBuilder().build();
		JsonObject airlineJson = JsonParser.parseString(restTemplate.getForObject(String.format(ADD_AIRLINE_API, name, initial, altitude, longitude), String.class)).getAsJsonObject();
		assertAirline(airlineJson, name, initial, altitude, longitude);
	}
	
	@Test
	public void testAdd() {
		testAddAirline("First airline ltd", 3000000000L, 10.456, 100.6535);
		testAddDestination("Hawai", 109.446, 10.623);
	}
	
	@Test
	public void testListAirlines() {
		testAddAirline("Airlines ltd", 100000000, 10.456, 100.6535);
		testAddAirline("North pole lines", 9000000000L, 2000.1, 3000.9);
	}
}
