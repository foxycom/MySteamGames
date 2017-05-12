package com.joffreylagut.mysteamgames.mysteamgames.data;

import com.joffreylagut.mysteamgames.mysteamgames.models.Goal;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator.generateOwnedGame;
import static junit.framework.Assert.assertTrue;

/**
 * GameTongueAdapterTest.java
 * Purpose: Handle all the unit tests of GameTongueAdapter.java.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-04-06
 */

public class GameTongueAdapterTest {

    @Test
    public void convertOwnedGameListToGameTongueList_goalsInGoalsClass() {

        double PROFITABLE_THRESHOLD = 1.00;

        String expectedUrlGameIcon = "http://media.steampowered.com/steamcommunity/public/images/apps/13210/de312a41b8a0b8fd6e1f0490ab0b44416c53cc61.jpg";

        OwnedGame ownedGame = generateOwnedGame(1);
        ownedGame.setTimePlayedForever(600);
        ownedGame.setGamePrice(11.00);
        ownedGame.setPricePerHour(1.10);
        String expectedProgression1 = "10 / 11h";
        String expectedCaption1 = "90%";

        Goal goal1 = new Goal(PROFITABLE_THRESHOLD, ownedGame);

        ownedGame.setTimePlayedForever(600);
        ownedGame.setGamePrice(15.00);
        ownedGame.setPricePerHour(1.5);
        String expectedProgression2 = "10 / 15h";
        String expectedCaption2 = "66%";

        Goal goal2 = new Goal(PROFITABLE_THRESHOLD, ownedGame);

        ownedGame.setTimePlayedForever(1200);
        ownedGame.setGamePrice(60.00);
        ownedGame.setPricePerHour(1.5);
        String expectedProgression3 = "20 / 60h";
        String expectedCaption3 = "33%";

        Goal goal3 = new Goal(PROFITABLE_THRESHOLD, ownedGame);

        List<OwnedGame> ownedGameListToConvert = new ArrayList<>();
        ownedGameListToConvert.add(goal1);
        ownedGameListToConvert.add(goal2);
        ownedGameListToConvert.add(goal3);

        List<GameTongueAdapter.GameTongue> convertedList = GameTongueAdapter.convertOwnedGameListToGameTongueList(ownedGameListToConvert, "€", PROFITABLE_THRESHOLD);

        GameTongueAdapter.GameTongue gameTongue1 = convertedList.get(0);
        GameTongueAdapter.GameTongue gameTongue2 = convertedList.get(1);
        GameTongueAdapter.GameTongue gameTongue3 = convertedList.get(2);

        testEquality(gameTongue1, goal1, expectedProgression1, expectedCaption1, expectedUrlGameIcon, 0);
        testEquality(gameTongue2, goal2, expectedProgression2, expectedCaption2, expectedUrlGameIcon, 0);
        testEquality(gameTongue3, goal3, expectedProgression3, expectedCaption3, expectedUrlGameIcon, 0);

    }

    private void testEquality(GameTongueAdapter.GameTongue gameTongue, OwnedGame ownedGame, String expectedProgression, String expectedCaption, String expectedUrlGameIcon, int expectedPercentageBackground) {
        assertTrue(gameTongue.gameTitle.equals(ownedGame.getGame().getGameName()));
        assertTrue(gameTongue.gameId == ownedGame.getGame().getGameID());
        assertTrue(gameTongue.gameProgression.equals(expectedProgression));
        assertTrue(gameTongue.gameCaption.equals(expectedCaption));
        assertTrue(gameTongue.urlGameIcon.equals(expectedUrlGameIcon));
        if (ownedGame.getClass() == Goal.class) {
            assertTrue(gameTongue.percentageBackground == ((Goal) ownedGame).getCompletionPercentage());
        } else {
            assertTrue(gameTongue.percentageBackground == expectedPercentageBackground);
        }

    }

