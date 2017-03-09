package com.joffreylagut.mysteamgames.mysteamgames.customclass;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joffrey on 10/02/2017.
 */

public class User {

    private int userID;
    private long steamID;
    private String accountName;
    private URL accountPicture;
    private List<OwnedGame> ownedGames;
    private int nbMinutesPlayed;

    public User(int userID, long steamID, String accountName, URL accountPicture,
                List<OwnedGame> ownedGames, int nbMinutesPlayed) {
        this.userID = userID;
        this.steamID = steamID;
        this.accountName = accountName;
        this.accountPicture = accountPicture;
        this.ownedGames = ownedGames;
        this.nbMinutesPlayed = nbMinutesPlayed;
    }

    public User() {
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

    public void setNbMinutesPlayed(int nbMinutesPlayed) { this.nbMinutesPlayed = nbMinutesPlayed; }

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

    // TODO change this function when the favorites will be implemented

    /**
     * This function is returning the user favorite games.
     *
     * @return a list of favorite games
     */
    public List<OwnedGame> getFavoriteGames() {
        List<OwnedGame> recentlyPlayedGames = new ArrayList<>();
        for (OwnedGame currentGame : this.ownedGames) {
            if (currentGame.getTimePlayed2Weeks() != 0) {
                recentlyPlayedGames.add(currentGame);
            }
        }
        return recentlyPlayedGames;
    }
}
