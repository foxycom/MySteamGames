package com.joffreylagut.mysteamgames.mysteamgames;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.Game;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameBundle;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EditGameActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    // Variable that allow us to retrieve information from OwnedGames table
    private int gameID;
    private int userID;
    // Variable used to query the db
    private SQLiteDatabase db;
    private UserDbHelper userDbHelper;
    // View variables declaration
    private EditText etGameName;
    private EditText etTimePlayed;
    private Spinner spTimeUnit;
    private RadioButton rdBoughtAlone;
    private RadioButton rdBoughtBundle;
    private EditText etGamePrice;
    private Spinner spBundleName;
    private EditText etBundlePrice;
    private List<String> arraySpinnerBundle;
    private ArrayAdapter<String> adapterBundle;
    private List<String[]> listUserBundleWithPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_edit_game_title));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // We get the values from GameDetailsActivity
        Intent previousIntent = getIntent();
        gameID = previousIntent.getIntExtra("gameID", 0);
        userID = previousIntent.getIntExtra("userID", 0);

        // Preparing the database.
        userDbHelper = UserDbHelper.getInstance(this);
        db = userDbHelper.getWritableDatabase();

        // We are linking our views with the layout
        etGameName = (EditText) findViewById(R.id.et_game_name);
        spTimeUnit = (Spinner) findViewById(R.id.sp_time_unit);
        etTimePlayed = (EditText) findViewById(R.id.et_time_played);
        rdBoughtAlone = (RadioButton) findViewById(R.id.rd_bought_alone);
        rdBoughtBundle = (RadioButton) findViewById(R.id.rd_bought_bundle);
        etGamePrice = (EditText) findViewById(R.id.et_game_price);
        spBundleName = (Spinner) findViewById(R.id.sp_bundle_name);
        etBundlePrice = (EditText) findViewById(R.id.et_bundle_price);
        RadioGroup rgBoughtType = (RadioGroup) findViewById(R.id.rg_bought_type);

        // We declare on event onCheckedChangeListener on the RadioGroup to add/remove some views
        rgBoughtType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == rdBoughtAlone.getId()) {
                    showViewsFromRadioButtonState(false);
                } else if (checkedId == rdBoughtBundle.getId()) {
                    showViewsFromRadioButtonState(true);
                }

            }

        });

        // We attach a watcher to the edit text that are here to display currency
        etGamePrice.addTextChangedListener(new MoneyTextWatcher(etGamePrice));
        etBundlePrice.addTextChangedListener(new MoneyTextWatcher(etBundlePrice));

        // We can now display the information inside of the views
        displayInformation();
    }

    private void showViewsFromRadioButtonState(boolean viewsBundle) {
        if (!viewsBundle) {
            hideView(findViewById(R.id.layout_bundle_name));
            hideView(findViewById(R.id.ti_layout_bundle_price));
            showView(findViewById(R.id.ti_layout_game_price));


        } else {
            showView(findViewById(R.id.layout_bundle_name));
            showView(findViewById(R.id.ti_layout_bundle_price));
            hideView(findViewById(R.id.ti_layout_game_price));
        }
    }

    /**
     * This methode change the height of a view to 0 dp to hide the view on the screen.
     *
     * @param viewToHide View that we want to hide.
     */
    private void hideView(View viewToHide) {
        viewToHide.getLayoutParams().height = 0;
        viewToHide.requestLayout();
    }

    /**
     * This methode change the height of a view to WARP_CONTENT to display the view on the screen.
     *
     * @param viewToShow View that we want to show.
     */
    private void showView(View viewToShow) {
        viewToShow.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        viewToShow.requestLayout();
    }

    /**
     * Method called when the user click on Save
     *
     * @param v View that have called the method.
     */
    public void saveGame(View v) {
        // TODO Fix the error bellow

        // In first, we need to retrieve all the information
        Game game = new Game(etGameName.getText().toString());
        game.setGameID(gameID);

        OwnedGame ownedGame = new OwnedGame(userID, game);
        ownedGame.setUserId(userID);
        double timePlayed = Double.parseDouble(etTimePlayed.getText().toString());
        String timeUnit = spTimeUnit.getSelectedItem().toString();
        // If the user have set the time in hour, we need to convert it in minutes
        if (timeUnit.equals("h")){
            ownedGame.setTimePlayedForever((int) (timePlayed * 60));
        }else{
            ownedGame.setTimePlayedForever((int) timePlayed);
        }
        if (rdBoughtAlone.isChecked()) {
            // The game have been bought alone
            // We have to clean the currency
            String cleanString = removeCurrency(etGamePrice.getText().toString());
            if (cleanString.length() == 0){
                cleanString = "0";
            }
            // We get the price
            Double gamePrice = Double.valueOf(cleanString);
            ownedGame.setGamePrice(gamePrice);
            // We have all of the information needed to update the game in DB
            userDbHelper.updateGameById(db, ownedGame.getGame());
            userDbHelper.updateOwnedGame(db, ownedGame);
        } else {
            // The game is in a bundle
            // We have to clean the currency
            String cleanString = removeCurrency(etBundlePrice.getText().toString());
            if (cleanString.length() == 0) cleanString = "0";
            // We create a GameBundle object
            GameBundle gameBundle = new GameBundle();
            gameBundle.setName(spBundleName.getSelectedItem().toString());
            gameBundle.setPrice(Double.valueOf(cleanString));
            if (!gameBundle.getName().equals(getResources().getString(R.string.spinner_choose_bundle_item)) &&
                    !gameBundle.getName().equals(getResources().getString(R.string.spinner_new_bundle_item))) {
                // First, we check if there is already a bundle with this name
                GameBundle gameBundleFromDb = userDbHelper.getGameBundleByName(db, gameBundle.getName(), userID);
                if (gameBundleFromDb.getId() != 0) {
                    // The user already own a bundle with this name so we update the bundle.
                    gameBundle.setId(gameBundleFromDb.getId());
                    userDbHelper.updateGameBundle(db, gameBundle);
                } else {
                    // We create a new bundle
                    userDbHelper.addNewGameBundle(db, gameBundle);
                }
                // To finish, we update the ownedgame table
                ownedGame.setGameBundle(gameBundle);
                userDbHelper.updateOwnedGame(db, ownedGame);
                userDbHelper.updateOwnedGamePriceFromBundle(db, gameBundle.getId());

            } else {
                showView(findViewById(R.id.tv_error_bundle_name));
                return;
            }
        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        // We want to display an animation to go back on the previous activity
        overridePendingTransition(R.transition.left_to_right_incoming,
                R.transition.left_to_right_outgoing);

    }

    /**
     * The function remove the currency from the String in parameter.
     * For example, "€25.2" will be returned "25.2"
     *
     * @param stringToClean String with the currency and the amount.
     * @return String without currency
     */
    private String removeCurrency(String stringToClean) {
        switch (sharedPreferences.getString("lp_currency", "$")) {
            case "€":
                return stringToClean.replaceAll("[€]", "");
            case "£":
                return stringToClean.replaceAll("[£]", "");
            default:
                return stringToClean.replaceAll("[$]", "");
        }
    }

    /**
     * Method that will query the database and insert the values into the views.
     */
    private void displayInformation() {
        // TODO Fix the error bellow

        // We are putting the values inside the time unit spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimeUnit.setAdapter(adapter);

        // We get all the information in OwnedGame table
        OwnedGame ownedGame = userDbHelper.getOwnedGame(db, userID, gameID);

        // We are putting the values inside the bundle spinner
        arraySpinnerBundle = new ArrayList<>();
        listUserBundleWithPrice = new ArrayList<>();
        // Basic values
        arraySpinnerBundle.add(getResources().getString(R.string.spinner_choose_bundle_item));
        arraySpinnerBundle.add(getResources().getString(R.string.spinner_new_bundle_item));

        // DB values
        List<GameBundle> userGameBundles = userDbHelper.getUserGameBundles(db, userID);
        if (userGameBundles.size() != 0) {
            for(GameBundle gameBundle : userGameBundles){
                String[] result = {gameBundle.getName(), String.valueOf(gameBundle.getPrice())};
                listUserBundleWithPrice.add(result);
                arraySpinnerBundle.add(gameBundle.getName());
            }
        }

        // Setup the adapter
        adapterBundle = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinnerBundle);
        adapterBundle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBundleName.setAdapter(adapterBundle);
        // Select the game bundle if exist
        if (ownedGame.getGameBundle().getName() != null) {
            int bundleNamePosition = getIndex(spBundleName, ownedGame.getGameBundle().getName());
            if (bundleNamePosition != -1) spBundleName.setSelection(bundleNamePosition);
        }

        spBundleName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (parentView.getAdapter().getItem(position) ==
                        selectedItemView.getResources().getString(R.string.spinner_new_bundle_item)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(selectedItemView.getContext());
                    builder.setTitle(selectedItemView.getResources().getString(R.string.alert_new_bundle_title));

                    // Set up the input
                    final EditText input = new EditText(selectedItemView.getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            arraySpinnerBundle.add(input.getText().toString());
                            adapterBundle = new ArrayAdapter<>(spBundleName.getContext(),
                                    android.R.layout.simple_spinner_item, arraySpinnerBundle);
                            adapterBundle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spBundleName.setAdapter(adapterBundle);

                            int spinnerPosition = adapterBundle.getPosition(input.getText().toString());
                            spBundleName.setSelection(spinnerPosition);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else {
                    // We have to check if the GameBundle already exist in db to show its price
                    for (String[] currentBundle : listUserBundleWithPrice) {
                        if (currentBundle[0].equals(parentView.getAdapter().getItem(position).toString())) {
                            // We add the price in the view
                            etBundlePrice.setText(currentBundle[1]);
                            break;
                        } else {
                            etBundlePrice.setText("");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nothing appened
            }
        });

        // Steam games information are auto-updated so we deactivate the fields
        if (ownedGame.getGame().getMarketplace() != null && ownedGame.getGame().getMarketplace().equals("Steam")) {
            etTimePlayed.setEnabled(false);
            etGameName.setEnabled(false);
            // Since we are storing time in minutes in DB, we choose automatically mn in the spinner
            int spinnerPosition = adapter.getPosition("mn");
            spTimeUnit.setSelection(spinnerPosition);
            spTimeUnit.setEnabled(false);
        }


        // We can now insert the values in views
        etGameName.setText(ownedGame.getGame().getGameName());
        etTimePlayed.setText(String.valueOf(ownedGame.getTimePlayedForever()));
        if (ownedGame.getGameBundle().getId() == 0) {
            rdBoughtAlone.setChecked(true);
            rdBoughtBundle.setChecked(false);
            showViewsFromRadioButtonState(false);
            if (ownedGame.getGamePrice() != -1.00){
                etGamePrice.setText(String.valueOf(ownedGame.getGamePrice()));
            }
        } else {
            rdBoughtAlone.setChecked(false);
            rdBoughtBundle.setChecked(true);
            etBundlePrice.setText(String.valueOf(ownedGame.getGameBundle().getPrice()));
            showViewsFromRadioButtonState(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit_game_save) {
            saveGame(findViewById(R.id.btn_save_game_edit));
        } else {
            this.onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // We want to display an animation to go back on the previous activity
        overridePendingTransition(R.transition.left_to_right_incoming,
                R.transition.left_to_right_outgoing);
    }

    private int getIndex(Spinner spinner, String myString) {

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                return i;
            }
        }
        return -1;
    }

    private class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            editText.removeTextChangedListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(editText.getContext());
            String cleanString;
            switch (sharedPreferences.getString("lp_currency", "$")) {
                case "€":
                    cleanString = s.replaceAll("[€]", "");
                    break;
                case "£":
                    cleanString = s.replaceAll("[£]", "");
                    break;
                default:
                    cleanString = s.replaceAll("[$]", "");
                    break;
            }
            String newText = sharedPreferences.getString("lp_currency", "$") + cleanString;
            editText.setText(newText);
            editText.setSelection(cleanString.length() + 1);
            editText.addTextChangedListener(this);
        }
    }
}
