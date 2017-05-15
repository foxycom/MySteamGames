package com.joffreylagut.mysteamgames.mysteamgames.models;

import java.util.Comparator;

/**
 * OwnedGame.java
 * Purpose: Blueprint for a OwnedGame object.
 *
 * @author Joffrey LAGUT
 * @version 1.7 2017-05-15
 */

public class OwnedGame {

    private int userId; // Mandatory
    private Game game; // Mandatory
    private int timePlayedForever = 0; // Optional
    private int timePlayed2Weeks = 0; // Optional
    private double gamePrice = -1.00; // Optional
    private Double pricePerHour = -1.00; // Optional
    private boolean favorite = false; // Optional
    private GameBundle gameBundle = null; // Optional

    public OwnedGame(int userId, Game game) {
        this.userId = userId;
        this.game = game;
    }

    public OwnedGame(int userId, Game game, int timePlayedForever, int timePlayed2Weeks, double gamePrice, boolean favorite, GameBundle gameBundle) {
        this.userId = userId;
        this.game = game;
        this.timePlayedForever = timePlayedForever;
        this.timePlayed2Weeks = timePlayed2Weeks;
        this.gamePrice = gamePrice;
        this.favorite = favorite;
        this.gameBundle = gameBundle;
        calculatePricePerHour();
    }

    public OwnedGame(int userId, Game game, int timePlayedForever, int timePlayed2Weeks, double gamePrice, boolean favorite, GameBundle gameBundle, Double pricePerHour) {
        this.userId = userId;
        this.game = game;
        this.timePlayedForever = timePlayedForever;
        this.timePlayed2Weeks = timePlayed2Weeks;
        this.gamePrice = gamePrice;
        this.favorite = favorite;
        this.gameBundle = gameBundle;
        this.pricePerHour = pricePerHour;
        calculatePricePerHour();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getTimePlayedForever() {
        return timePlayedForever;
    }

    public void setTimePlayedForever(int timePlayedForever) {
        this.timePlayedForever = timePlayedForever;
        calculatePricePerHour();
    }

    public int getTimePlayed2Weeks() {
        return timePlayed2Weeks;
    }

    public void setTimePlayed2Weeks(int timePlayed2Weeks) {
        this.timePlayed2Weeks = timePlayed2Weeks;
    }

    public double getGamePrice() {
        return gamePrice;
    }

    public void setGamePrice(double gamePrice) {

        this.gamePrice = gamePrice;
        calculatePricePerHour();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public GameBundle getGameBundle() {
        return gameBundle;
    }

    public void setGameBundle(GameBundle gameBundle) {
        this.gameBundle = gameBundle;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    /**
     * Calculate the price per hour of the game.
     */
    private void calculatePricePerHour() {
        if (timePlayedForever != 0) {
            if (this.gamePrice > 0) {
                Double nbHoursPlayed = (double) this.getTimePlayedForever() / 60;
                pricePerHour = this.getGamePrice() / nbHoursPlayed;
                return;
            }
            if (gamePrice == 0) {
                pricePerHour = 0.00;
            } else {
                pricePerHour = -1.00;
            }
        }

    }

    /**
     * Class created to do the comparison between the price per hour of 2 games.
     */
    public static class OwnedGamePricePerHourComparator implements Comparator<OwnedGame> {

        @Override
        public int compare(OwnedGame o1, OwnedGame o2) {
            Double pricePerHour1 = o1.getPricePerHour();
            Double pricePerHour2 = o2.getPricePerHour();
            return pricePerHour1.compareTo(pricePerHour2);
        }
    }
}
