package transponders.translinkmobile.test;



import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;

import transponders.translinkmobile.JSONRequest;
import transponders.translinkmobile.Stop;
import junit.framework.TestCase;


public class JSONRequestTest extends TestCase {

	private CountDownLatch lock;
	//private String result;
	
	public JSONRequestTest() {
		super();
		
	}
	
	@Override
	public void setUp() {
		lock = new CountDownLatch(1);
		//testJSONRequest();
	}
	
	public void testJSONRequest() {
		JSONRequest request = new JSONRequest();
		TestNetworkListener listener = new TestNetworkListener();
		request.setListener(listener);
		request.execute("http://deco3801-010.uqcloud.net/jsonrequesttest.php");
		try {
			lock.await(30000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("InterruptedException");
			return;
		}
		assertEquals("Result ok", listener.getResult());
		
	}
	
	private class TestNetworkListener implements JSONRequest.NetworkListener{

		private String result;
		@Override
		public void networkRequestCompleted(String result) {
			this.result = result;
			lock.countDown();
			
			
		}
		
		public String getResult() {
			return result;
		}
		
	}

}
