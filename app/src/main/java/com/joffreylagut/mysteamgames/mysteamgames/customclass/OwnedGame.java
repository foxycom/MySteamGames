package com.joffreylagut.mysteamgames.mysteamgames.customclass;

/**
 * Created by Joffrey on 15/02/2017.
 */

public class OwnedGame {
    private Game game;
    private int timePlayedForever;
    private int timePlayed2Weeks;
    private double gamePrice;

    public OwnedGame() {
    }

    public OwnedGame(Game game, int timePlayedForever, int timePlayed2Weeks, double gamePrice) {
        this.game = game;
        this.timePlayedForever = timePlayedForever;
        this.timePlayed2Weeks = timePlayed2Weeks;
        this.gamePrice = gamePrice;
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

    public long getTimePlayed2Weeks() {
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
}
