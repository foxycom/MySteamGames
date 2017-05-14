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
import com.joffreylagut.mysteamgames.mysteamgames.models.Goal;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.UiUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter.convertOwnedGameListToGameTongueList;

/**
 * HomeFragment.java
 * Purpose: Inflate and manage fragment_home layout.
 *
 * @author Joffrey LAGUT
 * @version 1.1 2017-05-12
 */

public class HomeFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {

    private static final int NUMBER_OF_GOALS_ALMOST_ACHIEVED = 3;
    private static final int NUMBER_OF_MOST_PROFITABLE = NUMBER_OF_GOALS_ALMOST_ACHIEVED;
    private static final int NUMBER_OF_MOST_PLAYED = NUMBER_OF_GOALS_ALMOST_ACHIEVED;

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

    private OnGameSelectedListener mCallback;
    private UserDbHelper mUserDbHelper;
    private SQLiteDatabase mDb;
    private int mUserId;
    private String mCurrency;
    private Double mProfitableThreshold;

    interface OnGameSelectedListener {
        void OnGameSelected(int gameId);
    }

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
        mCurrency = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.CURRENCY, "$");
        mProfitableThreshold = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.PROFITABLE_LIMIT, "1"));

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        // We display the information in the cards
        fetchFavorites();
        fetchMostPlayed();
        fetchMostProfitable();
        fetchGoalsAlmostAchieved();
    }

    /**
     * This method display the almost achieved goals.
     */
    private void fetchGoalsAlmostAchieved() {

        List<OwnedGame> ownedGamesWithPrice = mUserDbHelper.getUserOwnedGamesWithPrice(mDb, mUserId);
        List<Goal> goals = new ArrayList<>();

        for (OwnedGame currentOwnedGame : ownedGamesWithPrice) {
            if (currentOwnedGame.getPricePerHour() > mProfitableThreshold) {
                Goal currentGoal = new Goal(getContext(), currentOwnedGame);
                goals.add(currentGoal);
            }
        }

        Collections.sort(goals, new Goal.GoalCompletionComparator());
        Collections.reverse(goals);

        List<OwnedGame> ownedGamesAlmostAchieved = new ArrayList<>();
        int nbIterationMax = NUMBER_OF_GOALS_ALMOST_ACHIEVED;
        if (goals.size() < nbIterationMax) {
            nbIterationMax = goals.size();
        }
        for (int i = 0; i < nbIterationMax; i++) {
            ownedGamesAlmostAchieved.add(goals.get(i));
        }

        List<GameTongueAdapter.GameTongue> almostAchievedGameTongues = convertOwnedGameListToGameTongueList(ownedGamesAlmostAchieved, mCurrency, mProfitableThreshold);
        displayGameTongues(mCvGoals, mLvGoals, almostAchievedGameTongues);

    }

    /**
     * This method fetch the most played games in DB and display them in the Most played card.
     */
    private void fetchMostPlayed() {

        List<OwnedGame> mostPlayedOwnedGames = mUserDbHelper.getUserMostPlayedOwnedGames(mDb, mUserId, NUMBER_OF_MOST_PLAYED);
        List<GameTongueAdapter.GameTongue> mostPlayedGameTongues = createMostPlayedGameTongues(mostPlayedOwnedGames);

        displayGameTongues(mCvMostPlayed, mLvMostPlayed, mostPlayedGameTongues);
    }

    /**
     * This method fetch the favorites games in DB and display them in the Favorite card.
     */
    private void fetchFavorites() {

        List<OwnedGame> favoritesOwnedGames = mUserDbHelper.getFavoritesOwnedGamesByUserID(mDb, mUserId);
        List<GameTongueAdapter.GameTongue> favoritesGameTongues = convertOwnedGameListToGameTongueList(favoritesOwnedGames, mCurrency, mProfitableThreshold);
        displayGameTongues(mCvFavorites, mLvFavorites, favoritesGameTongues);

    }

    /**
     * This method fetch the favorites games in DB and display them in the Favorite card.
     */
    private void fetchMostProfitable() {

        List<OwnedGame> ownedGamesWithPrice = mUserDbHelper.getUserOwnedGamesWithPrice(mDb, mUserId);
        Collections.sort(ownedGamesWithPrice, new OwnedGamePricePerHourComparatorDesc());

        List<OwnedGame> ownedGamesMostProfitable = new ArrayList<>();
        int nbIterationMax = NUMBER_OF_MOST_PROFITABLE;
        if (ownedGamesWithPrice.size() < nbIterationMax) {
            nbIterationMax = ownedGamesMostProfitable.size();
        }
        for (int i = 0; i < nbIterationMax; i++) {
            ownedGamesMostProfitable.add(ownedGamesWithPrice.get(i));
        }

        List<GameTongueAdapter.GameTongue> mostProfitableGameTongues = createMostProfitableGameTongues(ownedGamesMostProfitable);
        displayGameTongues(mCvMostProfitable, mLvMostProfitable, mostProfitableGameTongues);

    }

    /**
     * Convert the list of OwnedGames in parameter into a a list of GameTongue objects respecting the format of
     * Most profitable card items.
     *
     * @param mostProfitableOwnedGames list of the most played OwnedGames
     * @return the list converted into a list of GameTongue
     */
    private List<GameTongueAdapter.GameTongue> createMostProfitableGameTongues(List<OwnedGame> mostProfitableOwnedGames) {

        int rank = 0;
        List<GameTongueAdapter.GameTongue> gameTongues = new ArrayList<>();

        for (OwnedGame ownedGame : mostProfitableOwnedGames) {
            rank++;
            GameTongueAdapter.GameTongue currentGameTongue = new GameTongueAdapter.GameTongue(
                    ownedGame.getGame().getGameID(),
                    ownedGame.getGame().getGameName(),
                    SteamAPICalls.convertTimePlayed(ownedGame.getTimePlayedForever()),
                    ownedGame.getPricePerHour() + mCurrency + "/h",
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
    private List<GameTongueAdapter.GameTongue> createMostPlayedGameTongues(List<OwnedGame> mostPlayedOwnedGames) {

        int rank = 0;
        List<GameTongueAdapter.GameTongue> gameTongues = new ArrayList<>();
        String pricePerHour;

        for (OwnedGame ownedGame : mostPlayedOwnedGames) {
            rank++;
            if (ownedGame.getPricePerHour() != -1.0) {
                pricePerHour = String.valueOf(ownedGame.getPricePerHour()) + mCurrency + "/h";
            } else {
                pricePerHour = "";
            }
            GameTongueAdapter.GameTongue currentGameTongue = new GameTongueAdapter.GameTongue(
                    ownedGame.getGame().getGameID(),
                    ownedGame.getGame().getGameName(),
                    pricePerHour,
                    SteamAPICalls.convertTimePlayed(ownedGame.getTimePlayedForever()),
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

    /**
     * Class created to do the comparison.
     */
    private static class OwnedGamePricePerHourComparatorDesc implements Comparator<OwnedGame> {

        @Override
        public int compare(OwnedGame o1, OwnedGame o2) {
            Double pricePerHour1 = o1.getPricePerHour();
            Double pricePerHour2 = o2.getPricePerHour();
            return pricePerHour1.compareTo(pricePerHour2);
        }
    }
}
