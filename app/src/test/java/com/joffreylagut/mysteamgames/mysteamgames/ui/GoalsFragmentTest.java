package com.joffreylagut.mysteamgames.mysteamgames.ui;

import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * GoalsFragmentTest.java
 * Purpose: Handle unit tests for GoalsFragment.java.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-17
 */

public class GoalsFragmentTest {
    @Test
    public void extractAndSortGoals_emptyList() {
        List<OwnedGame> ownedGameList = new ArrayList<>();
        int expectedListResultSize = 0;

        List<OwnedGame> results = GoalsFragment.extractAndSortGoals(ownedGameList, true, 1.0);
        assertTrue(results.size() == expectedListResultSize);

        results = GoalsFragment.extractAndSortGoals(ownedGameList, false, 1.0);
        assertTrue(results.size() == expectedListResultSize);
    }

    @Test
    public void extractAndSortGoals() {
        List<OwnedGame> ownedGameList = new ArrayList<>();
        int expectedListResultSize = 5;

        OwnedGame goalFinished = SampleGenerator.generateOwnedGame(1);
        goalFinished.setGamePrice(5);
        goalFinished.setTimePlayedForever(310);

        OwnedGame goalNotFinishedAndPlayed = SampleGenerator.generateOwnedGame(1);
        goalNotFinishedAndPlayed.setGamePrice(10);
        goalNotFinishedAndPlayed.setTimePlayedForever(25);

        OwnedGame goalNotFinishedAndNotPlayed = SampleGenerator.generateOwnedGame(1);
        goalNotFinishedAndNotPlayed.setGamePrice(10);
        goalNotFinishedAndNotPlayed.setTimePlayedForever(0);

        for (int i = 0; i < expectedListResultSize; i++) {
            ownedGameList.add(goalFinished);
            ownedGameList.add(goalNotFinishedAndNotPlayed);
            ownedGameList.add(goalNotFinishedAndPlayed);
        }

        List<OwnedGame> results = GoalsFragment.extractAndSortGoals(ownedGameList, true, 1.0);
        assertTrue(results.size() == expectedListResultSize);

        results = GoalsFragment.extractAndSortGoals(ownedGameList, false, 1.0);
        assertTrue(results.size() == expectedListResultSize);
    }
}
