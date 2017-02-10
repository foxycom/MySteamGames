package com.joffreylagut.mysteamgames.mysteamgames;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

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
        Log.d(TAG, "onBindViewHolderposition: " + position);
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


        public GamesListViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.iv_game_picture);
            name = (TextView) itemView.findViewById(R.id.tv_game_name);
            timePlayed = (TextView) itemView.findViewById(R.id.tv_game_time_played);

            itemView.setOnClickListener(this);
        }

        public void bind(GameListItem gameItem) {
            Picasso.with(image.getContext()).load(gameItem.getGameImage().toString()).into(image);
            name.setText(gameItem.getGameName());
            //name.setText(gameItem.getGameName());
            String convertedTime = Long.toString(TimeUnit.HOURS.convert(gameItem.getGameTimePlayed(), TimeUnit.MINUTES));
            timePlayed.setText(convertedTime + " h");
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickListener.ListItemClicked(gameList.get(clickedPosition).getGameName());
        }
    }
}


