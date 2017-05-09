package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.R;

import java.util.List;

/**
 * GameTongueAdapter.java
 * Purpose: Adapter used to specify the content of the ListView showed on the Home fragment.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class GameTongueAdapter extends ArrayAdapter<GameTongueAdapter.GameTongue> {


    public GameTongueAdapter(Context context, List<GameTongue> gameTongues) {
        super(context, 0, gameTongues);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // We check if we have to create a new view or if we can reuse it.
        if (convertView == null) {
            // We create the view.
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_game_tongue, parent, false);
        }

        // We try to get the content of the view via getTag.
        GameTongueViewHolder gameTongueViewHolder = (GameTongueViewHolder) convertView.getTag();
        if (gameTongueViewHolder == null) {
            // No tag are set in the view, we have to set it now.
            gameTongueViewHolder = new GameTongueViewHolder();
            gameTongueViewHolder.gameTitle = (TextView) convertView.findViewById(R.id.game_title);
            gameTongueViewHolder.gameProgression = (TextView) convertView.findViewById(R.id.game_progression_hours);
            gameTongueViewHolder.gameCaption = (TextView) convertView.findViewById(R.id.game_progression_percents);
            convertView.setTag(gameTongueViewHolder);
        }

        // We get the data to display
        GameTongue gameTongueToDisplay = getItem(position);

        // We set the data in our ViewHolder
        if (gameTongueToDisplay != null) {
            gameTongueViewHolder.gameTitle.setText(gameTongueToDisplay.gameTitle);
            gameTongueViewHolder.gameProgression.setText(gameTongueToDisplay.gameProgression);
            gameTongueViewHolder.gameCaption.setText(gameTongueToDisplay.gameCaption);
        }

        // And return the view
        return convertView;

    }

    /**
     * Class that is the blueprint of one view of our ListView.
     */
    private static class GameTongueViewHolder {
        TextView gameTitle;
        TextView gameProgression;
        TextView gameCaption;
    }

    /**
     * Class that define the structure of the data that we wants to use with our adapter.
     */
    public static class GameTongue {
        int gameId;
        String gameTitle;
        String gameProgression;
        String gameCaption;

        public GameTongue(int gameId, String gameTitle, String gameProgression, String gameCaption) {
            this.gameId = gameId;
            this.gameTitle = gameTitle;
            this.gameProgression = gameProgression;
            this.gameCaption = gameCaption;
        }

        public int getGameId() {
            return gameId;
        }

        public void setGameId(int gameId) {
            this.gameId = gameId;
        }

        public String getGameTitle() {
            return gameTitle;
        }

        public void setGameTitle(String gameTitle) {
            this.gameTitle = gameTitle;
        }

        public String getGameProgression() {
            return gameProgression;
        }

        public void setGameProgression(String gameProgression) {
            this.gameProgression = gameProgression;
        }

        public String getGameCaption() {
            return gameCaption;
        }

        public void setGameCaption(String gameCaption) {
            this.gameCaption = gameCaption;
        }
    }
}
