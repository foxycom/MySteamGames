package com.joffreylagut.mysteamgames.mysteamgames.models;

/**
 * User.java
 * Purpose: Blueprint for a OwnedGame object.
 *
 * @author Joffrey LAGUT
 * @version 1.6 2017-05-12
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
}
