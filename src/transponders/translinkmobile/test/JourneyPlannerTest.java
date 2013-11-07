package transponders.translinkmobile.test;

import java.util.Calendar;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import transponders.transmob.JourneyPlanner;

public class JourneyPlannerTest extends AndroidTestCase
{
	Calendar calFromJourneyPlanner;
	Calendar currentCalendar = Calendar.getInstance();
	
	public JourneyPlannerTest()
	{
		super();
	}
	
	public JourneyPlannerTest(Class<JourneyPlannerTest> activityClass) {
		super();
		testCurrentDate();
	}
	
	@SmallTest
	public void testCurrentDate()
	{
		calFromJourneyPlanner = JourneyPlanner.getCurrentDate();
		
		assert(calFromJourneyPlanner.equals(currentCalendar));
	}
}
