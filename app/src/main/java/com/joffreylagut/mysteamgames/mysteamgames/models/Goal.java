package com.joffreylagut.mysteamgames.mysteamgames.models;

import android.content.Context;
import androidx.preference.PreferenceManager;

import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;

import java.util.Comparator;

/**
 * Goal.java
 * Purpose: Blueprint for a Goal object.
 * A Goal object is a OwnedGame object with 2 more parameters.
 *
 * @author Joffrey LAGUT
 * @version 1.1 2017-05-17
 */

public class Goal extends OwnedGame {

    private double nbHoursToComplete;
    private int completionPercentage;

    public Goal(Context context, OwnedGame ownedGame) {
        super(ownedGame.getUserId(),
                ownedGame.getGame(),
                ownedGame.getTimePlayedForever(),
                ownedGame.getTimePlayed2Weeks(),
                ownedGame.getGamePrice(),
                ownedGame.isFavorite(),
                ownedGame.getGameBundle(),
                ownedGame.getPricePerHour());
        Double profitableThreshold = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString(SharedPreferencesHelper.PROFITABLE_LIMIT, "1"));
        this.calculateCompletionPercentage(profitableThreshold);
    }

    public Goal(Double profitableThreshold, OwnedGame ownedGame) {
        super(ownedGame.getUserId(),
                ownedGame.getGame(),
                ownedGame.getTimePlayedForever(),
                ownedGame.getTimePlayed2Weeks(),
                ownedGame.getGamePrice(),
                ownedGame.isFavorite(),
                ownedGame.getGameBundle(),
                ownedGame.getPricePerHour());
        this.calculateCompletionPercentage(profitableThreshold);
    }

    public void calculateCompletionPercentage(Double profitableThreshold) {

        this.nbHoursToComplete = this.getGamePrice() / profitableThreshold;

        if (this.getPricePerHour() > profitableThreshold) {
            Double nbHoursPlayed = Double.valueOf(this.getTimePlayedForever()) / 60;
            this.completionPercentage = (int) ((nbHoursPlayed / this.nbHoursToComplete) * 100);
        } else {
            Double nbHoursToReachThreshold = this.getGamePrice() / profitableThreshold;
            this.completionPercentage = 100;
        }
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public double getNbHoursToComplete() {
        return nbHoursToComplete;
    }

    /**
     * Class created to do the comparison between 2 AlmostAchievedOwnedGames.
     * By default, the order is ASC.
     */
    public static class GoalCompletionComparator implements Comparator<Goal> {

        @Override
        public int compare(Goal o1, Goal o2) {
            Integer completionPercentage1 = o1.completionPercentage;
            Integer completionPercentage2 = o2.completionPercentage;
            return completionPercentage1.compareTo(completionPercentage2);
        }
    }

    /**
     * Class created to do the comparison between 2 AlmostAchievedOwnedGames.
     * By default, the order is ASC.
     */
    public static class GoalCompletionComparatorFromOwnedGame implements Comparator<OwnedGame> {

        @Override
        public int compare(OwnedGame o1, OwnedGame o2) {
            if (o1.getClass() != Goal.class || o2.getClass() != Goal.class) {
                throw new IllegalArgumentException("The OwnedGames are not Goals");
            }
            Integer completionPercentage1 = ((Goal) o1).completionPercentage;
            Integer completionPercentage2 = ((Goal) o2).completionPercentage;
            return completionPercentage1.compareTo(completionPercentage2);
        }
    }

    /**
     * Class created to do the comparison between 2 AlmostAchievedOwnedGames.
     * By default, the order is ASC.
     */
    public static class GoalNbHoursToCompleteComparatorFromOwnedGame implements Comparator<OwnedGame> {

        @Override
        public int compare(OwnedGame o1, OwnedGame o2) {
            if (o1.getClass() != Goal.class || o2.getClass() != Goal.class) {
                throw new IllegalArgumentException("The OwnedGames are not Goals");
            }
            Double nbHoursToComplete1 = ((Goal) o1).nbHoursToComplete;
            Double nbHoursToComplete2 = ((Goal) o2).nbHoursToComplete;
            return nbHoursToComplete1.compareTo(nbHoursToComplete2);
        }
    }
}
