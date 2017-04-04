package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.provider.BaseColumns;

/**
 * UserContract.java
 * Purpose: This class contains the name of all the tables and fields in the database.
 *
 * @author Joffrey LAGUT
 * @version 1.5 2017-04-10
 */

final class UserContract {
    /**
     * Empty constructor to prevent someone from accidentally instantiating the contract class
     */
    public UserContract() {
    }

    /**
     * Class that specify the layout of User table.
     */
    static abstract class UserEntry implements BaseColumns{
        // Table name
        static final String TABLE_NAME = "USER";
        // Columns
        static final String COLUMN_STEAM_ID = "steamID";
        static final String COLUMN_ACCOUNT_NAME = "accountName";
        static final String COLUMN_ACCOUNT_PICTURE = "accountPicture";

    }

    /**
     * Class that specify the layout of Game table.
     */
    static abstract class GameEntry implements BaseColumns{
        // Table name
        static final String TABLE_NAME = "GAME";
        // Columns
        static final String COLUMN_STEAM_ID = "steamID";
        static final String COLUMN_GAME_NAME = "gameName";
        static final String COLUMN_GAME_LOGO = "gameLogo";
        static final String COLUMN_GAME_ICON = "gameIcon";
        static final String COLUMN_MARKETPLACE = "marketplace";
    }

    /**
     * Class that specify the layout of Owned table.
     */
    static abstract class OwnedGamesEntry{
        // Table name
        static final String TABLE_NAME = "OWNEDGAMES";
        // Columns
        static final String COLUMN_USER_ID = "userID";
        static final String COLUMN_GAME_ID = "gameID";
        static final String COLUMN_TIME_PLAYED_FOREVER = "timePlayedForever";
        static final String COLUMN_TIME_PLAYED_2_WEEKS = "timePlayed2Weeks";
        static final String COLUMN_GAME_PRICE = "gamePrice";
        static final String COLUMN_BUNDLE_ID = "bundleID";
        static final String COLUMN_FAVORITE = "favorite";
    }

    /**
     * Class that specify the layout of GameBundle table.
     */
    static abstract class BundleEntry implements BaseColumns {
        // Table name
        static final String TABLE_NAME = "BUNDLE";
        // Columns
        static final String COLUMN_BUNDLE_NAME = "bundleName";
        static final String COLUMN_BUNDLE_PRICE = "bundlePrice";
    }
}
