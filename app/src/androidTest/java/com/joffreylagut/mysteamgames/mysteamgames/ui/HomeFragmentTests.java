package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.joffreylagut.mysteamgames.mysteamgames.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * HomeFragmentUnitTests.java
 * Purpose: Unit tests for HomeFragmentTests.java.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-09
 */

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTests {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void displayFavorites_noFavorites() {
        onView(withId(R.id.action_home)).perform(click());

    }
}
