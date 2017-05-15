package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.UnitsConverterHelper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * GameListAdapter.java
 * Purpose: Adapter used to specify the content of the RecyclerView items showed on the Games fragment.
 *
 * @author Joffrey LAGUT
 * @version 1.2 2017-05-15
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GamesListViewHolder> {

    private List<GameListItem> gameList;
    private Context context;
    private View.OnClickListener listener;

    public GameListAdapter(List<GameListItem> gameList, Context context, View.OnClickListener listener) {
        this.gameList = gameList;
        this.context = context;
        this.listener = listener;
    }

    public List<GameListItem> getGameList() {
        return this.gameList;
    }

    public void setGameList(List<GameListItem> gameList) {
        this.gameList = gameList;
    }

    @Override
    public GamesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game_list, parent, false);
        view.setOnClickListener(listener);
        return new GamesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GamesListViewHolder holder, int position) {
        GameListItem game = gameList.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    class GamesListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_game_picture)
        ImageView image;
        @BindView(R.id.tv_game_name)
        TextView name;
        @BindView(R.id.tv_game_time_played)
        TextView timePlayed;
        @BindView(R.id.tv_game_price_per_hour)
        TextView gamePrice;

        GamesListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(GameListItem gameItem) {
            // We display the game image
            if(gameItem.getGameImage() != null){
            Picasso.with(image.getContext()).load(gameItem.getGameImage().toString()).into(image);
            }
            // We display the game name
            name.setText(gameItem.getGameName());
            // We display the time played
            String stringTimePlayed = UnitsConverterHelper.displayMinutesInHours(gameItem.getGameTimePlayed());
            if (stringTimePlayed.compareTo("0mn") == 0) {
                timePlayed.setText(context.getResources().getString(R.string.never_played));
            } else {
                timePlayed.setText(stringTimePlayed);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            double nbHours = (double) gameItem.getGameTimePlayed() / 60;
            String gamePriceFinal = String.valueOf(gameItem.getGamePrice());
            switch (gamePriceFinal) {
                case "-1.0":
                    gamePrice.setText("");
                    gamePrice.setVisibility(View.INVISIBLE);
                    break;
                case "0.0":
                    gamePrice.setText(context.getResources().getString(R.string.free));
                    gamePrice.setVisibility(View.VISIBLE);
                    break;
                default:
                    double pricePerHour = gameItem.getGamePrice() / nbHours;
                    DecimalFormat df = new DecimalFormat("#.##");
                    if (gameItem.getGameTimePlayed() == 0) {
                        gamePrice.setText("— " + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h");
                    } else {
                        gamePrice.setText(String.valueOf(df.format(pricePerHour)) + " " + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h");
                    }
                    gamePrice.setVisibility(View.VISIBLE);
                    break;
            }

        }
    }
}


