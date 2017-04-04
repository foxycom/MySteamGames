package com.joffreylagut.mysteamgames.mysteamgames.models;

import java.net.URL;

/**
 * Game.java
 * Purpose: Blueprint for a Game object.
 *
 * @author Joffrey LAGUT
 * @version 1.5 2017-04-10
 */

public class Game {

    private int gameID = 0; // Optional
    private long steamID = 0; // Optional
    private String gameName; // Mandatory
    private URL gameLogo = null; // Optional
    private URL gameIcon = null; // Optional
    private String marketplace = ""; // Optional

    public Game() {
    }

    public Game(String name){
        this.gameName = name;
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
