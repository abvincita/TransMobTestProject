package transponders.translinkmobile.test;

import android.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.KeyEvent;

import transponders.translinkmobile.NearbyStops;

public class NearbyStopsTest extends ActivityInstrumentationTestCase2<NearbyStops> {

	private NearbyStops activity;
	private ListView menuList;
	private ListAdapter menuAdapter;
	private String selectedString;
	private int mPos;
	public static final int ADAPTER_COUNT = 3;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 3;
	
	
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
	}
}
