package com.joffreylagut.mysteamgames.mysteamgames;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.data.UserContract;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String TAG = "GameDetailsActivity";
    SharedPreferences sharedPreferences;
    private UserDbHelper userDbHelper;
    private int gameID;
    private int userID;
    private int adapterPosition;
    private ImageView ivGameBlured;
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
        ivGameBlured = (ImageView)findViewById(R.id.iv_game_details_picture_blured);
        ivGame = (ImageView)findViewById(R.id.iv_game_details_picture);
        tvTimePlayed = (TextView)findViewById(R.id.tv_game_details_time_played);
        tvGamePrice = (TextView)findViewById(R.id.tv_game_details_game_price);
        tvGamePricePerHour = (TextView)findViewById(R.id.tv_game_details_game_price_per_hour);
        tvBundleName = (TextView) findViewById(R.id.tv_game_details_bundle_name);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        // Preparing the database.
        userDbHelper = UserDbHelper.getInstance(this);
        db = userDbHelper.getWritableDatabase();

        // We retrieve the gameID to display the game details.
        Intent intent = getIntent();
        gameID = intent.getIntExtra("gameID",0);
        userID = intent.getIntExtra("userID", 0);
        recyclerName = intent.getStringExtra("recyclerName");
        adapterPosition = intent.getIntExtra("adapterPosition", 0);
        displayGameInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_details_menu, menu);
        return true;
    }

    private void displayGameInformation(){

        // First, we have to get all the information about the game.
        Cursor result = userDbHelper.getGameBy_ID(db, String.valueOf(gameID));
        if(result.getCount() != 0){
            result.moveToFirst();
            String gameName = result.getString(
                    result.getColumnIndex(UserContract.GameEntry.COLUMN_GAME_NAME));
            getSupportActionBar().setTitle(gameName);
            String urlGame = result.getString(result.getColumnIndex(UserContract.GameEntry.COLUMN_GAME_LOGO));
            if(urlGame.length() != 0) {
                Picasso.with(this).load(urlGame).into(ivGame);
                Picasso.with(this).load(urlGame).transform(new BlurTransformation(this, 5)).into(ivGameBlured);

            }
        }else{
            // The game doesn't exist in DB. We log an error message.
            Log.e(TAG, "displayGameInformation: There is no game in database with the _ID " + gameID);
        }

        // Then, we have to get the other information about the game usage.
        result = userDbHelper.getOwnedGame(db, String.valueOf(userID), String.valueOf(gameID));
        if(result.getCount() != 0){
            String timePlayed = result.getString(result.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER));
            String gamePrice = result.getString(result.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE));
            TextView tvSpent = (TextView) findViewById(R.id.tv_game_details_spent);

            String priceText;
            switch (gamePrice) {
                case "":
                case "-1":
                    priceText = "?" + sharedPreferences.getString("lp_currency", "$");
                    tvGamePrice.setText(priceText);
                    tvGamePricePerHour.setText("?" + sharedPreferences.getString("lp_currency", "$") + "/h");
                    tvSpent.setVisibility(View.VISIBLE);
                    break;
                case "0":
                    tvGamePrice.setText(getResources().getString(R.string.free));
                    tvGamePricePerHour.setText(getResources().getString(R.string.free));
                    tvSpent.setVisibility(View.INVISIBLE);
                    break;
                default:
                    priceText = gamePrice + sharedPreferences.getString("lp_currency", "$");
                    tvGamePrice.setText(priceText);
                    Double hours = Double.valueOf(timePlayed) / 60;
                    Double pricePerHour = Double.valueOf(gamePrice) / hours;
                    DecimalFormat df = new DecimalFormat("#.##");
                    tvGamePricePerHour.setText(String.valueOf(df.format(pricePerHour)) + sharedPreferences.getString("lp_currency", "$") + "/h");
                    tvSpent.setVisibility(View.VISIBLE);
                    break;
            }
            tvTimePlayed.setText(SteamAPICalls.convertTimePlayed(Integer.valueOf(timePlayed), true));

            // We have to check is the game is included in a bundle
            String bundleID = result.getString(result.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID));

            if (bundleID != null) {
                // it's the case, we have to retrieve the bundle information
                result = userDbHelper.getBundleByID(db, bundleID);
                if (result.getCount() != 0) {
                    String bundleName = result.getString(result.getColumnIndex(UserContract.BundleEntry.COLUMN_BUNDLE_NAME));
                    tvBundleName.setText(bundleName);
                    findViewById(R.id.ll_game_details_bundle).setVisibility(View.VISIBLE);
                } else {
                    Log.e(TAG, "displayGameInformation: The bundle " + bundleID + "doesn't exist in database.");
                }


            }
        }else{
            // The game doesn't exist in DB. We log an error message.
            Log.e(TAG, "displayGameInformation: The user "+ userID + "doesn't own the game with the ID " + gameID);
        }
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_game_details_edit:
                Intent intentEdit = new Intent(this, EditGameActivity.class);
                intentEdit.putExtra("gameID", gameID);
                intentEdit.putExtra("userID", userID);

                // If the user is runing on SDK 16 or newer, display a transition.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(this,
                            R.transition.right_to_left_incoming, R.transition.right_to_left_outgoing)
                            .toBundle();
                    this.startActivityForResult(intentEdit, 1, bndlAnimation);
                } else {
                    this.startActivityForResult(intentEdit, 1);
                }
                return true;
            default:
                this.onBackPressed();
                return true;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                displayGameInformation();
                String price = tvGamePrice.getText().toString();
                price = price.substring(0, price.length() - 1);
                newPrice = price;

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
        switch (newPrice) {
            case "null":
                setResult(Activity.RESULT_CANCELED, returnIntent);
                break;
            case "Fre":
                returnIntent.putExtra("newPrice", "0");
                returnIntent.putExtra("recyclerName", recyclerName);
                setResult(Activity.RESULT_OK, returnIntent);
                break;
            default:
                returnIntent.putExtra("newPrice", newPrice);
                returnIntent.putExtra("recyclerName", recyclerName);
                setResult(Activity.RESULT_OK, returnIntent);
                break;
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