    @Test
    public void convertOwnedGameListToGameTongueList_goalsOwnedGamesClass() {

        double PROFITABLE_THRESHOLD = 1.00;
        String expectedUrlGameIcon = "http://media.steampowered.com/steamcommunity/public/images/apps/13210/de312a41b8a0b8fd6e1f0490ab0b44416c53cc61.jpg";

        List<OwnedGame> ownedGameListToConvert = new ArrayList<>();

        OwnedGame ownedGame1 = generateOwnedGame(1);
        ownedGame1.setTimePlayedForever(600);
        ownedGame1.setGamePrice(11.00);
        ownedGame1.setPricePerHour(1.10);
        String expectedProgression1 = "10 / 11h";
        String expectedCaption1 = "90%";
        int expectedPercentageBackground1 = 90;

        ownedGameListToConvert.add(ownedGame1);

        OwnedGame ownedGame2 = generateOwnedGame(1);
        ownedGame2.setTimePlayedForever(600);
        ownedGame2.setGamePrice(15.00);
        ownedGame2.setPricePerHour(1.5);
        String expectedProgression2 = "10 / 15h";
        String expectedCaption2 = "66%";
        int expectedPercentageBackground2 = 66;

        ownedGameListToConvert.add(ownedGame2);

        OwnedGame ownedGame3 = generateOwnedGame(1);
        ownedGame3.setTimePlayedForever(1200);
        ownedGame3.setGamePrice(60.00);
        ownedGame3.setPricePerHour(1.5);
        String expectedProgression3 = "20 / 60h";
        String expectedCaption3 = "33%";
        int expectedPercentageBackground3 = 33;

        ownedGameListToConvert.add(ownedGame3);

        List<GameTongueAdapter.GameTongue> convertedList = GameTongueAdapter.convertOwnedGameListToGameTongueList(ownedGameListToConvert, "€", PROFITABLE_THRESHOLD);

        GameTongueAdapter.GameTongue gameTongue1 = convertedList.get(0);
        GameTongueAdapter.GameTongue gameTongue2 = convertedList.get(1);
        GameTongueAdapter.GameTongue gameTongue3 = convertedList.get(2);

        testEquality(gameTongue1, ownedGame1, expectedProgression1, expectedCaption1, expectedUrlGameIcon, expectedPercentageBackground1);
        testEquality(gameTongue2, ownedGame2, expectedProgression2, expectedCaption2, expectedUrlGameIcon, expectedPercentageBackground2);
        testEquality(gameTongue3, ownedGame3, expectedProgression3, expectedCaption3, expectedUrlGameIcon, expectedPercentageBackground3);
    }

    @Test
    public void convertOwnedGameListToGameTongueList_completedOwnedGames() {

        double PROFITABLE_THRESHOLD = 1.00;
        int expectedPercentageBackground = 100;
        String expectedUrlGameIcon = "http://media.steampowered.com/steamcommunity/public/images/apps/13210/de312a41b8a0b8fd6e1f0490ab0b44416c53cc61.jpg";

        List<OwnedGame> ownedGameListToConvert = new ArrayList<>();

        OwnedGame ownedGame1 = generateOwnedGame(1);
        ownedGame1.setTimePlayedForever(600);
        ownedGame1.setGamePrice(8.00);
        ownedGame1.setPricePerHour(0.8);
        String expectedProgression1 = "10h";
        String expectedCaption1 = "0.8€/h";

        ownedGameListToConvert.add(ownedGame1);

        OwnedGame ownedGame2 = generateOwnedGame(1);
        ownedGame2.setTimePlayedForever(600);
        ownedGame2.setGamePrice(5);
        ownedGame2.setPricePerHour(0.5);
        String expectedProgression2 = "10h";
        String expectedCaption2 = "0.5€/h";

        ownedGameListToConvert.add(ownedGame2);

        OwnedGame ownedGame3 = generateOwnedGame(1);
        ownedGame3.setTimePlayedForever(1200);
        ownedGame3.setGamePrice(15.00);
        ownedGame3.setPricePerHour(0.75);
        String expectedProgression3 = "20h";
        String expectedCaption3 = "0.75€/h";

        ownedGameListToConvert.add(ownedGame3);

        List<GameTongueAdapter.GameTongue> convertedList = GameTongueAdapter.convertOwnedGameListToGameTongueList(ownedGameListToConvert, "€", PROFITABLE_THRESHOLD);

        GameTongueAdapter.GameTongue gameTongue1 = convertedList.get(0);
        GameTongueAdapter.GameTongue gameTongue2 = convertedList.get(1);
        GameTongueAdapter.GameTongue gameTongue3 = convertedList.get(2);

        testEquality(gameTongue1, ownedGame1, expectedProgression1, expectedCaption1, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue2, ownedGame2, expectedProgression2, expectedCaption2, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue3, ownedGame3, expectedProgression3, expectedCaption3, expectedUrlGameIcon, expectedPercentageBackground);

    }

    @Test
    public void convertOwnedGameListToGameTongueList_OwnedGameWithoutPrice() {

        double PROFITABLE_THRESHOLD = 1.00;
        int expectedPercentageBackground = 100;
        String expectedCaption = "";
        String expectedUrlGameIcon = "http://media.steampowered.com/steamcommunity/public/images/apps/13210/de312a41b8a0b8fd6e1f0490ab0b44416c53cc61.jpg";

        List<OwnedGame> ownedGameListToConvert = new ArrayList<>();

        OwnedGame ownedGame1 = generateOwnedGame(1);
        ownedGame1.setTimePlayedForever(900);
        ownedGame1.setGamePrice(-1.00);
        ownedGame1.setPricePerHour(0.00);
        String expectedProgression1 = "15h";

        ownedGameListToConvert.add(ownedGame1);

        OwnedGame ownedGame2 = generateOwnedGame(1);
        ownedGame2.setTimePlayedForever(600);
        ownedGame2.setGamePrice(-1.00);
        ownedGame2.setPricePerHour(0.00);
        String expectedProgression2 = "10h";

        ownedGameListToConvert.add(ownedGame2);

        OwnedGame ownedGame3 = generateOwnedGame(1);
        ownedGame3.setTimePlayedForever(1200);
        ownedGame3.setGamePrice(-1.00);
        ownedGame3.setPricePerHour(0.00);
        String expectedProgression3 = "20h";

        ownedGameListToConvert.add(ownedGame3);

        List<GameTongueAdapter.GameTongue> convertedList = GameTongueAdapter.convertOwnedGameListToGameTongueList(ownedGameListToConvert, "€", PROFITABLE_THRESHOLD);

        GameTongueAdapter.GameTongue gameTongue1 = convertedList.get(0);
        GameTongueAdapter.GameTongue gameTongue2 = convertedList.get(1);
        GameTongueAdapter.GameTongue gameTongue3 = convertedList.get(2);

        testEquality(gameTongue1, ownedGame1, expectedProgression1, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue2, ownedGame2, expectedProgression2, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue3, ownedGame3, expectedProgression3, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);

    }

