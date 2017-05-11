package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.UiUtilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * HomeFragment.java
 * Purpose: Inflate and manage fragment_home layout.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class HomeFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.home_favorite_goals_card)
    CardView mCvFavorites;
    @BindView(R.id.home_goals_card)
    CardView mCvGoals;
    @BindView(R.id.home_most_played_card)
    CardView mCvMostPlayed;
    @BindView(R.id.home_most_profitable_card)
    CardView mCvMostProfitable;

    @BindView(R.id.favorite_goals_card_list_view)
    ListView mLvFavorites;
    @BindView(R.id.goals_card_list_view)
    ListView mLvGoals;
    @BindView(R.id.most_played_card_list_view)
    ListView mLvMostPlayed;
    @BindView(R.id.most_profitable_card_list_view)
    ListView mLvMostProfitable;

    interface OnGameSelectedListener {
        void OnGameSelected(int gameId);
    }

    private OnGameSelectedListener mCallback;

    private UserDbHelper mUserDbHelper;
    private SQLiteDatabase mDb;
    private int mUserId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnGameSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGameSelectedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, viewRoot);

        mUserDbHelper = UserDbHelper.getInstance(getContext());
        mDb = mUserDbHelper.getWritableDatabase();
        mUserId = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(SharedPreferencesHelper.USER_ID, 0);


        fetchFavorites();


        displayGameTongues(mCvGoals, mLvGoals, SampleGenerator.generateListGameTongue(getContext()));
        displayGameTongues(mCvMostPlayed, mLvMostPlayed, SampleGenerator.generateListGameTongue(getContext()));
        displayGameTongues(mCvMostProfitable, mLvMostProfitable, SampleGenerator.generateListMostProfitableGameTongue(getContext()));

        return viewRoot;
    }

    /**
     * This method fetch the favorites games in DB and display them in the Favorite card.
     */
    private void fetchFavorites() {

        List<OwnedGame> favoritesOwnedGames = mUserDbHelper.getFavoritesOwnedGamesByUserID(mDb, mUserId);
        List<GameTongueAdapter.GameTongue> favoritesGameTongues = convertOwnedGameToGameTongue(favoritesOwnedGames);
        displayGameTongues(mCvFavorites, mLvFavorites, favoritesGameTongues);

    }

    /**
     * Convert the list of OwnedGames in parameter into a a list of GameTongue objects.
     *
     * @param ownedGames List of OwnedGames to convert.
     * @return a list of GameTongue containing the OwnedGames that were in parameter.
     */
    private List<GameTongueAdapter.GameTongue> convertOwnedGameToGameTongue(List<OwnedGame> ownedGames) {

        String currency = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.CURRENCY, "$");
        List<GameTongueAdapter.GameTongue> gameTongues = new ArrayList<>();

        Double profitableThreshold = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.PROFITABLE_LIMIT, "1"));


        if (ownedGames != null) {
            for (OwnedGame ownedGame : ownedGames) {

                // We have 5 type of GameTongue:
                // 1-Games Never played without price
                // 2-Games Never played with price
                // 3-Game played without price
                // 4-Game played with price per hour over threshold
                // 5-Game played with price per hour under threshold

                // By default, the caption and the progression are empty
                String gameCaption;
                String gameProgression = "";

                Double nbHoursToReachThreshold;
                Double nbHoursPlayed = (double) ownedGame.getTimePlayedForever() / 60;

                // Types 1 & 2
                if (ownedGame.getTimePlayedForever() == 0) {
                    if (ownedGame.getGamePrice() <= 0) {
                        // Type 1
                        gameCaption = "0h";
                    } else {
                        // Type 2
                        nbHoursToReachThreshold = ownedGame.getGamePrice() / profitableThreshold;
                        gameCaption = nbHoursPlayed.intValue() + " / " + nbHoursToReachThreshold.intValue() + "h";
                        gameProgression = "0%";
                    }
                } else {
                    if (ownedGame.getGamePrice() <= 0) {
                        // Type 3
                        gameCaption = SteamAPICalls.convertTimePlayed(ownedGame.getTimePlayedForever());
                    } else {
                        Double pricePerHour = ownedGame.getGamePrice() / nbHoursPlayed;
                        if (pricePerHour > profitableThreshold) {
                            // Type 4
                            nbHoursToReachThreshold = ownedGame.getGamePrice() / profitableThreshold;
                            gameCaption = nbHoursPlayed.intValue() + " / " + nbHoursToReachThreshold.intValue() + "h";
                            Double completion = (nbHoursPlayed / nbHoursToReachThreshold) * 100;
                            gameProgression = String.valueOf(completion.intValue()) + "%";
                        } else {
                            // Type 5
                            gameCaption = SteamAPICalls.convertTimePlayed(ownedGame.getTimePlayedForever());

                            DecimalFormat df = new DecimalFormat("#.##");
                            gameProgression = df.format(pricePerHour) + currency + "/h";

                        }
                    }
                }

                GameTongueAdapter.GameTongue currentGameTongue = new GameTongueAdapter.GameTongue(
                        ownedGame.getGame().getGameID(),
                        ownedGame.getGame().getGameName(),
                        gameCaption,
                        gameProgression
                );
                gameTongues.add(currentGameTongue);
            }
        }
        return gameTongues;
    }

    /**
     * Display the list of GameTongues in the ListView in parameter.
     * If there is no GameTongue in the list, the cardView visibility is set to GONE.
     *
     * @param cardViewDisplayed CardView that contains the ListView to fulfill.
     * @param listViewToFulfill ListView in which we want to display the gameTongues.
     * @param gameTongues       elements to display in the ListView.
     */
    private void displayGameTongues(CardView cardViewDisplayed, ListView listViewToFulfill, List<GameTongueAdapter.GameTongue> gameTongues) {

        // We check if there is games in the list to show/hide the card
        if (gameTongues.size() == 0) {
            cardViewDisplayed.setVisibility(View.GONE);
        } else {
            cardViewDisplayed.setVisibility(View.VISIBLE);

            // We create a new Adapter, set it to the ListView and set the ListViewHeight using the helper
            GameTongueAdapter gameTongueAdapter = new GameTongueAdapter(getContext(), gameTongues);
            listViewToFulfill.setAdapter(gameTongueAdapter);
            UiUtilities.setListViewHeightBasedOnItems(listViewToFulfill);

            listViewToFulfill.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // We get the GameTongue clicked item
        GameTongueAdapter.GameTongue gameTongueClicked = (GameTongueAdapter.GameTongue) parent.getItemAtPosition(position);

        mCallback.OnGameSelected(gameTongueClicked.getGameId());
    }
}
