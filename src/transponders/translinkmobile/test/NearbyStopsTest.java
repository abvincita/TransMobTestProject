package transponders.translinkmobile.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.KeyEvent;


import transponders.translinkmobile.DisplayRoutesFragment;

import transponders.translinkmobile.JourneyPlanner;
import transponders.translinkmobile.MaintenanceNewsFragment;

import transponders.translinkmobile.NearbyStops;
import transponders.translinkmobile.R;
import transponders.translinkmobile.Route;
import transponders.translinkmobile.RouteDataLoader;
import transponders.translinkmobile.Stop;
import transponders.translinkmobile.StopDataLoader;

public class NearbyStopsTest extends ActivityInstrumentationTestCase2<NearbyStops> {

	public static final int ADAPTER_COUNT = 3;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 3;
	
	private NearbyStops activity;
	private JourneyPlanner jpFragment;
	private MaintenanceNewsFragment mnFragment;
	
	private ListView menuList;
	private ListAdapter menuAdapter;
	private String selectedString;
	private int mPos;
	
	private StopDataLoader stopDataLoader;
	
	
	public NearbyStopsTest()
	{
		super(NearbyStops.class);
	}
	
	@Override  
	protected void setUp() throws Exception
	{
		super.setUp();
	
		setActivityInitialTouchMode(false);
	
		activity = getActivity();
		menuList = (ListView) activity.findViewById(transponders.translinkmobile.R.id.left_drawer_ns);
	
		menuAdapter = menuList.getAdapter();
		
		stopDataLoader = activity.getStopDataLoader();
	} 
	
	public void testPreConditions() 
	{
	    assertTrue(menuAdapter != null);
	    assertEquals(menuAdapter.getCount(), ADAPTER_COUNT);
	}
	
	public void testDrawerUI() {

	    activity.runOnUiThread(
	      new Runnable() {
	        public void run() {
	          menuList.requestFocus();
	          menuList.setSelection(INITIAL_POSITION);
	        } // end of run() method definition
	      } // end of anonymous Runnable object instantiation
	    ); // end of invocation of runOnUiThread
	    
	    this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	    
	    for (int i = 1; i <= TEST_POSITION; i++) 
	    {
	      this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
	    } 

	    this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	    
	    mPos = menuList.getSelectedItemPosition();
	    selectedString = (String) menuList.getItemAtPosition(mPos);
	    
	    ActionBar resultView = (ActionBar) activity.getActionBar();
	    
	    String resultTitle = (String) resultView.getTitle();
	    
	    if(resultTitle.equalsIgnoreCase("Nearby Stops & Service ETA"))
	    	resultTitle = "Nearby Stops";
	    
	    assertEquals(resultTitle, selectedString);
	    
	    if(mPos == 1)
	    {
	    	jpFragment = activity.getJourneyPlannerFragment();
	    	assertNotNull(jpFragment);
	    }  
	    if(mPos == 2)
	    {
	    	mnFragment = activity.getMaintenanceNewsFragment();
	    	assertNotNull(mnFragment);
	    }  
	}
	
