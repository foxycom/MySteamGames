package com.joffreylagut.mysteamgames.mysteamgames.customclass;

import java.net.URL;

/**
 * Created by Joffrey on 08/02/2017.
 */

public class GameListItem implements Comparable {
    // TODO Change the name of all the attributes and generate new getters/setters. Be carefull with the RecyclerView.Adapter
    private int gameID;
    private int gameSteamID;
    private URL gameImage;
    private String gameName;
    private int gameTimePlayed;
    private double gamePrice;
    private int userID;

    public GameListItem() {
    }

    public GameListItem(int gameID, int gameSteamID, URL gameImage, String gameName, int gameTimePlayed, double gamePrice, int userID) {
        this.gameID = gameID;
        this.gameSteamID = gameSteamID;
        this.gameImage = gameImage;
        this.gameName = gameName;
        this.gameTimePlayed = gameTimePlayed;
        this.gamePrice = gamePrice;
        this.userID = userID;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public double getGamePrice() {
        return gamePrice;
    }

    public void setGamePrice(double gamePrice) {
        this.gamePrice = gamePrice;
    }

    public int getGameSteamID() {
        return gameSteamID;
    }

    public void setGameSteamID(int gameSteamID) {
        this.gameSteamID = gameSteamID;
    }

    public URL getGameImage() {
        return gameImage;
    }

    public void setGameImage(URL gameImage) {
        this.gameImage = gameImage;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getGameTimePlayed() {
        return gameTimePlayed;
    }

    public void setGameTimePlayed(int gameTimePlayed) {
        this.gameTimePlayed = gameTimePlayed;
    }


    /**
     * By default, we are sorting the games by time played.
     */
    @Override
    public int compareTo(Object compareTu) {
        int compareGameTimePlayed = ((GameListItem) compareTu).getGameTimePlayed();
        return compareGameTimePlayed - this.getGameTimePlayed();
    }
}
