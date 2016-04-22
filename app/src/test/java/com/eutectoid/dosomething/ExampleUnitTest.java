package com.eutectoid.dosomething;

import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;

import com.eutectoid.dosomething.MainActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest extends FriendsListFragment {
    @Test
    public void idToLong_isCorrect() throws Exception {
        final User user = new User("12345678910");
        final long expected = 12345678910L;
        final long reality = idToLong(user);
        assertEquals(expected,reality);
    }

}

public class TestActivity extends ActivityInstrumentationTestCase2<MainActivity> {
    public unit setup() {

    }
    public void testActivityTestCaseSetUpProperly() {
        assertNotNull("activity should be launched successfully",
                getActivity());
    }
}