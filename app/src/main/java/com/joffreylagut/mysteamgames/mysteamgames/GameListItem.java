package com.joffreylagut.mysteamgames.mysteamgames;

/**
 * Created by Joffrey on 08/02/2017.
 */

public class GameListItem {
    private int mGameImage;
    private String mGameName;
    private String mGameTimePlayed;

    public GameListItem() {
    }

    public GameListItem(int gameImage, String gameName, String gameTimePlayed) {
        this.mGameImage = gameImage;
        this.mGameName = gameName;
        this.mGameTimePlayed = gameTimePlayed;
    }

    public int getGameImage() {
        return mGameImage;
    }

    public void setGameImage(int gameImage) {
        this.mGameImage = gameImage;
    }

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String gameName) {
        this.mGameName = gameName;
    }

    public String getGameTimePlayed() {
        return mGameTimePlayed;
    }

    public void setGameTimePlayed(String gameTimePlayed) {
        this.mGameTimePlayed = gameTimePlayed;
    }
}
