package com.joffreylagut.mysteamgames.mysteamgames.customclass;

import java.net.URL;
import java.util.List;

/**
 * Created by Joffrey on 10/02/2017.
 */

public class User {

    private String steamID;
    private String accountName;
    private URL accountPicture;
    private List<GameListItem> gameList;
    private int nbMinutesPlayed;

    public User(String steamID, String accountName, URL accountPicture, List<GameListItem> gameList, int nbMinutesPlayed) {
        this.accountName = accountName;
        this.accountPicture = accountPicture;
        this.gameList = gameList;
        this.nbMinutesPlayed = nbMinutesPlayed;
    }

    public User() {
    }

    public String getSteamID() {
        return steamID;
    }

    public void setSteamID(String steamID) {
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

    public List<GameListItem> getGameList() {
        return gameList;
    }

    public void setGameList(List<GameListItem> gameList) {
        this.gameList = gameList;
    }

    public int getNbMinutesPlayed() {
        return nbMinutesPlayed;
    }

    public void setNbMinutesPlayed(int nbMinutesPlayed) { this.nbMinutesPlayed = nbMinutesPlayed; }
}
