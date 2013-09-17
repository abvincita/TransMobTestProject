package transponders.translinkmobile.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;
import android.os.AsyncTask;

import transponders.translinkmobile.DisplayRoutesFragment;
import transponders.translinkmobile.JSONRequest;
import transponders.translinkmobile.JourneyPlanner;
import transponders.translinkmobile.MaintenanceNewsFragment;
import transponders.translinkmobile.MaintenanceNewsFragment.DownloadWebpageTask;
import transponders.translinkmobile.NearbyStops;
import transponders.translinkmobile.R;
import transponders.translinkmobile.Route;
import transponders.translinkmobile.ShowJourneyPage;
import transponders.translinkmobile.Stop;
import transponders.translinkmobile.StopDataLoader;

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
		menuList = (ListView) fragmentActivity.findViewById(transponders.translinkmobile.R.id.left_drawer_ns);
		navigationDrawer = (DrawerLayout) fragmentActivity.findViewById(transponders.translinkmobile.R.id.drawer_layout_ns);
		menuAdapter = menuList.getAdapter();
		
		stopDataLoader = fragmentActivity.getStopDataLoader();
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
	    	//testJourneyPlannerResult(jpFragment);
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
				assertEquals(s.getParentId(), "LM:Busway stations:PA Hospital station");
				matchedStop = s;
				break;
			} else if (s.getDescription().contains("Fake station. Let's burn together!")) {
				fail("This fail() cannot be reached, ever.");
				break;
			}
		}
		if (matchedStop == null) {
			fail("Could not find stop matching the given stop name");
		} else {
			assertEquals(matchedStop.getParentId(), "LM:Busway stations:PA Hospital station");
			ArrayList<Route> routes = matchedStop.getRoutes();
			assertNotNull(routes);
			boolean foundRoute105 = false;
			for (Route r: routes) {
				if (r.getCode().equals("105")) {
					foundRoute105 = true;
					assertEquals(r.getDescription(), "City, Boggo Rd, Fairfield, Yeronga, Tennyson, Indooroopilly");
					assertEquals(r.getType(), 2);
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
	
	public void testDisplayRoutesFragment() {
		Stop stop1 = new Stop("SI:002459","stop1 description", "2", new LatLng(0,0));
		Stop stop2 = new Stop("SI:000429","stop2 description", "2", new LatLng(0,0));
		ArrayList<Stop> savedStops = new ArrayList<Stop>();
		savedStops.add(stop1);
		savedStops.add(stop2);
		fragmentActivity.setSelectedStops(savedStops);
		
		//force swap the fragment to display routes
		/*activity.runOnUiThread(
	      new Runnable() {
	        public void run() {
	        	activity.openTimetableFragment();
	        }
	      }
	     );
		
		boolean found203 = false;
		boolean found204 = false;
		boolean found379 = false;*/
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
			
			assertEquals(resolvedParameters.get(0), oriResolved);
			assertEquals(resolvedParameters.get(1), destResolved);		    
		    
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

	public void testMaintenanceNews(MaintenanceNewsFragment mnf) throws InterruptedException
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
