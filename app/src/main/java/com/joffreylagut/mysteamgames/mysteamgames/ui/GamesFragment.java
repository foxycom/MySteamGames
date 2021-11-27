package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameListAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.GameListSorter;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * GamesFragment.java
 * Purpose: Inflate and manage fragment_games layout.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-09
 */
public class GamesFragment extends Fragment implements View.OnClickListener {

    private static final String ORDER_ALPHABETIC = "orderAlphabetic";
    private static final String ORDER_TIME_PLAYED = "orderTimePlayed";
    private static final String ORDER_PRICE_PER_HOUR = "orderPricePerHour";
    private static final boolean SORT_ASC = true;
    private static final boolean SORT_DESC = false;

    @BindView(R.id.action_open_fab_menu)
    FloatingActionsMenu mFam;

    @BindView(R.id.action_order_alphabetic)
    FloatingActionButton mFabSortAlphabetical;

    @BindView(R.id.action_order_price_per_hour)
    FloatingActionButton mFabSortPricePerHour;

    @BindView(R.id.action_order_time_played)
    FloatingActionButton mfabSortTimePlayed;

    @BindView(R.id.action_reverse_order)
    FloatingActionButton mFabReverseOrder;

    @BindView(R.id.rv_games)
    RecyclerView mRvGames;

    interface OnGameSelectedListener {
        void OnGameSelected(int gameId);
    }

    OnGameSelectedListener mCallback;
    private boolean mCurrentSort = SORT_ASC;
    private String mCurrentOrder = ORDER_ALPHABETIC;
    private UserDbHelper mUserDbHelper;
    private SQLiteDatabase mDb;

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
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        // Bind the views
        ButterKnife.bind(this, rootView);

        // Database tools
        mUserDbHelper = UserDbHelper.getInstance(getContext());
        mDb = mUserDbHelper.getWritableDatabase();

        GameListAdapter gameListAdapter = new GameListAdapter(getGameListFromDb(), getContext(), this);
        mRvGames.setAdapter(gameListAdapter);
        mRvGames.setLayoutManager(new GridLayoutManager(getContext(), 2));
        sortGamesDisplayed();

        return rootView;
    }

    /**
     * Get the list of games to display from DB.
     *
     * @return the list of games to display.
     */
    private List<GameListItem> getGameListFromDb() {

        // We get the userId from the sharedPreferences
        int userId = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(SharedPreferencesHelper.USER_ID, 0);

        // Set up the RecyclerView
        List<OwnedGame> userOwnedGames = mUserDbHelper.getOwnedGamesByUserID(mDb, userId);
        return createGameListItemsFromGameList(userOwnedGames);
    }

    /**
     * Refresh the data inside of the RecyclerView and set the previous position.
     */
    @Override
    public void onResume() {
        super.onResume();
        // We have to get the current position of the RecyclerView
        GridLayoutManager layoutManager = (GridLayoutManager) mRvGames.getLayoutManager();
        int currentPosition = layoutManager.findFirstVisibleItemPosition();

        // Then we refresh the data from db
        GameListAdapter gameListAdapter = new GameListAdapter(getGameListFromDb(), getContext(), this);
        mRvGames.setAdapter(gameListAdapter);
        sortGamesDisplayed();

        // We set the position of the RecyclerView
        layoutManager.scrollToPosition(currentPosition);
    }

    /**
     * This function transforms a List<OwnedGame> into a List<GameListItem> usable by the
     * GameListAdapters.
     *
     * @param ownedGames List<OwnedGame> that you want to convert.
     * @return List<GameListItem> that can be used by GameListAdapters.
     */
    public static List<GameListItem> createGameListItemsFromGameList(List<OwnedGame> ownedGames) {

        List<GameListItem> gameListItems = new ArrayList<>();
        GameListItem item;

        if (ownedGames != null) {
            for (OwnedGame ownedGame : ownedGames) {
                item = new GameListItem();
                item.setGameTimePlayed(ownedGame.getTimePlayedForever());
                item.setGameImage(ownedGame.getGame().getGameLogo());
                item.setGameName(ownedGame.getGame().getGameName());
                item.setGamePrice(ownedGame.getGamePrice());
                item.setGameID(ownedGame.getGame().getGameID());
                item.setUserID(ownedGame.getUserId());
                gameListItems.add(item);
            }
        } else {
            Log.e("Error in ", "createGameListItemsFromGameList: the argument ownedGames shouldn't be null. Returning empty list.");
        }

        return gameListItems;
    }

    /**
     * Called when the user click on a game.
     * Get the gameId and send it back to the activity.
     *
     * @param v view clicked.
     */
    @Override
    public void onClick(View v) {

        // We close the FloatingActionMenu
        mFam.collapse();

        // We have to retrieve the gameId
        int childId = mRvGames.getChildAdapterPosition(v);
        GameListAdapter gameListAdapter = (GameListAdapter) mRvGames.getAdapter();
        GameListItem gameListItem = gameListAdapter.getGameList().get(childId);

        // Then we send it to the activity via callback.
        mCallback.OnGameSelected(gameListItem.getGameID());
    }

    /**
     * Called when the user click on a FloatingActionButton.
     * Sort or change the order of the items in the RecyclerView.
     *
     * @param v View that have called the function.
     */
    @OnClick({R.id.action_order_alphabetic, R.id.action_order_time_played, R.id.action_order_price_per_hour, R.id.action_reverse_order})
    public void filter(View v) {
        switch (v.getId()) {
            case R.id.action_order_alphabetic:
                mCurrentOrder = ORDER_ALPHABETIC;
                break;
            case R.id.action_reverse_order:
                if (mCurrentSort == SORT_ASC) {
                    Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_downward_white, null);
                    if (icon != null) {
                        mFabReverseOrder.setIconDrawable(icon);
                    }
                    mCurrentSort = SORT_DESC;
                } else {
                    Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_upward_white, null);
                    if (icon != null) {
                        mFabReverseOrder.setIconDrawable(icon);
                    }
                    mCurrentSort = SORT_ASC;
                }
                break;
            case R.id.action_order_time_played:
                mCurrentOrder = ORDER_TIME_PLAYED;
                break;
            case R.id.action_order_price_per_hour:
                mCurrentOrder = ORDER_PRICE_PER_HOUR;
        }
        sortGamesDisplayed();

        mFam.collapse();
    }

    /**
     * Sort the GameListItems in the RecyclerView depending on the global variables
     * mCurrentSort & mCurrentOrder.
     */
    private void sortGamesDisplayed() {
        GameListAdapter gameListAdapter = (GameListAdapter) mRvGames.getAdapter();
        List<GameListItem> gameListItems = gameListAdapter.getGameList();

        switch (mCurrentOrder) {
            case ORDER_ALPHABETIC:
                gameListItems = GameListSorter.sortByName(gameListItems, mCurrentSort);
                break;
            case ORDER_PRICE_PER_HOUR:
                gameListItems = GameListSorter.sortByPricePerHour(gameListItems, mCurrentSort);
                break;
            case ORDER_TIME_PLAYED:
                gameListItems = GameListSorter.sortByTimePlayed(gameListItems, mCurrentSort);
        }
        gameListAdapter.setGameList(gameListItems);
        mRvGames.setAdapter(gameListAdapter);
    }
}
