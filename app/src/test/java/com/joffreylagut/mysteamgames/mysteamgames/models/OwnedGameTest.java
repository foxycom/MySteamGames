package com.joffreylagut.mysteamgames.mysteamgames.models;

import com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * OwnedGameTest.java
 * Purpose: Unit tests for OwnedGame.java.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-15
 */

public class OwnedGameTest {

    @Test
    public void calculatePricePerHour_noTimePlayed() {
        Double expectedPricePerHour = -1.00;
        Game game = SampleGenerator.generateGameWithoutId();
        OwnedGame ownedGameToTest = new OwnedGame(1, game);
        assertTrue(ownedGameToTest.getPricePerHour().equals(expectedPricePerHour));
    }

    @Test
    public void calculatePricePerHour_noGamePrice() {
        Double expectedPricePerHour = -1.00;
        Game game = SampleGenerator.generateGameWithoutId();
        OwnedGame ownedGameToTest = new OwnedGame(1, game);
        ownedGameToTest.setTimePlayedForever(120);
        assertTrue(ownedGameToTest.getPricePerHour().equals(expectedPricePerHour));
    }

    @Test
    public void calculatePricePerHour_gamePriceEqual0() {
        Double expectedPricePerHour = 0.00;
        Game game = SampleGenerator.generateGameWithoutId();
        OwnedGame ownedGameToTest = new OwnedGame(1, game);
        ownedGameToTest.setTimePlayedForever(120);
        ownedGameToTest.setGamePrice(0.00);
        assertTrue(ownedGameToTest.getPricePerHour().equals(expectedPricePerHour));
    }

    @Test
    public void calculatePricePerHour_gamePriceMoreThan0() {
        Double expectedPricePerHour = 12.50;
        Game game = SampleGenerator.generateGameWithoutId();
        OwnedGame ownedGameToTest = new OwnedGame(1, game);
        ownedGameToTest.setTimePlayedForever(120);
        ownedGameToTest.setGamePrice(25.00);
        assertTrue(ownedGameToTest.getPricePerHour().equals(expectedPricePerHour));
    }
}
