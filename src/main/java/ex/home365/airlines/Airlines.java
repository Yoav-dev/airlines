package ex.home365.airlines;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Airlines extends JpaRepository<Airline, String> {
	@Query(nativeQuery = true, value = "SELECT DISTINCT(DISTANCES_KEY) FROM "
			+ "(SELECT * FROM AIRCRAFT INNER JOIN DESTINATION ON NAME = ?1 AND AIRLINE_NAME = NAME) AS DEST "
			+ "INNER JOIN "
			+ "AIRLINE_DISTANCES "
			+ "ON DISTANCES < MAX_DISTANCE_KM AND NOT DISTANCES_KEY = NAME AND AIRLINE_DISTANCES.AIRLINE_NAME = DEST.NAME")
	List<String> findReachableDestinations(String airlineName);
}