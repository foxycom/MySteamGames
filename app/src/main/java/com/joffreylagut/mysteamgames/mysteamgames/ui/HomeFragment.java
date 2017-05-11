package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.UiUtilities;

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
    CardView mCvMostRentable;

    @BindView(R.id.favorite_goals_card_list_view)
    ListView mLvFavorites;
    @BindView(R.id.goals_card_list_view)
    ListView mLvGoals;
    @BindView(R.id.most_played_card_list_view)
    ListView mLvMostPlayed;
    @BindView(R.id.most_profitable_card_list_view)
    ListView mLvMostRentable;

    interface OnGameSelectedListener {
        void OnGameSelected(int gameId);
    }

    OnGameSelectedListener mCallback;

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

        displayGameTongues(mCvFavorites, mLvFavorites, SampleGenerator.generateListGameTongue(getContext()));
        displayGameTongues(mCvGoals, mLvGoals, SampleGenerator.generateListGameTongue(getContext()));
        displayGameTongues(mCvMostPlayed, mLvMostPlayed, SampleGenerator.generateListGameTongue(getContext()));
        displayGameTongues(mCvMostRentable, mLvMostRentable, SampleGenerator.generateListMostProfitableGameTongue(getContext()));

        return viewRoot;
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
