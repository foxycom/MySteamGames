package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.provider.BaseColumns;

/**
 * Created by Joffrey on 14/02/2017.
 */

public final class UserContract {
    /**
     * Empty constructor to prevent someone from accidentally instantiating the contract class
     */
    public UserContract() {
    }

    /**
     * Class that specify the layout of User table.
     */
    public static abstract class UserEntry implements BaseColumns{
        // Table name
        public static final String TABLE_NAME = "USER";
        // Columns
        public static final String COLUMN_STEAM_ID = "steamID";
        public static final String COLUMN_ACCOUNT_NAME = "accountName";
        public static final String COLUMN_ACCOUNT_PICTURE = "accountPicture";

    }

    /**
     * Class that specify the layout of Game table.
     */
    public static abstract class GameEntry implements BaseColumns{
        // Table name
        public static final String TABLE_NAME = "GAME";
        // Columns
        public static final String COLUMN_STEAM_ID = "steamID";
        public static final String COLUMN_GAME_NAME = "gameName";
        public static final String COLUMN_GAME_LOGO = "gameLogo";
        public static final String COLUMN_GAME_ICON = "gameIcon";
        public static final String COLUMN_MARKETPLACE = "marketplace";
    }

    /**
     * Class that specify the layout of Owned table.
     */
    public static abstract class OwnedGamesEntry{
        // Table name
        public static final String TABLE_NAME = "OWNEDGAMES";
        // Columns
        public static final String COLUMN_USER_ID = "userID";
        public static final String COLUMN_GAME_ID = "gameID";
        public static final String COLUMN_TIME_PLAYED_FOREVER = "timePlayedForever";
        public static final String COLUMN_TIME_PLAYED_2_WEEKS = "timePlayed2Weeks";
        public static final String COLUMN_GAME_PRICE = "gamePrice";
        public static final String COLUMN_BUNDLE_ID = "bundleID";
        public static final String COLUMN_FAVORITE = "favorite";
    }

    /**
     * Class that specify the layout of GameBundle table.
     */
    public static abstract class BundleEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "BUNDLE";
        // Columns
        public static final String COLUMN_BUNDLE_NAME = "bundleName";
        public static final String COLUMN_BUNDLE_PRICE = "bundlePrice";
    }
}
