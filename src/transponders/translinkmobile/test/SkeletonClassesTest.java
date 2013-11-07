package transponders.translinkmobile.test;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import transponders.transmob.Route;
import transponders.transmob.Stop;
import transponders.transmob.StopTrip;
import transponders.transmob.Trip;
import junit.framework.TestCase;

public class SkeletonClassesTest extends TestCase{
	public SkeletonClassesTest() {
		super();
		
	}
	
	@Override
	public void setUp() {
		
	}
	
	public void testStopAndRouteAndStopTrip() {
		Stop stop = new Stop("00", "Description", "2", new LatLng(1,1));
		assertEquals("00", stop.getId());
		assertEquals("Description", stop.getDescription());
		assertEquals(2, stop.getServiceType());
		assertEquals((Double)1.0, stop.getPosition().latitude);
		
		Route route = new Route("111", "Super Route", 2, 1);
		assertEquals("111", route.getCode());
		assertEquals("Super Route", route.getDescription());
		assertEquals(2, route.getType());
		
		Trip trip = new Trip ("tripID", route);
		
		StopTrip stopTrip = new StopTrip(stop, trip);
		Date date = new Date();
		Date date2 = new Date(date.getTime() + 500);
		stopTrip.setTime(date);
		
		
		assertEquals(date.getTime(), stopTrip.getTime().getTime()); 
		
				
	}
}
