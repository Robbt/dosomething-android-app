package com.eutectoid.dosomething;

import android.test.InstrumentationTestCase;

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