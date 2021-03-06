package transponders.translinkmobile.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutionException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ArrayAdapter;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;
import android.os.AsyncTask;

import transponders.transmob.R;

import transponders.transmob.DisplayRoutesFragment;
import transponders.transmob.GocardDisplayFragment;
import transponders.transmob.GocardLoginFragment;
import transponders.transmob.JSONRequest;
import transponders.transmob.JourneyPlanner;
import transponders.transmob.MaintenanceNewsFragment;
import transponders.transmob.NearbyStops;
import transponders.transmob.Route;
import transponders.transmob.RouteStopsLoader;
import transponders.transmob.ShowJourneyPage;
import transponders.transmob.Stop;
import transponders.transmob.StopDataLoader;
import transponders.transmob.MaintenanceNewsFragment.DownloadWebpageTask;
import transponders.transmob.Trip;

public class NearbyStopsTest extends ActivityInstrumentationTestCase2<NearbyStops> {

	public static final int ADAPTER_COUNT = 3;
	public static final int INITIAL_POSITION = 0;
	public static final int JP_POSITION = 1;
	public static final int MN_POSITION = 2;
	
	public static final int YEAR = 2013;
	public static final int MONTH = 8;
	public static final int DAY = 20;
	public static final int HOUR = 16;
	public static final int MINUTE = 0;
	public static final String origin = "UQ";
	public static final String destination = "Myer Centre";
	public static final String oriResolved = "LM:Bus Stations And Interchanges:UQ Chancellors Place";
	public static final String destResolved = "LM:Shopping Centres:Myer Centre";
	public static final String jpResultURL = "http://jp.translink.com.au/travel-information/journey-planner/saved-journey?from=UQ+Chancellors+Place+(Bus+Stations+And+Interchanges)&to=Myer+Centre+(Shopping+Centres)&timeMode=LeaveAfter&date=2013-09-20T16%3a00%3a00&time=2013-09-20T16%3a00%3a00&mode=30&walkSpeed=Normal&maximumWalk=1000&_source=api";
	
	private NearbyStops fragmentActivity;
	private JourneyPlanner jpFragment;
	private MaintenanceNewsFragment mnFragment;
	private ShowJourneyPage showJPFragment;
	
	private ListView menuList;
	private DrawerLayout navigationDrawer;
	private ListAdapter menuAdapter;
	private String selectedString;
	private int mPos;
	
	private String resultTitle;
	
	private StopDataLoader stopDataLoader;
	private RouteStopsLoader routeStopsLoader;
	
	
	public NearbyStopsTest()
	{
		super(NearbyStops.class);
	}
	
	@Override  
	protected void setUp() throws Exception
	{
		super.setUp();
	
		setActivityInitialTouchMode(false);
	
		fragmentActivity = getActivity();
		menuList = (ListView) fragmentActivity.findViewById(transponders.transmob.R.id.left_drawer_ns);
		navigationDrawer = (DrawerLayout) fragmentActivity.findViewById(transponders.transmob.R.id.drawer_layout_ns);
		menuAdapter = menuList.getAdapter();
		

		stopDataLoader = fragmentActivity.getStopDataLoader();

		stopDataLoader = fragmentActivity.getStopDataLoader();
		routeStopsLoader = fragmentActivity.getRouteStopsLoader();

	} 
	
	public void testPreConditions() 
	{
		assertNotNull(fragmentActivity);
	    assertTrue(menuAdapter != null);
	    assertEquals(menuAdapter.getCount(), ADAPTER_COUNT);
	}
	
