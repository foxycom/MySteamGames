package com.joffreylagut.mysteamgames.mysteamgames;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GameListAdapter.ListItemClickListener {

    RecyclerView recyclerView;
    Toast message = null;
    private List<GameListItem> gameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertGameData();
        recyclerView = (RecyclerView) findViewById(R.id.rv_games);

        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GameListAdapter gameListAdapter = new GameListAdapter(gameList, this);
        recyclerView.setAdapter(gameListAdapter);
    }

    private void insertGameData() {
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
    }

    @Override
    public void ListItemClicked(String clickedItemName) {
        if (message != null) {
            message.cancel();
        }
        message = Toast.makeText(this, "Click on " + clickedItemName, Toast.LENGTH_LONG);
        message.show();
    }
}
