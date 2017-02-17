package com.joffreylagut.mysteamgames.mysteamgames;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.data.AppPreferences;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Joffrey on 08/02/2017.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GamesListViewHolder> {

    final private ListItemClickListener mClickListener;
    private List<GameListItem> gameList;

    public GameListAdapter(List<GameListItem> gameList, ListItemClickListener clickListener) {
        this.gameList = gameList;
        this.mClickListener = clickListener;
    }

    @Override
    public GamesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_row, parent, false);
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

    public interface ListItemClickListener {
        void ListItemClicked(String clickedItemName);
    }

    class GamesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name;
        TextView timePlayed;
        TextView gamePrice;


        public GamesListViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.iv_game_picture);
            name = (TextView) itemView.findViewById(R.id.tv_game_name);
            timePlayed = (TextView) itemView.findViewById(R.id.tv_game_time_played);
            gamePrice = (TextView)itemView.findViewById(R.id.tv_game_price_per_hour);

            itemView.setOnClickListener(this);
        }

        public void bind(GameListItem gameItem) {
            if(gameItem.getGameImage() != null){
            Picasso.with(image.getContext()).load(gameItem.getGameImage().toString()).into(image);
            }
            name.setText(gameItem.getGameName());
            timePlayed.setText(SteamAPICalls.showMinutesInHoursOrMinutes(gameItem.getGameTimePlayed()));
            if(gameItem.getGamePrice() != 0){
                double nbHours = Double.valueOf(gameItem.getGameTimePlayed())/60;
                double pricePerHour = gameItem.getGamePrice()/nbHours;
                DecimalFormat df = new DecimalFormat("#.##");
                gamePrice.setText(String.valueOf(df.format(pricePerHour))+ " " + AppPreferences.getCurrency() +"/h");
                gamePrice.setVisibility(View.VISIBLE);
            }else{
                gamePrice.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Intent intent = new Intent(v.getContext(), GameDetailsActivity.class);
            intent.putExtra("gameID", gameList.get(clickedPosition).getGameID());
            intent.putExtra("userID", gameList.get(clickedPosition).getUserID());
            v.getContext().startActivity(intent);
        }
    }
}


