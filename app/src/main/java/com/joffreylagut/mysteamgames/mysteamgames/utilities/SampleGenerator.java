package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import android.content.Context;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * SampleGenerator.java
 * Purpose: This class contains constants used in the app.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-04-07
 */
public class SampleGenerator {

    // API Key to use the steam Web API.
    public static final String API_KEY = "B1F028BA4B3F02C594462737E055DB44";

    // Steam ID of the user who is using the app
    public static final long DEFAULT_STEAM_ID = 76561198052789807L;
    public static final long SECONDARY_STEAM_ID = 76561198090115246L;

    // Steam User JSON
    public static final String STEAM_USER_JSON_SAMPLE = "{\n" +
            "\t\"response\": {\n" +
            "\t\t\"players\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"steamid\": \"" + DEFAULT_STEAM_ID +"\",\n" +
            "\t\t\t\t\"communityvisibilitystate\": 3,\n" +
            "\t\t\t\t\"profilestate\": 1,\n" +
            "\t\t\t\t\"personaname\": \"Slayde\",\n" +
            "\t\t\t\t\"lastlogoff\": 1491565408,\n" +
            "\t\t\t\t\"profileurl\": \"http://steamcommunity.com/profiles/76561198052789807/\",\n" +
            "\t\t\t\t\"avatar\": \"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/e7/e791518190efca7b1237c7eb46923c435aa1ace4.jpg\",\n" +
            "\t\t\t\t\"avatarmedium\": \"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/e7/e791518190efca7b1237c7eb46923c435aa1ace4_medium.jpg\",\n" +
            "\t\t\t\t\"avatarfull\": \"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/e7/e791518190efca7b1237c7eb46923c435aa1ace4_full.jpg\",\n" +
            "\t\t\t\t\"personastate\": 3,\n" +
            "\t\t\t\t\"realname\": \"Joffrey LGT\",\n" +
            "\t\t\t\t\"primaryclanid\": \"103582791457276526\",\n" +
            "\t\t\t\t\"timecreated\": 1321533180,\n" +
            "\t\t\t\t\"personastateflags\": 0,\n" +
            "\t\t\t\t\"loccountrycode\": \"FR\",\n" +
            "\t\t\t\t\"locstatecode\": \"A8\",\n" +
            "\t\t\t\t\"loccityid\": 15828\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t\t\n" +
            "\t}\n" +
            "}\n";

    public static final String STEAM_USER_JSON_SAMPLE_UPDATED = "{\n" +
            "\t\"response\": {\n" +
            "\t\t\"players\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"steamid\": \"" + DEFAULT_STEAM_ID +"\",\n" +
            "\t\t\t\t\"communityvisibilitystate\": 3,\n" +
            "\t\t\t\t\"profilestate\": 1,\n" +
            "\t\t\t\t\"personaname\": \"SlaydeBTW\",\n" +
            "\t\t\t\t\"lastlogoff\": 1491565408,\n" +
            "\t\t\t\t\"profileurl\": \"http://steamcommunity.com/profiles/76561198052789807/\",\n" +
            "\t\t\t\t\"avatar\": \"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/e7/e791518190efca7b1237c7eb46923c435aa1ace4.jpg\",\n" +
            "\t\t\t\t\"avatarmedium\": \"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/e7/e791518190efca7b1237c7eb46923c435aa1ace4_medium.jpg\",\n" +
            "\t\t\t\t\"avatarfull\": \"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/e7/e791518190efca7b1237c7eb46923c435aa1ace4_full.jpg\",\n" +
            "\t\t\t\t\"personastate\": 3,\n" +
            "\t\t\t\t\"realname\": \"Joffrey LGT\",\n" +
            "\t\t\t\t\"primaryclanid\": \"103582791457276526\",\n" +
            "\t\t\t\t\"timecreated\": 1321533180,\n" +
            "\t\t\t\t\"personastateflags\": 0,\n" +
            "\t\t\t\t\"loccountrycode\": \"FR\",\n" +
            "\t\t\t\t\"locstatecode\": \"A8\",\n" +
            "\t\t\t\t\"loccityid\": 15828\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t\t\n" +
            "\t}\n" +
            "}\n";

    public static final String STEAM_USER_GAMES_JSON_SAMPLE = "{\n" +
            "\t\"response\": {\n" +
            "\t\t\"game_count\": 3,\n" +
            "\t\t\"games\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"appid\": 236110,\n" +
            "\t\t\t\t\"name\": \"Dungeon Defenders II\",\n" +
            "\t\t\t\t\"playtime_2weeks\": 1200,\n" +
            "\t\t\t\t\"playtime_forever\": 60000,\n" +
            "\t\t\t\t\"img_icon_url\": \"0ce07c2568f978a01c8ae5f4d9402c3253963641\",\n" +
            "\t\t\t\t\"img_logo_url\": \"fe628ba79f4b23a5aee079d1c71f7c1ef24065f4\",\n" +
            "\t\t\t\t\"has_community_visible_stats\": true\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"appid\": 413150,\n" +
            "\t\t\t\t\"name\": \"Stardew Valley\",\n" +
            "\t\t\t\t\"playtime_forever\": 1200,\n" +
            "\t\t\t\t\"img_icon_url\": \"35d1377200084a4034238c05b0c8930451e2fb40\",\n" +
            "\t\t\t\t\"img_logo_url\": \"694de1f9cf09aba9d6d118bcff07714343529a0a\",\n" +
            "\t\t\t\t\"has_community_visible_stats\": true\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"appid\": 268500,\n" +
            "\t\t\t\t\"name\": \"XCOM 2\",\n" +
            "\t\t\t\t\"playtime_forever\": 15,\n" +
            "\t\t\t\t\"img_icon_url\": \"f275aeb0b1b947262810569356a199848c643754\",\n" +
            "\t\t\t\t\"img_logo_url\": \"10a6157d6614f63cd8a95d002d022778c207c218\",\n" +
            "\t\t\t\t\"has_community_visible_stats\": true\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t\t\n" +
            "\t}\n" +
            "}";

