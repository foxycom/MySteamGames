package com.joffreylagut.mysteamgames.mysteamgames.objects;

import java.net.URL;

/**
 * Created by Joffrey on 15/02/2017.
 */

public class Game {

    private int gameID;
    private long steamID;
    private String gameName;
    private URL gameLogo;
    private URL gameIcon;
    private String marketplace;

    public Game() {
    }

    public Game(int gameID, long steamID, String gameName, URL gameLogo, URL gameIcon, String marketplace) {
        this.gameID = gameID;
        this.steamID = steamID;
        this.gameName = gameName;
        this.gameLogo = gameLogo;
        this.gameIcon = gameIcon;
        this.marketplace = marketplace;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public long getSteamID() {
        return steamID;
    }

    public void setSteamID(long steamID) {
        this.steamID = steamID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public URL getGameLogo() {
        return gameLogo;
    }

    public void setGameLogo(URL gameLogo) {
        this.gameLogo = gameLogo;
    }

    public URL getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(URL gameIcon) {
        this.gameIcon = gameIcon;
    }

    public String getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(String marketplace) {
        this.marketplace = marketplace;
    }
}