	public void testStopDataLoader() {
				
		//Test for the bus stop PA Hospital station, platform 2 near -27.4967, 153.03418,  
		stopDataLoader.requestStopsNear(-27.4967, 153.03418, 1000);
		while (stopDataLoader.isLoading()) {
			;
		}
		ArrayList<Stop> stops = stopDataLoader.getStopsNear();
		Stop matchedStop=null;
		assertNotNull(stops);
		for (Stop s: stops) {
			if (s.getDescription().contains("PA Hospital station, platform 2")) {
				assertEquals("LM:Busway stations:PA Hospital station", s.getParentId());
				matchedStop = s;
				break;
			}
		}
		if (matchedStop == null) {
			fail("Could not find stop matching the given stop name");
		} else {
			assertEquals("LM:Busway stations:PA Hospital station", matchedStop.getParentId());
			ArrayList<Route> routes = matchedStop.getRoutes();
			assertNotNull(routes);
			boolean foundRoute105 = false;
			for (Route r: routes) {
				if (r.getCode().equals("105")) {
					foundRoute105 = true;
					assertEquals("City, Boggo Rd, Fairfield, Yeronga, Tennyson, Indooroopilly", r.getDescription());
					assertEquals(2, r.getType());
					break;
				}
			}
			if (!foundRoute105) {
				fail("Couldn't find route 105");
			}
			ArrayList<Stop> groupedStops = stopDataLoader.getStopsFromParent(matchedStop);
			//find the platform 1 stop
			boolean foundPlatform1 = false;
			for (Stop s: groupedStops) {
				if (s.getDescription().equals("PA Hospital station, platform 1")) {
					foundPlatform1 = true;
					break;
				}
			}
			if (!foundPlatform1) {
				fail("Couldnot find platform1");
			}
			final Stop finalMatchedStop = matchedStop;
			activity.runOnUiThread(
					new Runnable() {
						public void run() {
		        	stopDataLoader.addSavedStopMarkersToMap(true);
						}
					}
					);
			//Check there is a Marker matching saved stop
			/*ArrayList<Marker> stopMarkers = activity.getStopMarkers();
			assertNotNull(stopMarkers);
			
			boolean foundMatchingMarker = false;
			
			for (final Marker m: stopMarkers) {
				activity.runOnUiThread(
					      new Runnable() {
					        public void run() {
					        	final double lat = m.getPosition().latitude;
					        }
					      }
					      );
				
					        	
				if (lat == finalMatchedStop.getParentPosition().latitude &&
						m.getPosition().longitude == finalMatchedStop.getParentPosition().longitude) {
					foundMatchingMarker = true;
				}
			}
			if (!foundMatchingMarker) {
				fail("Could not locate matching marker");
			}
						/*}
					}
					);*/
			
		}
		
		
	}
	
	public void testDisplayRoutesFragment() throws InterruptedException {
		Stop stop1 = new Stop("002459","stop1 description", "2", new LatLng(0,0));
		stop1.addRoute(new Route("203","testroute",2));
		stop1.addRoute(new Route("204","testroute",2));
		Stop stop2 = new Stop("000429","stop2 description", "2", new LatLng(0,0));
		stop2.addRoute(new Route("379","testroute",2));
		ArrayList<Stop> savedStops = new ArrayList<Stop>();
		savedStops.add(stop1);
		savedStops.add(stop2);
		activity.setSelectedStops(savedStops);
		
		//force swap the fragment to display routes
		activity.runOnUiThread(
	      new Runnable() {
	        public void run() {
	        	activity.openTimetableFragment();
	        }
	      }
	     );
		
		
		boolean found203 = false;
		boolean found204 = false;
		boolean found379 = false;
		CountDownLatch lock = new CountDownLatch(1);
		
		DisplayRoutesFragment displayRouteFragment = (DisplayRoutesFragment) activity.getContentFragment();
		int count =0;
		while (displayRouteFragment == null) {
			displayRouteFragment = (DisplayRoutesFragment) activity.getContentFragment();
			Log.d("TestCase", "fragment null");
			if (++count == 500) {
				fail("Activity content frame remained null");
			}
		}
		ArrayAdapter<String> adapter = displayRouteFragment.getAdapter();
		RouteDataLoader routeDataLoader = displayRouteFragment.getRouteDataLoader();
		List<String> lines = displayRouteFragment.getLines();
		routeDataLoader.setCompletedAsyncTasksLatch(lock);
		lock.await(40000, TimeUnit.MILLISECONDS);
		/*while(routeDataLoader.isLoading()) {
			;
		}*/
		
		
		assertEquals(3, lines.size());
		for(String str: lines) {
			if (str.contains("203")) {
				found203=true;
			} else if (str.contains("204")) {
				found204=true;
			} else if (str.contains("379")) {
				found379=true;
			}
		}
		if (!found203 || !found204 || !found379) {
			fail("Could not find all required routes in the DisplayRoutesFragment");
		}
		int adapterSize = adapter.getCount();
		for (int i=0; i<adapterSize; i++) {
			assertEquals(lines.get(i), adapter.getItem(i));
		}
		
	}
	
	/*public void testRouteStopsLoader() {
		
	}*/
}