	public void testJourneyPlannerFromDrawerUI() throws InterruptedException {
		
		MyRunnable myRunnable = new MyRunnable();
		
	    synchronized(myRunnable)
	    {
	    	fragmentActivity.runOnUiThread(myRunnable);
	    	 myRunnable.wait(); 
	    }
	    
	   /* this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	    
	    for (int i = 0; i < JP_POSITION; i++) 
	    {
	      this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
	    } 

	    this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);*/    
	    
	    mPos = menuList.getSelectedItemPosition();
	    selectedString = (String) menuList.getItemAtPosition(mPos);
	    
	    ActionBar actionBar = (ActionBar) fragmentActivity.getActionBar();
	    resultTitle = (String) actionBar.getTitle();
	    
	    Log.d("MPOS", mPos + "");
	    Log.d("SELECTEDSTRING", selectedString);
	    Log.d("TITLE", resultTitle);
	    
	    assertEquals(resultTitle, selectedString);
	    	    
	    if(mPos == 1)
	    {
	    	jpFragment = fragmentActivity.getJourneyPlannerFragment();
	    	assertNotNull(jpFragment);
	    	testJourneyPlannerResult(jpFragment);
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
			fragmentActivity.runOnUiThread(
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
		Stop stop1 = new Stop("002459","stop1 description", "8", new LatLng(0,0));
		stop1.addRoute(new Route("203","testroute",8, 1));
		stop1.addRoute(new Route("204","testroute",8, 1));
		Stop stop2 = new Stop("000429","stop2 description", "8", new LatLng(0,0));
		stop2.addRoute(new Route("379","testroute",8, 1));
		ArrayList<Stop> savedStops = new ArrayList<Stop>();
		savedStops.add(stop1);
		savedStops.add(stop2);
		fragmentActivity.setSelectedStops(savedStops);
		
		//force swap the fragment to display routes
		fragmentActivity.runOnUiThread(
	      new Runnable() {
	        public void run() {
	        	fragmentActivity.openTimetableFragment("TITLE");
	        }
	      }
	     );
		
		boolean found203 = false;
		boolean found204 = false;
		boolean found379 = false;
		CountDownLatch lock = new CountDownLatch(1);
		
		
		DisplayRoutesFragment displayRouteFragment = (DisplayRoutesFragment) fragmentActivity.getContentFragment();
		int count =0;
		while (displayRouteFragment == null) {
			displayRouteFragment = (DisplayRoutesFragment) fragmentActivity.getContentFragment();
			Log.d("TestCase", "fragment null");
			if (++count == 50000) {
				fail("Activity content frame remained null");
			}
		}
		//displayRouteFragment.setCompletedAsyncTasksLatch(lock);
		/*
		count = 0;
		ArrayAdapter<String> adapter = displayRouteFragment.getAdapter();
		while (adapter == null) {
			adapter = displayRouteFragment.getAdapter();
			if (++count == 500) {
				fail("Adapter remained null");
			}
		}
		count = 0;
		RouteDataLoader routeDataLoader = displayRouteFragment.getRouteDataLoader();
		while (routeDataLoader == null) {
			routeDataLoader = displayRouteFragment.getRouteDataLoader();
			if (++count == 500) {
				fail("routeDataLoaderS remained null");
			}
		}*/
		displayRouteFragment.setCountDownLatch(lock);
		lock.await(50000, TimeUnit.MILLISECONDS);
		ArrayList<Trip> firstTrips = displayRouteFragment.getFirstTrips();
		//ArrayList<Route> routes = displayRouteFragment.getAvailableRoutes();

		

		for(Trip trip : firstTrips) {
			Route route = trip.getRoute();
			Log.d("TestCase", route.getCode());
			if (route.getCode().equalsIgnoreCase("203")) {
				found203=true;
			} else if (route.getCode().equalsIgnoreCase("204")) {
				found204=true;
			} else if (route.getCode().equalsIgnoreCase("379")) {
				found379=true;
			}
		}
		if (!found203) {
			fail("Could not find route 203 in the DisplayRoutesFragment");
		} else if (!found204) {
			fail("Could not find route 204 in the DisplayRoutesFragment");
		} else if (!found379) {
			fail("Could not find route 379 in the DisplayRoutesFragment");
		}
		
		assertEquals(3, firstTrips.size());
	}
	
	/**
	 * check the login fragment for GoCards.
	 * Requires a file called 'login.txt' in working directory folder, with a single line: <gocardnumber> <password>
	 * @throws InterruptedException
	 */
	public void testGocardLoginFragment() throws InterruptedException{
		//force swap the fragment to display routes
		OpenGocardLoginFragmentRunnable openGocardLoginFragmentRunnable = new OpenGocardLoginFragmentRunnable();
		Log.d("TestCase", "1");
	    synchronized(openGocardLoginFragmentRunnable)
	    {
	    	fragmentActivity.runOnUiThread(openGocardLoginFragmentRunnable);
	    	openGocardLoginFragmentRunnable.wait(); 
	    }
		CountDownLatch lock = new CountDownLatch(1);
		GocardLoginFragment gocardLoginFragment = (GocardLoginFragment) fragmentActivity.getContentFragment();
		int count =0;
		while (gocardLoginFragment == null) {
			gocardLoginFragment = (GocardLoginFragment) fragmentActivity.getContentFragment();
			if (++count == 140000) {
				fail("Activity content frame remained null");
				return;
			}
		}
		
		//Make sure onCreateView is called
		//gocardLoginFragment.setCountDownLatch(lock);
		//lock.await(10000, TimeUnit.MILLISECONDS);
		lock=new CountDownLatch(1);
		gocardLoginFragment.setCountDownLatch(lock);
		
		//Test for invalid login
		GocardLoginFragmentSubmitButtonRunnable gocardLoginFragmentSubmitButtonRunnable = new GocardLoginFragmentSubmitButtonRunnable (gocardLoginFragment, "12345", "badPass");
		Log.d("TestCase", "2");
		synchronized(gocardLoginFragmentSubmitButtonRunnable) {
			fragmentActivity.runOnUiThread(gocardLoginFragmentSubmitButtonRunnable);
			gocardLoginFragmentSubmitButtonRunnable.wait();
		}
		lock.await(40000, TimeUnit.MILLISECONDS);
		assertEquals(View.VISIBLE, gocardLoginFragment.getWrongPassWarning().getVisibility());
		Log.d("TestCase", "2-A");
		//Check login details are added for testing
	    BufferedReader br;
	    String fileStr = "";
		try {
			Log.d("TestCase", "Absolute file path is " + new File(".").getAbsolutePath());
			Log.d("TestCase", "Context path is "+ getInstrumentation().getContext().getFilesDir());
			br = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("login.txt")));
		
	        StringBuilder sb = new StringBuilder();
	        String line;
			
				line = br.readLine();
			

	        while (line != null) {
	            sb.append(line);
	            sb.append('\n');
	            line = br.readLine();
	        }
	        fileStr = sb.toString();
	        br.close();
		} catch (FileNotFoundException e) {
			fail("Could not read 'login.txt'. Ensure it is located in working directory folder");
			return;
		} catch (IOException e) {
			fail("IOError on assets/login.txt");
			e.printStackTrace();
			return;
		}
		String[] args = fileStr.split(" ");
		if (args.length != 2) {
			fail("Error in 'assets/login.txt'. Ensure it is a single line: <gocardnumber> <password>");
			return;
		}
		
		
		lock = new CountDownLatch(1);
		gocardLoginFragment.setCountDownLatch(lock);
		
		Log.d("TestCase", ""+args[0]+", "+args[1]);
		//Test for valid login
		gocardLoginFragmentSubmitButtonRunnable = new GocardLoginFragmentSubmitButtonRunnable (gocardLoginFragment, args[0], args[1]);
		Log.d("TestCase", "3");
		synchronized(gocardLoginFragmentSubmitButtonRunnable) {
			fragmentActivity.runOnUiThread(gocardLoginFragmentSubmitButtonRunnable);
			gocardLoginFragmentSubmitButtonRunnable.wait();
		}
		Log.d("TestCase", "3-A");
		lock.await(40000, TimeUnit.MILLISECONDS);
		Log.d("TestCase", "3-B");
		assertEquals(View.INVISIBLE, gocardLoginFragment.getWrongPassWarning().getVisibility());
		Log.d("TestCase", "3-C");
		
		
		count =0;
		Log.d("TestCase", "4");
		while (fragmentActivity.getContentFragment().getClass() != GocardDisplayFragment.class) {
			//gocardDisplayFragment = (GocardDisplayFragment) fragmentActivity.getContentFragment();
			if (++count == 140000) {
				fail("Fragment did not change to GocardDisplayFragment");
				return;
			}
		}
		Log.d("TestCase", "5");
		GocardDisplayFragment gocardDisplayFragment = (GocardDisplayFragment) fragmentActivity.getContentFragment();
		
		lock = new CountDownLatch(1);
		gocardDisplayFragment.setCountDownLatch(lock);
		
		lock.await(40000, TimeUnit.MILLISECONDS);
		TableLayout balanceTable = gocardDisplayFragment.getBalanceTable();
		TableLayout historyTable = gocardDisplayFragment.getHistoryTable();
		
		assertEquals(true, (balanceTable.getChildCount() > 0));
		assertEquals(true, (historyTable.getChildCount() > 0));
		
	}
	
