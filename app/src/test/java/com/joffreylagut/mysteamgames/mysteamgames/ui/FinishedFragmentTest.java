package com.joffreylagut.mysteamgames.mysteamgames.ui;

import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * FinishedFragmentTest.java
 * Purpose: Handle unit tests for FinishedFragment.java.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-17
 */

public class FinishedFragmentTest {

    @Test
    public void extractAndSortGoalsFinished_emptyList() {
        List<OwnedGame> ownedGameList = new ArrayList<>();
        int expectedListResultSize = 0;

        List<OwnedGame> results = FinishedFragment.extractAndSortGoalsFinished(ownedGameList, 1.0);
        assertTrue(results.size() == expectedListResultSize);
    }

    @Test
    public void extractAndSortGoalsFinished() {
        List<OwnedGame> ownedGameList = new ArrayList<>();
        int expectedListResultSize = 5;

        OwnedGame goalFinished = SampleGenerator.generateOwnedGame(1);
        goalFinished.setGamePrice(5);
        goalFinished.setTimePlayedForever(310);

        OwnedGame goalNotFinished = SampleGenerator.generateOwnedGame(1);
        goalNotFinished.setGamePrice(10);
        goalNotFinished.setTimePlayedForever(25);

        for (int i = 0; i < expectedListResultSize; i++) {
            ownedGameList.add(goalFinished);
            ownedGameList.add(goalNotFinished);
        }

        List<OwnedGame> results = FinishedFragment.extractAndSortGoalsFinished(ownedGameList, 1.0);
        assertTrue(results.size() == expectedListResultSize);
    }
}
