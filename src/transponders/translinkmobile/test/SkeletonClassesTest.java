package transponders.translinkmobile.test;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import transponders.translinkmobile.Route;
import transponders.translinkmobile.Stop;
import transponders.translinkmobile.StopRoute;
import junit.framework.TestCase;

public class SkeletonClassesTest extends TestCase{
	public SkeletonClassesTest() {
		super();
		
	}
	
	@Override
	public void setUp() {
		
	}
	
	public void testStopAndRouteAndStopRoute() {
		Stop stop = new Stop("00", "Description", "2", new LatLng(1,1));
		assertEquals("00", stop.getId());
		assertEquals("Description", stop.getDescription());
		assertEquals("2", stop.getServiceType());
		assertEquals(1, stop.getPosition().latitude);
		
		Route route = new Route("111", "Super Route", 2);
		assertEquals("111", route.getCode());
		assertEquals("Super Route", route.getDescription());
		assertEquals(2, route.getType());
		
		StopRoute stopRoute = new StopRoute(stop, route);
		Date date = new Date();
		Date date2 = new Date(date.getTime() + 500);
		stopRoute.addTime(date);
		stopRoute.addTime(date2);
		ArrayList<Date> times = stopRoute.getTimes();
		
		assertEquals(date.getTime(), times.get(0).getTime()); 
		assertEquals(date2.getTime(), times.get(1).getTime());
				
	}
}