	public void testJourneyPlannerResult(JourneyPlanner jpf) throws InterruptedException
	{   
			View jpView = jpf.getView();
			assertNotNull(jpView);
			
			EditText fromText = (EditText) jpView.findViewById(R.id.fromLocation);
			EditText destText = (EditText) jpView.findViewById(R.id.toLocation);
			Button button = (Button) jpView.findViewById(R.id.sendDestButton);
			
			assertNotNull(fromText);
			assertNotNull(destText);
			assertNotNull(button);
			
			fromText.setText(origin);
			destText.setText(destination);
			jpf.setDate(YEAR, MONTH, DAY);
			jpf.setTime(HOUR, MINUTE);
			
			SubmitButtonRunnable buttonRunnable = new SubmitButtonRunnable(button);
			
		    synchronized(buttonRunnable)
		    {
		    	fragmentActivity.runOnUiThread(buttonRunnable);
		    	buttonRunnable.wait(); 
		    }
		    
		    JSONRequest request = jpFragment.getJSONRequest();
			
			try {
				String done = request.get();
		    	   
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		    
			
			List<String> resolvedParameters = jpf.getResolvedParameters();
			assertNotNull(resolvedParameters);
			
			assertEquals(oriResolved, resolvedParameters.get(0));
			assertEquals(destResolved, resolvedParameters.get(1));		    
		    
		    Log.d("BUTTON ONCLICK", "before getShowJPFragment");
    	    showJPFragment = jpFragment.getShowJourneyPageFragment();
			
			assertNotNull(showJPFragment);
			
			JSONRequest showJPRequest = showJPFragment.getJSONRequest();
			try {
				String done = showJPRequest.get();
		    	   
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String resultURL = showJPFragment.getResultURL();
				
			assertNotNull(resultURL);
			
			assertEquals(resultURL, jpResultURL);
	}
	
	private class MyRunnable implements Runnable
	{
		public void run()
		{
			navigationDrawer.openDrawer(menuList);
        	menuList.setSelection(JP_POSITION);
        	View v = menuAdapter.getView(JP_POSITION, null, null);
        	long id = menuAdapter.getItemId(JP_POSITION);
    		menuList.performItemClick(v, JP_POSITION, id);
        
    		FragmentManager m = fragmentActivity.getSupportFragmentManager();
    	    m.executePendingTransactions();
    	    
			synchronized(this)
			{
				this.notify() ;
			}
		   
		}
	}
	
	public void testMaintenanceNewsFromDrawerUI() throws InterruptedException {
		
		SelectMaintenanceNews myRunnable = new SelectMaintenanceNews();
		
	    synchronized(myRunnable)
	    {
	    	fragmentActivity.runOnUiThread(myRunnable);
	    	myRunnable.wait(); 
	    }
	    
	    mPos = menuList.getSelectedItemPosition();
	    selectedString = (String) menuList.getItemAtPosition(mPos);
	    
	    ActionBar actionBar = (ActionBar) fragmentActivity.getActionBar();
	    resultTitle = (String) actionBar.getTitle();
	    
	    Log.d("MPOS", mPos + "");
	    Log.d("SELECTEDSTRING", selectedString);
	    Log.d("TITLE", resultTitle);
	    
	    assertEquals(resultTitle, selectedString);
	    	    
	    if(mPos == 2)
	    {
	    	mnFragment = fragmentActivity.getMaintenanceNewsFragment();
	    	assertNotNull(mnFragment);
	    	testMaintenanceNews(mnFragment);
	    }  
	}
	
	public void testRouteStopsLoader() throws InterruptedException {
		
		//Test the trip version
		
		//Trip trip = new Trip ()
		
		//Get the route version
		
		Route route = new Route("209","Random Name", 2, 1);
		CountDownLatch lock = new CountDownLatch(1);
		routeStopsLoader.setCompletedAsyncTasksLatch(lock);
		routeStopsLoader.requestRouteStops(route);
		try {
			lock.await(40000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Request route stops was interrupted");
			return;
		}
		ArrayList<Stop> stops = routeStopsLoader.getStops();
		assertEquals(11, stops.size());
		
		//test the polyline
		lock = new CountDownLatch(1);
		/*LatLng point1 = new LatLng(41.69957665997156, -87.68943786621094);
		LatLng point2 = new LatLng(41.72315557551985, -87.56103515625);
		LatLng point3 = new LatLng(41.6226288146378, -87.57545471191406);
		String encodedPolyline = "kmo}F~yevOkrCoaXhsR`yA";
		routeStopsLoader.addLineToMap(encodedPolyline);*/

		
		
	}

	public void testMaintenanceNews(MaintenanceNewsFragment mnf)
	{   
			View mnView = mnf.getView();
			assertNotNull(mnView);
			
			TableLayout newsTable = (TableLayout) mnView.findViewById(R.id.newsTable);
			TextView newsDate = (TextView) mnView.findViewById(R.id.newsDate);
			
			assertNotNull(newsTable);
			assertNotNull(newsDate);
			
			DownloadWebpageTask downloadTask = mnf.getDownloadWebpageTask(); 
			
			try {
				downloadTask.get();
		    	   
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(true)
			{
				Log.d("TESTMAINTENANCENEWS", "inside while(true)");
				
				if(downloadTask.getStatus() == AsyncTask.Status.FINISHED)
				{
					String allTitles = mnf.getAllTitles();
					String allURLs = mnf.getAllURLs();
					
					assertNotNull(allTitles);
					assertNotNull(allURLs);
					
					MaintenanceNewsTest mnTest = new MaintenanceNewsTest(allTitles, allURLs);
					mnTest.testAllNews();

					break;
				}
			}

			Log.d("TESTMAINTENANCENEWS", "finished testMaintenanceNews");
			
	}
	
	private class SelectMaintenanceNews implements Runnable
	{
		public void run()
		{
			navigationDrawer.openDrawer(menuList);
        	menuList.setSelection(MN_POSITION);
        	View v = menuAdapter.getView(MN_POSITION, null, null);
        	long id = menuAdapter.getItemId(MN_POSITION);
    		menuList.performItemClick(v, MN_POSITION, id);
        
    		FragmentManager m = fragmentActivity.getSupportFragmentManager();
    	    m.executePendingTransactions();
    	    
			synchronized(this)
			{
				this.notify() ;
			}
		   
		}
	}
	
	private class OpenGocardLoginFragmentRunnable implements Runnable {

		@Override
		public void run() {
			
			fragmentActivity.openGocardLoginFragment();
			synchronized(this) {
				this.notify();
			}
		}
		
	}
	
	private class GocardLoginFragmentSubmitButtonRunnable implements Runnable {

		GocardLoginFragment gocardLoginFragment;
		String goCardId;
		String goCardPass;
		public GocardLoginFragmentSubmitButtonRunnable(GocardLoginFragment fragment, String goCardId, String goCardPass) {
			gocardLoginFragment = fragment;
			this.goCardId = goCardId;
			this.goCardPass = goCardPass;
		}
		
		@Override
		public void run() {
			synchronized(this) {
				gocardLoginFragment.setGocardNumber(goCardId);
				gocardLoginFragment.setPassword(goCardPass);
				Button loginButton = (Button) gocardLoginFragment.getView().findViewById(R.id.login_button);
				loginButton.performClick();
				//FragmentManager m = gocardLoginFragment.getFragmentManager();
				//m.executePendingTransactions();
				this.notify();
			}
			
			
		}
		
	}
	
	private class SubmitButtonRunnable implements Runnable
	{
		Button button;
		public SubmitButtonRunnable(Button b)
		{
			button = b;
		}
		
		public void run()
		{	
			synchronized(this)
			{
				button.performClick();
				FragmentManager m = jpFragment.getFragmentManager();
	    	    m.executePendingTransactions();
		    	   
				this.notify() ;
			}
			
		}
	}
}