    public static final String STEAM_USER_GAMES_JSON_SAMPLE_UPDATED = "{\n" +
            "\t\"response\": {\n" +
            "\t\t\"game_count\": 3,\n" +
            "\t\t\"games\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"appid\": 236110,\n" +
            "\t\t\t\t\"name\": \"Dungeon Defenders II9\",\n" +
            "\t\t\t\t\"playtime_2weeks\": 1209,\n" +
            "\t\t\t\t\"playtime_forever\": 60009,\n" +
            "\t\t\t\t\"img_icon_url\": \"0ce07c2568f978a01c8ae5f4d9402c3253963649\",\n" +
            "\t\t\t\t\"img_logo_url\": \"fe628ba79f4b23a5aee079d1c71f7c1ef24065f9\",\n" +
            "\t\t\t\t\"has_community_visible_stats\": true\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"appid\": 413150,\n" +
            "\t\t\t\t\"name\": \"Stardew Valley9\",\n" +
            "\t\t\t\t\"playtime_forever\": 1209,\n" +
            "\t\t\t\t\"img_icon_url\": \"35d1377200084a4034238c05b0c8930451e2fb49\",\n" +
            "\t\t\t\t\"img_logo_url\": \"694de1f9cf09aba9d6d118bcff07714343529a09\",\n" +
            "\t\t\t\t\"has_community_visible_stats\": true\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"appid\": 268500,\n" +
            "\t\t\t\t\"name\": \"XCOM 29\",\n" +
            "\t\t\t\t\"playtime_forever\": 15,\n" +
            "\t\t\t\t\"img_icon_url\": \"f275aeb0b1b947262810569356a199848c643759\",\n" +
            "\t\t\t\t\"img_logo_url\": \"10a6157d6614f63cd8a95d002d022778c207c219\",\n" +
            "\t\t\t\t\"has_community_visible_stats\": true\n" +
            "\t\t\t}\n" +
            "\t\t]\n" +
            "\t\t\n" +
            "\t}\n" +
            "}";

    /**
     * Generate dummy data to display information in the views.
     *
     * @param context Environment variables.
     * @return a list of GameTongue.
     */
    public static List<GameTongueAdapter.GameTongue> generateListGameTongue(Context context) {
        List<GameTongueAdapter.GameTongue> gameTongues = new ArrayList<>();

        gameTongues.add(new GameTongueAdapter.GameTongue(
                1,
                context.getResources().getString(R.string.game_title_example_1),
                context.getResources().getString(R.string.game_goals_progression_hours_1),
                context.getResources().getString(R.string.game_goals_progression_percent_1)
        ));
        gameTongues.add(new GameTongueAdapter.GameTongue(
                1,
                context.getResources().getString(R.string.game_title_example_2),
                context.getResources().getString(R.string.game_goals_progression_hours_2),
                context.getResources().getString(R.string.game_goals_progression_percent_2)
        ));
        gameTongues.add(new GameTongueAdapter.GameTongue(
                1,
                context.getResources().getString(R.string.game_title_example_3),
                context.getResources().getString(R.string.game_goals_progression_hours_3),
                context.getResources().getString(R.string.game_goals_progression_percent_3)
        ));

        return gameTongues;
    }

    /**
     * Generate dummy data to display information in the views.
     *
     * @param context Environment variables.
     * @return a list of GameTongue.
     */
    public static List<GameTongueAdapter.GameTongue> generateListMostProfitableGameTongue(Context context) {
        List<GameTongueAdapter.GameTongue> gameTongues = new ArrayList<>();

        gameTongues.add(new GameTongueAdapter.GameTongue(
                1,
                context.getResources().getString(R.string.game_title_example_1),
                context.getResources().getString(R.string.game_hours_played_1),
                context.getResources().getString(R.string.game_price_per_hour_1)
        ));
        gameTongues.add(new GameTongueAdapter.GameTongue(
                1,
                context.getResources().getString(R.string.game_title_example_2),
                context.getResources().getString(R.string.game_hours_played_2),
                context.getResources().getString(R.string.game_price_per_hour_2)
        ));
        gameTongues.add(new GameTongueAdapter.GameTongue(
                1,
                context.getResources().getString(R.string.game_title_example_3),
                context.getResources().getString(R.string.game_hours_played_2),
                context.getResources().getString(R.string.game_price_per_hour_2)
        ));

        return gameTongues;
    }
}
