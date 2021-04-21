package ex.home365.airlines;

import java.time.Duration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Aircraft implements Comparable<Aircraft> {
	private static final long MILLISECONDS_IN_MONTH = Duration.ofDays(30).toMillis();
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column
	private long price;
	
	@Column
	private int maxDistanceKm;
	
	@Column
	private long creationTime = System.currentTimeMillis();
	
	@ManyToOne
	private Airline airline;
	
	public Aircraft() {
	}
	
	public Aircraft(long price, int maxDistanceKm) {
		setPrice(price);
		setMaxDistanceKm(maxDistanceKm);
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public int getMaxDistanceKm() {
		return maxDistanceKm;
	}

	public void setMaxDistanceKm(int maxDistanceKm) {
		this.maxDistanceKm = maxDistanceKm;
	}

	public Airline getAirline() {
		return airline;
	}

	public void setAirline(Airline airline) {
		this.airline = airline;
	}
	
	public long getCurrentPrice() {
		long monthsInUse = (System.currentTimeMillis() - creationTime) / MILLISECONDS_IN_MONTH;
		
		return (long)(price * 1 - (monthsInUse * 0.02));
	}
	
	@Override
	public boolean equals(Object airCraft) {
		return id == ((Aircraft) airCraft).id;
	}

	@Override
	public int compareTo(Aircraft airCraft) {
		return id - airCraft.id;
	}
}