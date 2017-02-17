package com.joffreylagut.mysteamgames.mysteamgames;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joffreylagut.mysteamgames.mysteamgames.data.UserContract;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String TAG = "GameDetailsActivity";
    private EditText etPrice;
    private ImageView ivGameBlured;
    private ImageView ivGame;
    private TextView tvTimePlayed;
    private TextView tvGamePrice;
    private TextView tvGamePricePerHour;
    private SQLiteDatabase db;
    UserDbHelper userDbHelper;

    int gameID;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        // First, we are linking our views with the layout
        etPrice = (EditText)findViewById(R.id.et_price);
        ivGameBlured = (ImageView)findViewById(R.id.iv_game_details_picture_blured);
        ivGame = (ImageView)findViewById(R.id.iv_game_details_picture);
        tvTimePlayed = (TextView)findViewById(R.id.tv_game_details_time_played);
        tvGamePrice = (TextView)findViewById(R.id.tv_game_details_game_price);
        tvGamePricePerHour = (TextView)findViewById(R.id.tv_game_details_game_price_per_hour);

        // We are declaring a new UserDbHelper to access to the db.
        userDbHelper = new UserDbHelper(this);
        db = userDbHelper.getWritableDatabase();

        // We retrieve the gameID to display the game details.
        Intent intent = getIntent();
        gameID = intent.getIntExtra("gameID",0);
        userID = intent.getIntExtra("userID", 0);
        displayGameInformation();
    }

    private void displayGameInformation(){

        // First, we have to get all the information about the game.
        Cursor result = userDbHelper.getGameBy_ID(db, String.valueOf(gameID));
        if(result.getCount() != 0){
            result.moveToFirst();
            String urlGame = result.getString(result.getColumnIndex(UserContract.GameEntry.COLUMN_GAME_LOGO));
            if(urlGame.length() != 0) {
                Picasso.with(this).load(urlGame).into(ivGame);
                Picasso.with(this).load(urlGame).into(ivGameBlured);
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
            if(!gamePrice.equals("") && !gamePrice.equals("0.00")) {

                Double hours = Double.valueOf(timePlayed) / 60;
                Double pricePerHour = Double.valueOf(gamePrice) / hours;
                DecimalFormat df = new DecimalFormat("#.##");
                tvGamePricePerHour.setText(String.valueOf(df.format(pricePerHour)));
            }else{
                tvGamePricePerHour.setText("?");
            }

            tvTimePlayed.setText(timePlayed);
            tvGamePrice.setText(gamePrice);

        }else{
            // The game doesn't exist in DB. We log an error message.
            Log.e(TAG, "displayGameInformation: The user "+ userID + "doesn't own the game with the ID " + gameID);
        }
    }
    public void setGamePrice(View v){

        Toast.makeText(this, "New price: " + etPrice.getText(), Toast.LENGTH_SHORT).show();
        // We have to set the price in database
        userDbHelper.updateOwnedGamePrice(db, String.valueOf(userID), String.valueOf(gameID), etPrice.getText().toString());
        displayGameInformation();
    }
}
