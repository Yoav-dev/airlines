package ex.home365.airlines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Destinations extends JpaRepository<Destination, String> {
}