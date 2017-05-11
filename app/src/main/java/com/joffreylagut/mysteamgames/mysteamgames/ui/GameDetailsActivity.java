package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.Game;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class GameDetailsActivity extends AppCompatActivity {

    static final String ARG_GAME_ID = "gameId";
    static final String ARG_USER_ID = "userId";

    private static final String TAG = "GameDetailsActivity";
    SharedPreferences sharedPreferences;
    private UserDbHelper userDbHelper;
    private int gameID;
    private int userID;
    private boolean favorite;
    private int adapterPosition;
    private boolean edited;
    private ImageView ivGameBlurred;
    private ImageView ivGame;
    private TextView tvTimePlayed;
    private TextView tvGamePrice;
    private TextView tvGamePricePerHour;
    private TextView tvBundleName;
    private SQLiteDatabase db;
    private String newPrice = "null";
    private String recyclerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        // First, we are linking our views with the layout
        ivGameBlurred = (ImageView) findViewById(R.id.iv_game_details_picture_blurred);
        ivGame = (ImageView)findViewById(R.id.iv_game_details_picture);
        tvTimePlayed = (TextView)findViewById(R.id.tv_game_details_time_played);
        tvGamePrice = (TextView)findViewById(R.id.tv_game_details_game_price);
        tvGamePricePerHour = (TextView)findViewById(R.id.tv_game_details_game_price_per_hour);
        tvBundleName = (TextView) findViewById(R.id.tv_game_details_bundle_name);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        edited = false;

        // Preparing the database.
        userDbHelper = UserDbHelper.getInstance(this);
        db = userDbHelper.getWritableDatabase();

        // We retrieve the gameID to display the game details.
        Intent intent = getIntent();
        gameID = intent.getIntExtra(ARG_GAME_ID, 0);
        userID = intent.getIntExtra(ARG_USER_ID, 0);
        recyclerName = intent.getStringExtra("recyclerName");
        adapterPosition = intent.getIntExtra("adapterPosition", 0);
        displayGameInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_details_menu, menu);

        MenuItem itemFavorite = menu.findItem(R.id.menu_game_details_favorite);
        if (favorite) {
            itemFavorite.setIcon(R.drawable.ic_star);
        } else {
            itemFavorite.setIcon(R.drawable.ic_star_border_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Fix the error bellow

        switch (item.getItemId()) {
            case R.id.menu_game_details_edit:
                Intent intentEdit = new Intent(this, EditGameActivity.class);
                intentEdit.putExtra(EditGameActivity.ARG_GAME_ID, gameID);
                intentEdit.putExtra(EditGameActivity.ARG_USER_ID, userID);

                // If the user is running on SDK 16 or newer, display a transition.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(this,
                            R.transition.right_to_left_incoming, R.transition.right_to_left_outgoing)
                            .toBundle();
                    this.startActivityForResult(intentEdit, 1, bndlAnimation);
                } else {
                    this.startActivityForResult(intentEdit, 1);
                }
                return true;
            case R.id.menu_game_details_favorite:
                Game currentGame = userDbHelper.getGameById(db, gameID);
                OwnedGame ownedGame = new OwnedGame(userID, currentGame);
                if (favorite) {
                    ownedGame.setFavorite(false);
                    userDbHelper.updateOwnedGame(db, ownedGame, true);
                } else {
                    ownedGame.setFavorite(true);
                    userDbHelper.updateOwnedGame(db, ownedGame, true);
                }
                // We have to change the icon
                favorite = !favorite;
                edited = true;
                invalidateOptionsMenu();
                return true;
            default:
                this.onBackPressed();
                return true;
        }
    }

    private void displayGameInformation(){

        // TODO Fix the error bellow
        // First, we have to get all the information about the owned game
        OwnedGame ownedGame = userDbHelper.getOwnedGame(db, userID, gameID);
        if(ownedGame.getGame().getGameID() != 0){
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(ownedGame.getGame().getGameName());
            }
            if(ownedGame.getGame().getGameLogo() != null) {
                Picasso.with(this).load(ownedGame.getGame().getGameLogo().toString()).into(ivGame);
                Picasso.with(this).load(ownedGame.getGame().getGameLogo().toString()).transform(new BlurTransformation(this, 5)).into(ivGameBlurred);
            }

            TextView tvSpent = (TextView) findViewById(R.id.tv_game_details_spent);

            String priceText;
            switch (String.valueOf(ownedGame.getGamePrice())) {
                case "-1.0":
                    priceText = "?" + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$");
                    tvGamePrice.setText(priceText);
                    tvGamePricePerHour.setText("?" + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h");
                    tvSpent.setVisibility(View.VISIBLE);
                    break;
                // TODO Test the case of the free to play
                case "0":
                    tvGamePrice.setText(getResources().getString(R.string.free));
                    tvGamePricePerHour.setText(getResources().getString(R.string.free));
                    tvSpent.setVisibility(View.INVISIBLE);
                    break;
                default:
                    priceText = ownedGame.getGamePrice() + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$");
                    tvGamePrice.setText(priceText);
                    Double hours = (double) ownedGame.getTimePlayedForever() / 60;
                    Double pricePerHour = ownedGame.getGamePrice() / hours;
                    DecimalFormat df = new DecimalFormat("#.##");
                    tvGamePricePerHour.setText(String.valueOf(df.format(pricePerHour)) + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h");
                    tvSpent.setVisibility(View.VISIBLE);
                    break;
            }
            tvTimePlayed.setText(SteamAPICalls.convertTimePlayed(ownedGame.getTimePlayedForever(), true));

            favorite = ownedGame.isFavorite();

            // We have to check is the game is included in a bundle

            if (ownedGame.getGameBundle() != null) {
                if (ownedGame.getGameBundle().getId() != 0) {
                    tvBundleName.setText(ownedGame.getGameBundle().getName());
                    LinearLayout llBundle = (LinearLayout) findViewById(R.id.ll_game_details_bundle);
                    llBundle.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    llBundle.requestLayout();
                }
            } else {
                LinearLayout llBundle = (LinearLayout) findViewById(R.id.ll_game_details_bundle);
                llBundle.getLayoutParams().height = 0;
                llBundle.requestLayout();
            }
            invalidateOptionsMenu();
        }else{
            // The owned game is not found in DB.
            Log.e(TAG, "displayGameInformation: The user "+ userID + "doesn't own the game with the ID " + gameID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                displayGameInformation();
                String price = tvGamePrice.getText().toString();

                if (!price.equals(getResources().getString(R.string.free))) {
                    price = price.substring(0, price.length() - 1);
                } else {
                    price = "0";
                }
                newPrice = price;
                edited = true;

                CoordinatorLayout coordinatorLayout =
                        (CoordinatorLayout) findViewById(R.id.activity_game_details_coordinator);

                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        R.string.snackbar_game_saved, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("adapterPosition", adapterPosition);
        if (edited) {
            switch (newPrice) {
                case "null":
                    break;
                case "Fre":
                    returnIntent.putExtra("newPrice", "0");
                    break;
                default:
                    returnIntent.putExtra("newPrice", newPrice);
                    break;
            }
            returnIntent.putExtra("recyclerName", recyclerName);
            returnIntent.putExtra("favorite", favorite);
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
        // We want to display an animation to go back on the previous activity
        overridePendingTransition(R.transition.left_to_right_incoming,
                R.transition.left_to_right_outgoing);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
