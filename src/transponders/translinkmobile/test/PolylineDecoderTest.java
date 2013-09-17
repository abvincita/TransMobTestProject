package transponders.translinkmobile.test;

import java.util.List;

import transponders.translinkmobile.PolylineDecoder;
import junit.framework.TestCase;

import com.google.android.gms.maps.model.LatLng;

public class PolylineDecoderTest extends TestCase {
	public PolylineDecoderTest() {
		super();
		
	}
	
	@Override
	public void setUp() {
		
	}
	public void testPolylineDecoder() {
		LatLng point1 = new LatLng(41.69957665997156, -87.68943786621094);
		LatLng point2 = new LatLng(41.72315557551985, -87.56103515625);
		LatLng point3 = new LatLng(41.6226288146378, -87.57545471191406);
		String encodedPolyline = "kmo}F~yevOkrCoaXhsR`yA";
		List<LatLng> list = PolylineDecoder.decodePoly(encodedPolyline);
		assertEquals(point1.latitude, list.get(0).latitude);
		assertEquals(point1.longitude, list.get(0).longitude);
		assertEquals(point2.latitude, list.get(1).latitude);
		assertEquals(point2.longitude, list.get(1).longitude);
		assertEquals(point3.latitude, list.get(2).latitude);
		assertEquals(point3.longitude, list.get(2).longitude);
		
	}
}
