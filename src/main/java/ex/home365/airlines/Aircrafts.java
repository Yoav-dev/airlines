package ex.home365.airlines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Aircrafts extends JpaRepository<Aircraft, Integer> {
}