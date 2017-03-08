package com.joffreylagut.mysteamgames.mysteamgames;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Joffrey on 08/02/2017.
 */

class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GamesListViewHolder> {

    private List<GameListItem> gameList;
    private Context context;

    GameListAdapter(List<GameListItem> gameList, Context context) {
        this.gameList = gameList;
        this.context = context;
    }

    @Override
    public GamesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_row, parent, false);
        return new GamesListViewHolder(view);
    }

    List<GameListItem> getGameList() {
        return this.gameList;
    }

    void setGameList(List<GameListItem> gameList) {
        this.gameList = gameList;
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

    interface ListItemClickListener {
        void ListItemClicked(String clickedItemName);
    }

    class GamesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name;
        TextView timePlayed;
        TextView gamePrice;


        GamesListViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.iv_game_picture);
            name = (TextView) itemView.findViewById(R.id.tv_game_name);
            timePlayed = (TextView) itemView.findViewById(R.id.tv_game_time_played);
            gamePrice = (TextView)itemView.findViewById(R.id.tv_game_price_per_hour);

            itemView.setOnClickListener(this);
        }

        void bind(GameListItem gameItem) {
            // We display the game image
            if(gameItem.getGameImage() != null){
            Picasso.with(image.getContext()).load(gameItem.getGameImage().toString()).into(image);
            }
            // We display the game name
            name.setText(gameItem.getGameName());
            // We display the time played
            String stringTimePlayed = SteamAPICalls.convertTimePlayed(gameItem.getGameTimePlayed());
            if (stringTimePlayed.compareTo("0mn") == 0) {
                timePlayed.setText(context.getResources().getString(R.string.never_played));
            } else {
                timePlayed.setText(stringTimePlayed);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            double nbHours = gameItem.getGameTimePlayed() / 60;
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
                    String pricePerHourFormated = String.valueOf(df.format(pricePerHour));
                    if (pricePerHourFormated.compareTo("∞") == 0) {
                        gamePrice.setText("— " + sharedPreferences.getString("lp_currency", "$") + "/h");
                    } else {
                        gamePrice.setText(String.valueOf(df.format(pricePerHour)) + " " + sharedPreferences.getString("lp_currency", "$") + "/h");
                    }
                    gamePrice.setVisibility(View.VISIBLE);
                    break;
            }

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Intent intent = new Intent(v.getContext(), GameDetailsActivity.class);
            intent.putExtra("gameID", gameList.get(clickedPosition).getGameID());
            intent.putExtra("userID", gameList.get(clickedPosition).getUserID());
            intent.putExtra("adapterPosition", clickedPosition);
            ((Activity) context).startActivityForResult(intent, 1);
        }
    }
}


