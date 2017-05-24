package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.User;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.UnitsConverterHelper;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * AccountFragment.java
 * Purpose: Inflate and manage fragment_account layout.
 *
 * @author Joffrey LAGUT
 * @version 1.1 2017-05-24
 */

public class AccountFragment extends Fragment {

    @BindView(R.id.account_picture)
    ImageView mIvUserPicture;
    @BindView(R.id.account_nb_games)
    TextView mTvNbGames;
    @BindView(R.id.account_nb_hours_played)
    TextView mTvNbHoursPlayed;
    @BindView(R.id.account_money_spent)
    TextView mTvMoneySpent;
    @BindView(R.id.account_average_price_per_hour)
    TextView mTvAveragePricePerHour;
    @BindView(R.id.account_message)
    TextView mTvMessage;
    @BindView(R.id.account_no_game_price_message)
    TextView mtvNoGamePriceMessage;
    @BindView(R.id.toolbar)
    Toolbar mTbUserName;
    @BindView(R.id.action_share)
    Button mBtnShare;

    private UserDbHelper mUserDbHelper;
    private SQLiteDatabase mDb;
    private int mUserId;
    private User mUser;
    private String mCurrency;
    private Double mProfitableThreshold;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, viewRoot);

        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mTbUserName);

        initializeGlobalVars();
        fetchAndDisplayUserProfile();
        setGoalStatusMessage();

        return viewRoot;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivityWithAnimation(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start the activity with an animation if the operating system support it.
     *
     * @param intent
     */
    private void startActivityWithAnimation(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(getContext(),
                    R.transition.right_to_left_incoming, R.transition.right_to_left_outgoing)
                    .toBundle();
            startActivity(intent, bndlAnimation);
        } else {
            startActivity(intent);
        }
    }

    /**
     * Display a message depending on the current price per hour of the user.
     */
    private void setGoalStatusMessage() {
        String message = generateCompletionMessage(mUser.getAveragePricePerHour(), mProfitableThreshold);
        mTvMessage.setText(message);
    }

    /**
     * Return the message to put into the message text view.
     *
     * @param averagePricePerHour of the user.
     * @param profitableThreshold set in the preferences.
     * @return a string ready to be inserted into the text view.
     */
    private String generateCompletionMessage(double averagePricePerHour, Double profitableThreshold) {

        String message;

        Double achievement75PercentsCompletedThreshold = profitableThreshold + ((profitableThreshold * 25) / 100);

        if (averagePricePerHour <= profitableThreshold) {
            message = getResources().getString(R.string.profile_goal_reached);
        } else if (averagePricePerHour > profitableThreshold && averagePricePerHour <= achievement75PercentsCompletedThreshold) {
            message = getResources().getString(R.string.profile_goal_25_percent_reached);
        } else {
            message = getResources().getString(R.string.profile_goal_not_reached);
        }

        return message;
    }

    /**
     * Fetch the user profile information.
     */
    private void fetchAndDisplayUserProfile() {
        mUser = mUserDbHelper.getUserByID(mDb, mUserId, true);

        mTbUserName.setTitle(mUser.getAccountName());
        Picasso.with(getContext()).load(mUser.getAccountPicture().toString()).into(mIvUserPicture);
        String nbGamesPlayed = String.valueOf(mUser.getOwnedGames().size()) + " " + getResources().getString(R.string.games);
        mTvNbGames.setText(nbGamesPlayed);
        String nbHoursPlayed = UnitsConverterHelper.displayMinutesInHours(mUser.getNbMinutesPlayed()) + " " + getResources().getString(R.string.played);
        mTvNbHoursPlayed.setText(nbHoursPlayed);
        String moneySpent;
        if (mUser.getTotalMoneySpent() == 0) {
            mTvMessage.setVisibility(View.GONE);
            mtvNoGamePriceMessage.setVisibility(View.VISIBLE);
            moneySpent = "0";
        } else {
            mTvMessage.setVisibility(View.VISIBLE);
            mtvNoGamePriceMessage.setVisibility(View.GONE);
            moneySpent = String.valueOf(UnitsConverterHelper.formatDouble(mUser.getTotalMoneySpent()));
        }
        moneySpent += mCurrency + " " + getResources().getString(R.string.money_spent);
        mTvMoneySpent.setText(moneySpent);
        String pricePerHour = UnitsConverterHelper.createPricePerHour(mUser.getAveragePricePerHour(), mCurrency);
        mTvAveragePricePerHour.setText(pricePerHour);

    }

    /**
     * Initialize the global variables.
     */
    private void initializeGlobalVars() {
        mUserDbHelper = UserDbHelper.getInstance(getContext());
        mDb = mUserDbHelper.getWritableDatabase();
        mUserId = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(SharedPreferencesHelper.USER_ID, 0);
        mCurrency = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.CURRENCY, "$");
        mProfitableThreshold = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(SharedPreferencesHelper.PROFITABLE_LIMIT, "1"));
    }

    @OnClick(R.id.action_share)
    public void share(View v) {
        String shareMessage = getResources().getString(R.string.share_message) + " " + getResources().getString(R.string.share_link);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

}