    @Test
    public void convertOwnedGameListToGameTongueList_OwnedGameNeverPlayedWithoutPrice() {

        double PROFITABLE_THRESHOLD = 1.00;
        int expectedPercentageBackground = 100;
        String expectedCaption = "";
        String expectedProgression = "0h";
        String expectedUrlGameIcon = "http://media.steampowered.com/steamcommunity/public/images/apps/13210/de312a41b8a0b8fd6e1f0490ab0b44416c53cc61.jpg";

        List<OwnedGame> ownedGameListToConvert = new ArrayList<>();

        OwnedGame ownedGame1 = generateOwnedGame(1);
        ownedGame1.setTimePlayedForever(0);
        ownedGame1.setGamePrice(-1.00);
        ownedGame1.setPricePerHour(0.00);

        ownedGameListToConvert.add(ownedGame1);

        OwnedGame ownedGame2 = generateOwnedGame(1);
        ownedGame2.setTimePlayedForever(0);
        ownedGame2.setGamePrice(-1.00);
        ownedGame2.setPricePerHour(0.00);

        ownedGameListToConvert.add(ownedGame2);

        OwnedGame ownedGame3 = generateOwnedGame(1);
        ownedGame3.setTimePlayedForever(0);
        ownedGame3.setGamePrice(-1.00);
        ownedGame3.setPricePerHour(0.00);

        ownedGameListToConvert.add(ownedGame3);

        List<GameTongueAdapter.GameTongue> convertedList = GameTongueAdapter.convertOwnedGameListToGameTongueList(ownedGameListToConvert, "€", PROFITABLE_THRESHOLD);

        GameTongueAdapter.GameTongue gameTongue1 = convertedList.get(0);
        GameTongueAdapter.GameTongue gameTongue2 = convertedList.get(1);
        GameTongueAdapter.GameTongue gameTongue3 = convertedList.get(2);

        testEquality(gameTongue1, ownedGame1, expectedProgression, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue2, ownedGame2, expectedProgression, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue3, ownedGame3, expectedProgression, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);

    }

    @Test
    public void convertOwnedGameListToGameTongueList_OwnedGameNeverPlayedWithPrice() {

        double PROFITABLE_THRESHOLD = 1.00;
        int expectedPercentageBackground = 0;
        String expectedCaption = "0%";
        String expectedUrlGameIcon = "http://media.steampowered.com/steamcommunity/public/images/apps/13210/de312a41b8a0b8fd6e1f0490ab0b44416c53cc61.jpg";

        List<OwnedGame> ownedGameListToConvert = new ArrayList<>();

        OwnedGame ownedGame1 = generateOwnedGame(1);
        ownedGame1.setTimePlayedForever(0);
        ownedGame1.setGamePrice(10.00);
        ownedGame1.setPricePerHour(Double.POSITIVE_INFINITY);
        String expectedProgression1 = "0 / 10h";

        ownedGameListToConvert.add(ownedGame1);

        OwnedGame ownedGame2 = generateOwnedGame(1);
        ownedGame2.setTimePlayedForever(0);
        ownedGame2.setGamePrice(25.00);
        ownedGame2.setPricePerHour(Double.POSITIVE_INFINITY);
        String expectedProgression2 = "0 / 25h";

        ownedGameListToConvert.add(ownedGame2);

        OwnedGame ownedGame3 = generateOwnedGame(1);
        ownedGame3.setTimePlayedForever(0);
        ownedGame3.setGamePrice(42.00);
        ownedGame3.setPricePerHour(Double.POSITIVE_INFINITY);
        String expectedProgression3 = "0 / 42h";

        ownedGameListToConvert.add(ownedGame3);

        List<GameTongueAdapter.GameTongue> convertedList = GameTongueAdapter.convertOwnedGameListToGameTongueList(ownedGameListToConvert, "€", PROFITABLE_THRESHOLD);

        GameTongueAdapter.GameTongue gameTongue1 = convertedList.get(0);
        GameTongueAdapter.GameTongue gameTongue2 = convertedList.get(1);
        GameTongueAdapter.GameTongue gameTongue3 = convertedList.get(2);


        testEquality(gameTongue1, ownedGame1, expectedProgression1, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue2, ownedGame2, expectedProgression2, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);
        testEquality(gameTongue3, ownedGame3, expectedProgression3, expectedCaption, expectedUrlGameIcon, expectedPercentageBackground);

    }

}
