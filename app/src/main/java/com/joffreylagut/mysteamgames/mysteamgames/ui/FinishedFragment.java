package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.GameTongueHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FinishedFragment.java
 * Purpose: Inflate and manage fragment_finished layout.
 *
 * @author Joffrey LAGUT
 * @version 1.1 2017-05-17
 */

public class FinishedFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.finished_card_list_view)
    ListView mGoalsFinished;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_finished, container, false);
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
        fetchGoalsFinished();
    }

    /**
     * Fetch the goals from db, extract the goals finished and display them in the list view.
     */
    private void fetchGoalsFinished() {

        List<OwnedGame> goalsFromDb = mUserDbHelper.getUserOwnedGamesWithPriceAlreadyPlayed(mDb, mUserId);
        List<OwnedGame> goalsFinished = extractAndSortGoalsFinished(goalsFromDb, mProfitableThreshold);

        List<GameTongueAdapter.GameTongue> gameTongues = GameTongueAdapter.convertOwnedGameListToGameTongueList(goalsFinished, mCurrency, mProfitableThreshold);

        GameTongueHelper.displayGameTongues(getContext(), null, mGoalsFinished, gameTongues, this, false);
    }

    /**
     * Extract and return the finished goals from the list in parameter.
     * The list is sorted by price per hour.
     *
     * @param goalsFromDb all the goals.
     * @return only the goals finished.
     */
    static List<OwnedGame> extractAndSortGoalsFinished(List<OwnedGame> goalsFromDb, Double profitableThreshold) {
        List<OwnedGame> goalsFinished = new ArrayList<>();

        for (OwnedGame currentOwnedGame : goalsFromDb) {
            if (currentOwnedGame.getPricePerHour() < profitableThreshold) {
                goalsFinished.add(currentOwnedGame);
            }
        }

        Collections.sort(goalsFinished, new OwnedGame.OwnedGamePricePerHourComparator());
        return goalsFinished;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // We get the GameTongue clicked item
        GameTongueAdapter.GameTongue gameTongueClicked = (GameTongueAdapter.GameTongue) parent.getItemAtPosition(position);
        mCallback.OnGameSelected(gameTongueClicked.getGameId());
    }
}
