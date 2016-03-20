package com.candkpeters.ceol.view;

import android.test.InstrumentationTestCase;
import android.util.Log;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers.*;

//import static org.junit.Assert.assertThat;

//import junit.framework.TestCase;

/**
 * Created by crisp on 29/01/2016.
 */
//@RunWith(AndroidJUnit4.class)
public class UIThreadUpdaterTest extends InstrumentationTestCase {

    private static final String TAG = "UIThreadUpdaterTest";
    public Integer counter;

    public class TheDoer implements Runnable {

        private static final String TAG = "TheDoer";
        UIThreadUpdater updater;

        public TheDoer( int repeatMsecs ) {
            this.updater = new UIThreadUpdater(this, repeatMsecs);
        }

        public void start() {
            updater.startUpdates();
        }

        public void stop() {
            updater.stopUpdates();
        }

        public void fireOnce() { updater.fireOnce();}

        @Override
        public void run() {
            Log.d(TAG, "run: Counter=" + counter);
            synchronized (counter) {
                counter --;
            }
        }
    }

    TheDoer theDoer;

    @Test
    public void testRegularCall() throws Exception {
        theDoer = new TheDoer(4000);
        counter = 5;
        theDoer.start();

        long timeoutMsec = 20000 + System.currentTimeMillis();
        while (System.currentTimeMillis() < timeoutMsec) {
            Log.d(TAG, "testRegularCall: Time left = " + (timeoutMsec - System.currentTimeMillis()));
            Thread.sleep(1000, 0);
            Log.d(TAG, "testRegularCall: Counter="+counter);

            if (timeoutMsec-System.currentTimeMillis() <= 10000) {
                Log.d(TAG, "testRegularCall: fireOnce");
                theDoer.fireOnce();
            }
            if (counter <= 0) {
                break;
            }
        }
        assertThat(counter, is(lessThanOrEqualTo(0)));

    }

}