package com.joffreylagut.mysteamgames.mysteamgames.customclass;

import java.net.URL;

/**
 * Created by Joffrey on 08/02/2017.
 */

public class GameListItem {
    // TODO Change the name of all the attributes and generate new getters/setters. Be carefull with the RecyclerView.Adapter
    private int GameSteamID;
    private URL mGameImage;
    private String mGameName;
    private int mGameTimePlayed;
    public GameListItem() {
    }
    public GameListItem(URL gameImage, String gameName, int gameTimePlayed) {
        this.mGameImage = gameImage;
        this.mGameName = gameName;
        this.mGameTimePlayed = gameTimePlayed;
    }

    public int getGameSteamID() {
        return GameSteamID;
    }

    public void setGameSteamID(int gameSteamID) {
        GameSteamID = gameSteamID;
    }

    public URL getGameImage() {
        return mGameImage;
    }

    public void setGameImage(URL gameImage) {
        this.mGameImage = gameImage;
    }

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String gameName) {
        this.mGameName = gameName;
    }

    public int getGameTimePlayed() {
        return mGameTimePlayed;
    }

    public void setGameTimePlayed(int gameTimePlayed) {
        this.mGameTimePlayed = gameTimePlayed;
    }
}
