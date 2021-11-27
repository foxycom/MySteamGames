package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.models.Goal;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.UnitsConverterHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * GameTongueAdapter.java
 * Purpose: Adapter used to specify the content of the ListView showed on the Home fragment.
 *
 * @author Joffrey LAGUT
 * @version 1.2 2017-05-15
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
            gameTongueViewHolder.gameProgression = (TextView) convertView.findViewById(R.id.game_progression);
            gameTongueViewHolder.gameCaption = (TextView) convertView.findViewById(R.id.game_caption);
            gameTongueViewHolder.gameRank = (ImageView) convertView.findViewById(R.id.game_rank);
            gameTongueViewHolder.guideline = (Guideline) convertView.findViewById(R.id.guideline);
            gameTongueViewHolder.gameIcon = (ImageView) convertView.findViewById(R.id.game_icon);
            convertView.setTag(gameTongueViewHolder);
        }

        // We get the data to display
        GameTongue gameTongueToDisplay = getItem(position);

        // We set the data in our ViewHolder
        if (gameTongueToDisplay != null) {
            gameTongueViewHolder.gameTitle.setText(gameTongueToDisplay.gameTitle);
            gameTongueViewHolder.gameProgression.setText(gameTongueToDisplay.gameProgression);
            gameTongueViewHolder.gameCaption.setText(gameTongueToDisplay.gameCaption);
            Picasso.with(getContext()).load(gameTongueToDisplay.urlGameIcon).into(gameTongueViewHolder.gameIcon);
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
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) gameTongueViewHolder.guideline.getLayoutParams();
            layoutParams.guidePercent = (float) gameTongueToDisplay.percentageBackground / 100;
            gameTongueViewHolder.guideline.setLayoutParams(layoutParams);
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
        Guideline guideline;
        ImageView gameIcon;
    }

    /**
     * Class that define the structure of the data that we wants to use with our adapter.
     */
    public static class GameTongue {
        final int gameId;
        final String gameTitle;
        final String gameProgression;
        final String gameCaption;
        int gameRank;
        final int percentageBackground;
        final String urlGameIcon;

        public GameTongue(int gameId, String gameTitle, String gameProgression, String gameCaption, int percentageBackground, String urlGameIcon) {
            this.gameId = gameId;
            this.gameTitle = gameTitle;
            this.gameProgression = gameProgression;
            this.gameCaption = gameCaption;
            this.percentageBackground = percentageBackground;
            this.urlGameIcon = urlGameIcon;
        }

        public int getGameId() {
            return gameId;
        }

        public void setGameRank(int gameRank) {
            this.gameRank = gameRank;
        }
    }

    // TODO Refactor that method & create Unit tests
    /**
     * Convert the list of OwnedGames in parameter into a a list of GameTongue objects.
     *
     * @param ownedGames List of OwnedGames to convert.
     * @return a list of GameTongue containing the OwnedGames that were in parameter.
     */
    public static List<GameTongue> convertOwnedGameListToGameTongueList(List<OwnedGame> ownedGames, String currency, Double profitableThreshold) {

        List<GameTongue> gameTongues = new ArrayList<>();

        if (ownedGames != null) {
            for (OwnedGame ownedGame : ownedGames) {

                // We could have 5 type of GameTongue:
                // 1-Games Never played without price
                // 2-Games Never played with price                      => Goal
                // 3-Game played without price
                // 4-Game played with price per hour over threshold     => Goal
                // 5-Game played with price per hour under threshold    => Goal


                // By default, the caption and the progression are empty
                String gameCaption;
                String gameProgression = "";

                Double nbHoursToReachThreshold;
                Double nbHoursPlayed = (double) ownedGame.getTimePlayedForever() / 60;

                int progressionPercentage = 100;

                // Types 1 & 2
                if (ownedGame.getTimePlayedForever() == 0) {
                    if (ownedGame.getGamePrice() <= 0) {
                        // Type 1
                        gameCaption = "0h";
                    } else {
                        // Type 2
                        nbHoursToReachThreshold = ownedGame.getGamePrice() / profitableThreshold;
                        gameCaption = UnitsConverterHelper.createProgressionInHours(nbHoursPlayed, nbHoursToReachThreshold);
                        gameProgression = "0%";
                        progressionPercentage = 0;
                    }
                } else {
                    if (ownedGame.getGamePrice() <= 0) {
                        // Type 3
                        gameCaption = UnitsConverterHelper.displayMinutesInHours(ownedGame.getTimePlayedForever());
                    } else {
                        if (ownedGame.getPricePerHour() > profitableThreshold) {
                            // Type 4
                            // When we are fetching goals, it's possible that the ownedGame is, in fact, a Goal object.
                            // If it's the case, we already have all the information to display and don"t have to do extra calculation.
                            if (ownedGame.getClass() == Goal.class) {
                                progressionPercentage = ((Goal) ownedGame).getCompletionPercentage();
                                nbHoursToReachThreshold = ((Goal) ownedGame).getNbHoursToComplete();
                                gameCaption = UnitsConverterHelper.createProgressionInHours(nbHoursPlayed, nbHoursToReachThreshold);
                            } else {
                                nbHoursToReachThreshold = ownedGame.getGamePrice() / profitableThreshold;
                                gameCaption = UnitsConverterHelper.createProgressionInHours(nbHoursPlayed, nbHoursToReachThreshold);
                                Double completion = (nbHoursPlayed / nbHoursToReachThreshold) * 100;
                                progressionPercentage = completion.intValue();
                            }
                            gameProgression = String.valueOf(progressionPercentage) + "%";
                        } else {
                            // Type 5
                            gameCaption = UnitsConverterHelper.displayMinutesInHours(ownedGame.getTimePlayedForever());
                            gameProgression = UnitsConverterHelper.createPricePerHour(ownedGame.getPricePerHour(), currency);
                        }
                    }
                }
                GameTongue currentGameTongue = new GameTongue(
                        ownedGame.getGame().getGameID(),
                        ownedGame.getGame().getGameName(),
                        gameCaption,
                        gameProgression,
                        progressionPercentage,
                        ownedGame.getGame().getGameIcon().toString()
                );
                gameTongues.add(currentGameTongue);
            }
        }
        return gameTongues;
    }
}
