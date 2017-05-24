package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.Goal;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.ExpandAnimation;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.GameTongueHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * GoalsFragment.java
 * Purpose: Inflate and manage fragment_goals layout.
 *
 * @author Joffrey LAGUT
 * @version 1.2 2017-05-24
 */

public class GoalsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    @BindView(R.id.goals_in_progress_linear_layout)
    LinearLayout mLlInProgress;
    @BindView(R.id.goals_not_started_linear_layout)
    LinearLayout mLlNotStarted;
    @BindView(R.id.games_without_price_linear_layout)
    LinearLayout mLlGamesWithoutPrice;

    @BindView(R.id.goals_in_progress_card)
    CardView mCvInProgress;
    @BindView(R.id.goals_not_started_card)
    CardView mCvNotStarted;
    @BindView(R.id.games_without_price_card)
    CardView mCvGamesWithoutPrice;

    @BindView(R.id.goals_in_progress_card_list_view)
    ListView mLvGoalsInProgress;
    @BindView(R.id.goals_not_started_card_list_view)
    ListView mLvGoalsNotStarted;
    @BindView(R.id.games_without_price_card_list_view)
    ListView mLvGamesWithoutPrice;

    @BindView(R.id.goals_in_progress_card_icon)
    ImageView mIvInProgressIcon;
    @BindView(R.id.goals_in_progress_card_title)
    TextView mTvInProgressTitle;
    @BindView(R.id.goals_in_progress_card_no_content_message)
    TextView mTvNoGoalsInProgress;

    @BindView(R.id.goals_not_started_card_icon)
    ImageView mIvNotStartedIcon;
    @BindView(R.id.goals_not_started_card_title)
    TextView mTvNotStartedTitle;
    @BindView(R.id.goals_not_started_card_no_content_message)
    TextView mTvNoGoalsNotStarted;

    @BindView(R.id.games_without_price_card_icon)
    ImageView mIvGameWithoutPriceIcon;
    @BindView(R.id.games_without_price_card_title)
    TextView mTvGameWithoutPriceTitle;
    @BindView(R.id.games_without_price_card_no_content_message)
    TextView mTvNoGameWithoutPrice;

    private OnGameSelectedListener mCallback;
    private UserDbHelper mUserDbHelper;
    private SQLiteDatabase mDb;
    private int mUserId;
    private String mCurrency;
    private Double mProfitableThreshold;

    private LinearLayout mLlCurrentlyOpened;

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
        View viewRoot = inflater.inflate(R.layout.fragment_goals, container, false);
        ButterKnife.bind(this, viewRoot);

        setGlobalVars();
        setAllListeners();

        // By default, the goals in progress are opened
        mLlCurrentlyOpened = mLlInProgress;
        disableCardGoalsInProgressListeners();

        return viewRoot;
    }

    /**
     * Set the global vars of the fragment.
     */
    private void setGlobalVars() {
        mUserDbHelper = UserDbHelper.getInstance(getContext());
        mDb = mUserDbHelper.getWritableDatabase();
        mUserId = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(SharedPreferencesHelper.USER_ID, 0);
        mCurrency = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.CURRENCY, "$");
        mProfitableThreshold = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.PROFITABLE_LIMIT, "1"));
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchGoalsInProgress();
        fetchGoalsNotStartedYet();
        fetchGameWithoutPrice();
        expandCardWithInformation();
    }

    /**
     * Check each card to see if there is content.
     * Display the one that have content, or the no price card if none of them have content.
     */
    private void expandCardWithInformation() {
        LinearLayout linearLayoutToExpand;

        setAllListeners();

        if (mLvGoalsInProgress.getVisibility() != View.GONE) {
            linearLayoutToExpand = mLlInProgress;
            disableCardGoalsInProgressListeners();
        } else if (mLvGoalsNotStarted.getVisibility() != View.GONE) {
            linearLayoutToExpand = mLlNotStarted;
            disableCardGoalsNotStartedListeners();
        } else {
            linearLayoutToExpand = mLlGamesWithoutPrice;
            disableCardGamesWithoutPriceListeners();
        }

        launchAnimation(linearLayoutToExpand);

    }

    @OnClick({R.id.goals_in_progress_card_icon, R.id.goals_in_progress_card_title, R.id.goals_not_started_card_icon, R.id.goals_not_started_card_title, R.id.games_without_price_card_icon, R.id.games_without_price_card_title})
    @Override
    public void onClick(View v) {
        setAllListeners();
        switch (v.getId()) {
            case R.id.goals_in_progress_linear_layout:
            case R.id.goals_in_progress_card_icon:
            case R.id.goals_in_progress_card_title:
                disableCardGoalsInProgressListeners();
                launchAnimation(mLlInProgress);
                break;
            case R.id.goals_not_started_linear_layout:
            case R.id.goals_not_started_card_icon:
            case R.id.goals_not_started_card_title:
                disableCardGoalsNotStartedListeners();
                launchAnimation(mLlNotStarted);
                break;
            case R.id.games_without_price_linear_layout:
            case R.id.games_without_price_card_icon:
            case R.id.games_without_price_card_title:
                disableCardGamesWithoutPriceListeners();
                launchAnimation(mLlGamesWithoutPrice);
                break;
        }
    }

    /**
     * Launch the expand animation on the LinearLayout in parameter and launch the folding animation
     * on the currently opened LinearLayout.
     *
     * @param llToExpand LinearLayout that we want to expand.
     */
    private void launchAnimation(final LinearLayout llToExpand) {

        // Create the and assign the animations
        ExpandAnimation expand = new ExpandAnimation(llToExpand, 0, 1);
        expand.setDuration(500);
        ExpandAnimation fold = new ExpandAnimation(mLlCurrentlyOpened, 1, 0);
        fold.setDuration(500);

        // Start the animations
        llToExpand.startAnimation(expand);
        mLlCurrentlyOpened.startAnimation(fold);

        // Switch the currently opened LinearLayout for the next time
        mLlCurrentlyOpened = llToExpand;
    }

    /**
     * Disable all the onClickListeners on the goals in progress card.
     *
     */
    private void disableCardGoalsInProgressListeners() {
        mIvInProgressIcon.setOnClickListener(null);
        mTvInProgressTitle.setOnClickListener(null);
        mLlInProgress.setOnClickListener(null);
    }

    /**
     * Disable all the onClickListeners on the goals not started card.
     */
    private void disableCardGoalsNotStartedListeners() {
        mIvNotStartedIcon.setOnClickListener(null);
        mTvNotStartedTitle.setOnClickListener(null);
        mLlNotStarted.setOnClickListener(null);
    }

    /**
     * Disable all the onClickListeners on the games without price card.
     */
    private void disableCardGamesWithoutPriceListeners() {
        mIvGameWithoutPriceIcon.setOnClickListener(null);
        mTvGameWithoutPriceTitle.setOnClickListener(null);
        mLlGamesWithoutPrice.setOnClickListener(null);
    }

    /**
     * Set all the onClickListeners of the layout and set the weight of each card to 0.
     */
    private void setAllListeners() {
        mIvInProgressIcon.setOnClickListener(this);
        mTvInProgressTitle.setOnClickListener(this);
        mLlInProgress.setOnClickListener(this);

        mIvNotStartedIcon.setOnClickListener(this);
        mTvNotStartedTitle.setOnClickListener(this);
        mLlNotStarted.setOnClickListener(this);

        mLlGamesWithoutPrice.setOnClickListener(this);
        mIvGameWithoutPriceIcon.setOnClickListener(this);
        mTvGameWithoutPriceTitle.setOnClickListener(this);
    }

    /**
     * Display the games without price.
     */
    private void fetchGameWithoutPrice() {

        // We fetch the OwnedGames in DB
        List<OwnedGame> goalsToDisplay = mUserDbHelper.getUserOwnedGamesWithoutPrice(mDb, mUserId);
        Collections.sort(goalsToDisplay, new OwnedGame.OwnedGameTimePlayedComparator());
        Collections.reverse(goalsToDisplay);

        // We convert this list in a GameTongue list
        List<GameTongueAdapter.GameTongue> gameTongueList = GameTongueAdapter.convertOwnedGameListToGameTongueList(goalsToDisplay, mCurrency, mProfitableThreshold);

        // And display them in the ListView
        GameTongueHelper.displayGameTongues(getContext(), mTvNoGameWithoutPrice, mLvGamesWithoutPrice, gameTongueList, this, false);

    }

    /**
     * Display the goals not started yet.
     */
    private void fetchGoalsNotStartedYet() {

        // We fetch the OwnedGames in DB
        List<OwnedGame> goalsToDisplay = mUserDbHelper.getUserOwnedGamesWithPrice(mDb, mUserId);
        goalsToDisplay = extractAndSortGoals(goalsToDisplay, false, mProfitableThreshold);

        // We convert this list in a GameTongue list
        List<GameTongueAdapter.GameTongue> gameTongueList = GameTongueAdapter.convertOwnedGameListToGameTongueList(goalsToDisplay, mCurrency, mProfitableThreshold);

        // And display them in the ListView
        GameTongueHelper.displayGameTongues(getContext(), mTvNoGoalsNotStarted, mLvGoalsNotStarted, gameTongueList, this, false);

    }

    /**
     * Display the goals in progress.
     */
    private void fetchGoalsInProgress() {

        // We fetch the OwnedGames in DB
        List<OwnedGame> goalsToDisplay = mUserDbHelper.getUserOwnedGamesWithPriceAlreadyPlayed(mDb, mUserId);
        goalsToDisplay = extractAndSortGoals(goalsToDisplay, true, mProfitableThreshold);

        // We convert this list in a GameTongue list
        List<GameTongueAdapter.GameTongue> gameTongueList = GameTongueAdapter.convertOwnedGameListToGameTongueList(goalsToDisplay, mCurrency, mProfitableThreshold);

        // And display them in the ListView
        GameTongueHelper.displayGameTongues(getContext(), mTvNoGoalsInProgress, mLvGoalsInProgress, gameTongueList, this, false);

    }

    /**
     * Return a list of OwnedGame that contain only goals.
     * The goals are sorted by completion percentage descendant.
     *
     * @param ownedGames List that we want to extract the goals.
     * @return a list of goals.
     */
    static List<OwnedGame> extractAndSortGoals(List<OwnedGame> ownedGames, boolean alreadyPlayed, Double profitableThreshold) {

        List<OwnedGame> goalsList = new ArrayList<>();
        for (OwnedGame currentOwnedGame : ownedGames) {
            if (alreadyPlayed) {
                if (currentOwnedGame.getTimePlayedForever() > 0 && currentOwnedGame.getPricePerHour() > profitableThreshold) {
                    goalsList.add(new Goal(profitableThreshold, currentOwnedGame));
                }
            } else {
                if (currentOwnedGame.getTimePlayedForever() == 0 && currentOwnedGame.getGamePrice() > 0) {
                    goalsList.add(new Goal(profitableThreshold, currentOwnedGame));
                }
            }
        }
        if (alreadyPlayed) {
            Collections.sort(goalsList, new Goal.GoalCompletionComparatorFromOwnedGame());
            Collections.reverse(goalsList);
        } else {
            Collections.sort(goalsList, new Goal.GoalNbHoursToCompleteComparatorFromOwnedGame());
        }
        return goalsList;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // We get the GameTongue clicked item
        GameTongueAdapter.GameTongue gameTongueClicked = (GameTongueAdapter.GameTongue) parent.getItemAtPosition(position);
        mCallback.OnGameSelected(gameTongueClicked.getGameId());
    }
}
