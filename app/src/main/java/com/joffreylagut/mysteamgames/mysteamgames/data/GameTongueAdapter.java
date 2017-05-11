package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
            gameTongueViewHolder.gameRank = (ImageView) convertView.findViewById(R.id.game_rank);
            gameTongueViewHolder.background = convertView.findViewById(R.id.game_layout_background);
            convertView.setTag(gameTongueViewHolder);
        }

        // We get the data to display
        GameTongue gameTongueToDisplay = getItem(position);

        // We set the data in our ViewHolder
        if (gameTongueToDisplay != null) {
            gameTongueViewHolder.gameTitle.setText(gameTongueToDisplay.gameTitle);
            gameTongueViewHolder.gameProgression.setText(gameTongueToDisplay.gameProgression);
            gameTongueViewHolder.gameCaption.setText(gameTongueToDisplay.gameCaption);
            switch (gameTongueToDisplay.gameRank) {
                case 1:
                    gameTongueViewHolder.gameRank.setVisibility(View.VISIBLE);
                    gameTongueViewHolder.gameRank.setImageResource(R.drawable.ic_trophy_gold);
                    gameTongueViewHolder.gameRank.setContentDescription(getContext().getResources().getString(R.string.gold_trophy_content_description));
                    break;
                case 2:
                    gameTongueViewHolder.gameRank.setVisibility(View.VISIBLE);
                    gameTongueViewHolder.gameRank.setImageResource(R.drawable.ic_trophy_silver);
                    gameTongueViewHolder.gameRank.setContentDescription(getContext().getResources().getString(R.string.silver_trophy_content_description));
                    break;
                case 3:
                    gameTongueViewHolder.gameRank.setVisibility(View.VISIBLE);
                    gameTongueViewHolder.gameRank.setImageResource(R.drawable.ic_trophy_bronze);
                    gameTongueViewHolder.gameRank.setContentDescription(getContext().getResources().getString(R.string.bronze_trophy_content_description));
                    break;
            }

            // Now we change the width of the background view depending of the completion %
            //ViewGroup.LayoutParams backgroundViewLayoutParams = gameTongueViewHolder.background.getLayoutParams();
            Guideline guideline = (Guideline) convertView.findViewById(R.id.guideline);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
            layoutParams.guidePercent = (float) gameTongueToDisplay.percentageBackground / 100;
            guideline.setLayoutParams(layoutParams);
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
        ImageView gameRank;
        View background;
    }

    /**
     * Class that define the structure of the data that we wants to use with our adapter.
     */
    public static class GameTongue {
        int gameId;
        String gameTitle;
        String gameProgression;
        String gameCaption;
        int gameRank;
        int percentageBackground;

        public GameTongue(int gameId, String gameTitle, String gameProgression, String gameCaption, int percentageBackground) {
            this.gameId = gameId;
            this.gameTitle = gameTitle;
            this.gameProgression = gameProgression;
            this.gameCaption = gameCaption;
            this.percentageBackground = percentageBackground;
        }

        public int getGameId() {
            return gameId;
        }

        public void setGameRank(int gameRank) {
            this.gameRank = gameRank;
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
