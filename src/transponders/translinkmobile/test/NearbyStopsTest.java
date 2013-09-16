package transponders.translinkmobile.test;

import java.util.ArrayList;

import android.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.KeyEvent;

import transponders.translinkmobile.JourneyPlanner;
import transponders.translinkmobile.MaintenanceNewsFragment;
import transponders.translinkmobile.NearbyStops;
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
				
		//Test for the bus stop PA Hospital station near -27.4967, 153.03418,  
		stopDataLoader.requestStopsNear(-27.4967, 153.03418, 1000);
		while (stopDataLoader.isLoading()) {
			;
		}
		ArrayList<Stop> stops = stopDataLoader.getStopsNear();
		boolean foundMatch = false;
		assertNotNull(stops);
		for (Stop s: stops) {
			if (s.getDescription().contains("PA Hospital station")) {
				foundMatch = true;
				break;
			} else if (s.getDescription().contains("Fake station. Let's burn together!")) {
				fail();
				break;
			}
		}
		if (!foundMatch) {
			fail();
		}
		
		//Test that the stops around UQ Lakes have the parent "UQ Lakes"
		stopDataLoader.requestStopsNear(-27.498037,153.017823, 1000);
		
	}
}
