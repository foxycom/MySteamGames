package com.joffreylagut.mysteamgames.mysteamgames.ui;


import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

/**
 * GamesFragmentTests.java
 * Purpose: Tests GamesFragment.java methods.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-09
 */

@RunWith(AndroidJUnit4.class)
public class GamesFragmentTests {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void displayGamesFragment() {
        onView(withId(R.id.action_games)).perform(click());
    }

    // TODO See if it's possible to move that test in UnitTests
    @Test
    @Ignore
    public void createGameListItemsFromGameList_listEmpty() {
        List<OwnedGame> ownedGames = new ArrayList<>();
        List<GameListItem> gameListItems = GamesFragment.createGameListItemsFromGameList(ownedGames);
        assertTrue(gameListItems.size() == 0);
    }

    // TODO See if it's possible to move that test in UnitTests
    @Test
    @Ignore
    public void createGameListItemsFromGameList_listNull() {
        List<GameListItem> gameListItems = GamesFragment.createGameListItemsFromGameList(null);
        assertTrue(gameListItems.size() == 0);
    }

    /**
     * Check that the Floating action buttons are on the view.
     */
    @Test
    @Ignore
    public void floatingActionMenu_opening() {

        int fabButtonList[] = {R.id.action_reverse_order, R.id.action_order_price_per_hour, R.id.action_order_time_played, R.id.action_order_price_per_hour};

        for (int fabButton : fabButtonList) {
            onView(withId(fabButton)).check(matches(isDisplayed()));
        }

    }

}
