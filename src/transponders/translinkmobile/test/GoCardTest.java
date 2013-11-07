package transponders.translinkmobile.test;

import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

public class GoCardTest extends TestCase {

	private CountDownLatch lock;
	
	public GoCardTest() {
		super();
	}
	
	@Override
	public void setUp() {
		lock = new CountDownLatch(1);
		
	}
}
