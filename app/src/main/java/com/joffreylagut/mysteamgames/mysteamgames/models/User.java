package com.joffreylagut.mysteamgames.mysteamgames.models;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User.java
 * Purpose: Blueprint for a User object.
 *
 * @author Joffrey LAGUT
 * @version 1.5 2017-04-10
 */

public class User {

    private int userID;
    private long steamID;
    private String accountName;
    private URL accountPicture;
    private List<OwnedGame> ownedGames;
    private int nbMinutesPlayed;
    private double totalMoneySpent;

    public double getAveragePricePerHour() {
        return averagePricePerHour;
    }

    private double averagePricePerHour;

    public User() {
        ownedGames = new ArrayList<>();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public long getSteamID() {
        return steamID;
    }

    public void setSteamID(long steamID) {
        this.steamID = steamID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public URL getAccountPicture() {
        return accountPicture;
    }

    public void setAccountPicture(URL accountPicture) {
        this.accountPicture = accountPicture;
    }

    public List<OwnedGame> getOwnedGames() {
        return ownedGames;
    }

    public void setOwnedGames(List<OwnedGame> ownedGames) {
        this.ownedGames = ownedGames;
    }

    public int getNbMinutesPlayed() {
        return nbMinutesPlayed;
    }

    public void setNbMinutesPlayed(int nbMinutesPlayed) {
        this.nbMinutesPlayed = nbMinutesPlayed;
        calculatePricePerHour();
    }

    public double getTotalMoneySpent() {
        return totalMoneySpent;
    }

    public void setTotalMoneySpent(double totalMoneySpent) {
        this.totalMoneySpent = totalMoneySpent;
        calculatePricePerHour();
    }

    private void calculatePricePerHour() {
        this.averagePricePerHour = this.totalMoneySpent / (this.nbMinutesPlayed / 60);
    }

    /**
     * This function is returning the user recently played games.
     *
     * @return a list of recently played games
     */
    public List<OwnedGame> getRecentlyPlayedGames() {
        List<OwnedGame> recentlyPlayedGames = new ArrayList<>();
        for (OwnedGame currentGame : this.ownedGames) {
            if (currentGame.getTimePlayed2Weeks() != 0) {
                recentlyPlayedGames.add(currentGame);
            }
        }
        return recentlyPlayedGames;
    }

    /**
     * This function is returning the user favorite games.
     * @return a list of favorite games
     */
    public List<OwnedGame> getFavoriteGames() {
        List<OwnedGame> games = new ArrayList<>();
        for (OwnedGame currentGame : this.ownedGames) {
            if (currentGame.isFavorite()) {
                games.add(currentGame);
            }
        }
        return games;
    }
}
