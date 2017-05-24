package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;

import java.util.ArrayList;
import java.util.List;

/**
 * GameTongueHelper.java
 * Purpose: Contains helpers to create the GameTongue lists from OwnedGame lists.
 *
 * @author Joffrey LAGUT
 * @version 1.2 2017-05-24
 */

public class GameTongueHelper {

    /**
     * Convert the list of OwnedGames in parameter into a a list of GameTongue objects respecting the format of
     * Most profitable card items.
     *
     * @param mostProfitableOwnedGames list of the most played OwnedGames
     * @return the list converted into a list of GameTongue
     */
    public static List<GameTongueAdapter.GameTongue> createMostProfitableGameTongues(List<OwnedGame> mostProfitableOwnedGames, String currency) {

        int rank = 0;
        List<GameTongueAdapter.GameTongue> gameTongues = new ArrayList<>();

        for (OwnedGame ownedGame : mostProfitableOwnedGames) {
            rank++;
            GameTongueAdapter.GameTongue currentGameTongue = new GameTongueAdapter.GameTongue(
                    ownedGame.getGame().getGameID(),
                    ownedGame.getGame().getGameName(),
                    UnitsConverterHelper.displayMinutesInHours(ownedGame.getTimePlayedForever()),
                    UnitsConverterHelper.createPricePerHour(ownedGame.getPricePerHour(), currency),
                    100,
                    ownedGame.getGame().getGameIcon().toString()
            );
            currentGameTongue.setGameRank(rank);
            gameTongues.add(currentGameTongue);
        }
        return gameTongues;
    }

    /**
     * Convert the list of OwnedGames in parameter into a a list of GameTongue objects respecting the format of
     * Most played card items.
     *
     * @param mostPlayedOwnedGames list of the most played OwnedGames
     * @return the list converted into a list of GameTongue
     */
    public static List<GameTongueAdapter.GameTongue> createMostPlayedGameTongues(List<OwnedGame> mostPlayedOwnedGames, String currency) {

        int rank = 0;
        List<GameTongueAdapter.GameTongue> gameTongues = new ArrayList<>();
        String pricePerHour;

        for (OwnedGame ownedGame : mostPlayedOwnedGames) {
            rank++;
            if (ownedGame.getPricePerHour() != -1.0) {
                pricePerHour = UnitsConverterHelper.createPricePerHour(ownedGame.getPricePerHour(), currency);
            } else {
                pricePerHour = "";
            }
            GameTongueAdapter.GameTongue currentGameTongue = new GameTongueAdapter.GameTongue(
                    ownedGame.getGame().getGameID(),
                    ownedGame.getGame().getGameName(),
                    pricePerHour,
                    UnitsConverterHelper.displayMinutesInHours(ownedGame.getTimePlayedForever()),
                    100,
                    ownedGame.getGame().getGameIcon().toString()
            );
            currentGameTongue.setGameRank(rank);
            gameTongues.add(currentGameTongue);
        }
        return gameTongues;
    }

    /**
     * Display the list of GameTongues in the ListView in parameter.
     * If there is no GameTongue in the list, the cardView visibility is set to GONE.
     *
     * @param context               Environment variables.
     * @param tvMessageIfNoContent  TextView displaying a message when there is no content.
     * @param listViewToFulfill     ListView in which we want to display the gameTongues.
     * @param listener              Listener to apply on each View of the ListView
     * @param resizeHeight          True if we want to resize the height of the ListView depending on the number of elements.
     * @param gameTongues           elements to display in the ListView.
     */
    public static void displayGameTongues(Context context, TextView tvMessageIfNoContent, ListView listViewToFulfill, List<GameTongueAdapter.GameTongue> gameTongues, AdapterView.OnItemClickListener listener, boolean resizeHeight) {

        // We check if there is games in the list to show/hide the card
        if (listViewToFulfill != null && gameTongues.size() == 0) {
            listViewToFulfill.setVisibility(View.GONE);
            tvMessageIfNoContent.setVisibility(View.VISIBLE);
        } else {
            if (listViewToFulfill != null) {
                listViewToFulfill.setVisibility(View.VISIBLE);
                tvMessageIfNoContent.setVisibility(View.GONE);
            }

            // We create a new Adapter, set it to the ListView and set the ListViewHeight using the helper
            GameTongueAdapter gameTongueAdapter = new GameTongueAdapter(context, gameTongues);
            listViewToFulfill.setAdapter(gameTongueAdapter);
            if (resizeHeight) {
                UiUtilities.setListViewHeightBasedOnItems(listViewToFulfill);
            }
            listViewToFulfill.setOnItemClickListener(listener);
        }
    }
}
