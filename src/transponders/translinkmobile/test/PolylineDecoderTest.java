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
		/*LatLng point1 = new LatLng(41.69957665997156, -87.68943786621094);
		LatLng point2 = new LatLng(41.72315557551985, -87.56103515625);
		LatLng point3 = new LatLng(41.6226288146378, -87.57545471191406);*/
		LatLng point1 = new LatLng(41.6996, -87.6894);
		LatLng point2 = new LatLng(41.7232, -87.5610);
		LatLng point3 = new LatLng(41.6226, -87.5754);
		String encodedPolyline = "kmo}F~yevOkrCoaXhsR`yA";
		List<LatLng> list = PolylineDecoder.decodePoly(encodedPolyline);
		assertEquals((double)Math.round(point1.latitude *10000)/10000, (double)Math.round(list.get(0).latitude * 10000)/10000);
		assertEquals((double)Math.round(point1.longitude *10000)/10000, (double)Math.round(list.get(0).longitude * 10000)/10000);
		assertEquals((double)Math.round(point2.latitude *10000)/10000, (double)Math.round(list.get(1).latitude * 10000)/10000);
		assertEquals((double)Math.round(point2.longitude *10000)/10000, (double)Math.round(list.get(1).longitude * 10000)/10000);
		assertEquals((double)Math.round(point3.latitude *10000)/10000, (double)Math.round(list.get(2).latitude * 10000)/10000);
		assertEquals((double)Math.round(point3.longitude *10000)/10000, (double)Math.round(list.get(2).longitude * 10000)/10000);
	}
}
