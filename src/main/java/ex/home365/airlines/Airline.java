package ex.home365.airlines;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * @author yoavp
 *
 */
@Entity
public class Airline extends Destination {
	@Column
	private AtomicLong balance = new AtomicLong();
	
	@OneToMany
	private Set<Aircraft> aircrafts = new ConcurrentSkipListSet<Aircraft>();

	@ElementCollection
	private Map<Destination, Double> distances = new ConcurrentHashMap<Destination, Double>();
	
	@Transient
	private MainSystem mainSystem;
	
	public Airline() {
	}
	
	public Airline(String name, long balance, double altitude, double longitude, MainSystem mainSystem) {
		super(name, altitude, longitude);
		setBalance(balance);
		this.mainSystem = mainSystem;
	}

	public long getBalance() {
		return balance.longValue();
	}

	public void setBalance(long balance) {
		this.balance.set(balance);
	}
	
	void addAircraft(Aircraft aircraft) {
		aircrafts.add(aircraft);
		aircraft.setAirline(this);
		mainSystem.saveAircraft(aircraft);
		mainSystem.saveDestination(this);
	}
	
	private void pay(long sum, Airline payTo) {
		balance.set(balance.longValue() - sum);
		payTo.balance.set(payTo.balance.longValue() + sum);
		mainSystem.saveDestination(payTo);
		mainSystem.saveDestination(this);
	}
	
	void sellAircraft(Aircraft aircraft, Airline sellTo) {
		long aircraftCurrentPrice = aircraft.getCurrentPrice();
		
		if (!aircrafts.remove(aircraft)) {
			throw new IllegalArgumentException("Aircraft with ID " + aircraft.getId() + " does not belong to airline " + getName());
		}
		
		sellTo.addAircraft(aircraft);
		sellTo.pay(aircraftCurrentPrice, this);
		mainSystem.saveAircraft(aircraft);
		mainSystem.saveDestination(this);
		mainSystem.saveDestination(sellTo);
	}
	
	void buyAircraft(Aircraft aircraft, Airline buyFrom) {
		buyFrom.sellAircraft(aircraft, this);
	}
	
	void updateDistances() {
		List<Destination> destinationsList = mainSystem.retrieveDestinations();
		int destinationsListSize = destinationsList.size();
		
		for (int i = distances.size(); i < destinationsListSize; i++) {
			Destination destination = destinationsList.get(i);
			
			if (!destination.getName().equals(getName())) {
				distances.put(destination, haversine(destination));
			}
		}
		
		mainSystem.saveAirline(this);
	}
	
	public Map<Destination, Double> listDistances() {
		updateDistances();
		
		return distances;
	}
	
	Airline setMainSystem(MainSystem mainSystem) {
		this.mainSystem = mainSystem;
		
		return this;
	}
}