package ex.home365.airlines;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.spatial4j.core.distance.DistanceUtils;

@Entity
public class Destination implements Comparable<Destination>{
	@Id
	@Column
	private String name;
	
	// In Radians
	@Column
	private double altitude;
	
	// In Radians
	@Column
	private double longitude;
	
	public Destination() {
		
	}
	
	public Destination(String name, double altitude, double longitude) {
		setName(name);
		setAltitude(altitude);
		setLongitude(longitude);
	}
	
	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	double haversine(Destination fromDestination) {
		double distRAD = DistanceUtils.distHaversineRAD(altitude, longitude, fromDestination.altitude, fromDestination.longitude);
		
		return DistanceUtils.radians2Dist(distRAD, DistanceUtils.EARTH_MEAN_RADIUS_KM);
	}

	@Override
	public int compareTo(Destination destination) {
		return name.compareTo(destination.name);
	}
}