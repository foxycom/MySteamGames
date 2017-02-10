package com.joffreylagut.mysteamgames.mysteamgames;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.User;
import com.joffreylagut.mysteamgames.mysteamgames.data.AppPreferences;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GameListAdapter.ListItemClickListener {

    // Declaration of the global values of this activity.
    private User currentUser = new User();
    private Toast message = null;

    // Declaration of all the view that we will interact with.
    private ImageView ivProfile;
    private ProgressBar pbLoading;
    private TextView tvAccountName;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First, we are linking our views with the layout
        pbLoading = (ProgressBar)findViewById(R.id.pb_loading);
        tvAccountName = (TextView)findViewById(R.id.tv_account_name);
        recyclerView = (RecyclerView) findViewById(R.id.rv_games);
        ivProfile = (ImageView)findViewById(R.id.iv_profile);

        // Then, we are putting values inside our current user
        currentUser.setSteamID(AppPreferences.getUserSteamID());
        currentUser.setGameList(insertGameData());

        // We are generating the URL and then ask the Steam API
        SteamAPICalls.getURLPlayerProfileInformation(AppPreferences.getUserSteamID());
        RetrieveProfileInformation myProfileInformation = new RetrieveProfileInformation();
        myProfileInformation.execute(SteamAPICalls.getURLPlayerProfileInformation(currentUser.getSteamID()));

    }

    /**
     * Insert data sample in the gamelist.
     */
    private List<GameListItem> insertGameData() {
        List<GameListItem> gameList = new ArrayList<>();
        gameList.add(new GameListItem(R.drawable.clash, "Clash of clans", "16,5h"));
        gameList.add(new GameListItem(R.drawable.clash, "Clash royale", "22h"));
        gameList.add(new GameListItem(R.drawable.clash, "Doom", "9,8h"));
        gameList.add(new GameListItem(R.drawable.clash, "Age of Empire", "25h"));
        gameList.add(new GameListItem(R.drawable.clash, "Civilization VI", "52,6h"));
        gameList.add(new GameListItem(R.drawable.clash, "Crash Bandicoot", "2h"));
        gameList.add(new GameListItem(R.drawable.clash, "Warhammer", "1h"));
        gameList.add(new GameListItem(R.drawable.clash, "Terraria", "132h"));
        gameList.add(new GameListItem(R.drawable.clash, "Minecraft", "1235h"));
        gameList.add(new GameListItem(R.drawable.clash, "Ark", "63h"));
        gameList.add(new GameListItem(R.drawable.clash, "Red Faction", "5h"));
        gameList.add(new GameListItem(R.drawable.clash, "Spore", "0,5h"));
        gameList.add(new GameListItem(R.drawable.clash, "Dofus", "0h"));
        gameList.add(new GameListItem(R.drawable.clash, "Conan", "16,5h"));
        return gameList;
    }

    @Override
    public void ListItemClicked(String clickedItemName) {
        if (message != null) {
            message.cancel();
        }
        message = Toast.makeText(this, "Click on " + clickedItemName, Toast.LENGTH_LONG);
        message.show();
    }

    public void ParseJSONProfile(String JSONProfil){
        try {
            // We are directly going to the object containing our player information.
            JSONObject player = new JSONObject(JSONProfil).getJSONObject("response").getJSONArray("players").getJSONObject(0);

            // We can now insert the information on our currentUser.
            currentUser.setAccountName(player.getString("personaname"));

            try {
                currentUser.setAccountPicture(new URL(player.getString("avatarfull")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayInformation(){
        // Now we can display the user's information.
        tvAccountName.setText(currentUser.getAccountName());
        //Picasso.with(this).load(String.valueOf(currentUser.getAccountPicture())).into(ivProfile);
        String urlImageToLoad = currentUser.getAccountPicture().toString();
        Picasso.with(this).load(urlImageToLoad).into(ivProfile);


        // We insert the data in our RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GameListAdapter gameListAdapter = new GameListAdapter(currentUser.getGameList(), this);
        recyclerView.setAdapter(gameListAdapter);

    }
    class RetrieveProfileInformation extends AsyncTask<URL, Void, String> {

        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(URL... urls) {
            // Do some validation here
            URL urlToCall = urls[0];
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) urlToCall.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            pbLoading.setVisibility(View.GONE);
            Log.i("INFO", response);
            ParseJSONProfile(response);
            displayInformation();
        }
    }
}
