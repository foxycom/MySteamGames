package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.provider.BaseColumns;

/**
 * Created by Joffrey on 14/02/2017.
 */

public final class UserContract {
    /**
     * Empty constructor to prevent someone from accidentally instantiating the contract class
     */
    public UserContract(){};

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




}
