package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Joffrey on 06/03/2017.
 * This class has been created to make the sorting of List<GameListItem> easier.
 */

public class GameListSorter {

    /**
     * This method sort the List<GameListItem> provided by name.
     *
     * @param listToSort List<GameListItem> that we want to sort
     * @param order      True for Ascendant, False for Descendant.
     * @return List<GameListItem> sorted by name.
     */
    public static List<GameListItem> sortByName(List<GameListItem> listToSort, boolean order) {
        List<GameListItem> sortedList = listToSort;
        if (order) {
            Collections.sort(sortedList, new GameNameComparatorAsc());
        } else {
            Collections.sort(sortedList, new GameNameComparatorDsc());
        }
        return sortedList;
    }

    /**
     * This method sort the List<GameListItem> provided by time played.
     *
     * @param listToSort List<GameListItem> that we want to sort
     * @param order      True for Ascendant, False for Descendant.
     * @return List<GameListItem> sorted by time played.
     */
    public static List<GameListItem> sortByTimePlayed(List<GameListItem> listToSort, boolean order) {
        List<GameListItem> sortedList = listToSort;
        if (order) {
            Collections.sort(sortedList, new TimePlayedComparatorAsc());
        } else {
            Collections.sort(sortedList, new TimePlayedComparatorDsc());
        }
        return sortedList;
    }

    /**
     * This method sort the List<GameListItem> provided by price.
     *
     * @param listToSort List<GameListItem> that we want to sort
     * @param order      True for Ascendant, False for Descendant.
     * @return List<GameListItem> sorted by price.
     */
    public static List<GameListItem> sortByPrice(List<GameListItem> listToSort, boolean order) {
        List<GameListItem> sortedList = listToSort;
        if (order) {
            Collections.sort(sortedList, new PriceComparatorAsc());
        } else {
            Collections.sort(sortedList, new PriceComparatorDsc());
        }
        return sortedList;
    }

    /**
     * This method sort the List<GameListItem> provided by price per hour.
     *
     * @param listToSort List<GameListItem> that we want to sort
     * @param order      True for Ascendant, False for Descendant.
     * @return List<GameListItem> sorted by price.
     */
    public static List<GameListItem> sortByPricePerHour(List<GameListItem> listToSort, boolean order) {
        List<GameListItem> sortedList = listToSort;
        if (order) {
            Collections.sort(sortedList, new PricePerHourComparatorAsc());
        } else {
            Collections.sort(sortedList, new PricePerHourComparatorDsc());
        }
        return sortedList;
    }

    /**
     * Class created to do the comparition.
     */
    private static class GameNameComparatorAsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem o1, GameListItem o2) {
            String gameName1 = o1.getGameName();
            String gameName2 = o2.getGameName();
            return gameName1.compareTo(gameName2);
        }

    }

    private static class GameNameComparatorDsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem o1, GameListItem o2) {
            String gameName1 = o1.getGameName();
            String gameName2 = o2.getGameName();
            return gameName2.compareTo(gameName1);
        }

    }

    private static class TimePlayedComparatorAsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem o1, GameListItem o2) {
            int timePlayed1 = o1.getGameTimePlayed();
            int timePlayed2 = o2.getGameTimePlayed();
            return timePlayed1 - timePlayed2;
        }

    }

    private static class TimePlayedComparatorDsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem o1, GameListItem o2) {
            int timePlayed1 = o1.getGameTimePlayed();
            int timePlayed2 = o2.getGameTimePlayed();
            return timePlayed2 - timePlayed1;
        }
    }

    private static class PriceComparatorAsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem o1, GameListItem o2) {
            double priceGame1 = o1.getGamePrice();
            double priceGame2 = o2.getGamePrice();
            double result = priceGame1 - priceGame2;
            if (result < 0) {
                return -1;
            } else if (result == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private static class PriceComparatorDsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem o1, GameListItem o2) {
            double priceGame1 = o1.getGamePrice();
            double priceGame2 = o2.getGamePrice();
            double result = priceGame2 - priceGame1;
            if (result < 0) {
                return -1;
            } else if (result == 0) {
                return 0;
            } else {
                return 1;
            }
        }

    }

    private static class PricePerHourComparatorAsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem game1, GameListItem game2) {

            // We define the order than we want to display the types of games

            final int GAME_FREE_TO_PLAY_PLAYED = 4;
            final int GAME_WITH_PRICE_PLAYED = 3;
            final int GAME_WITH_PRICE_NEVER_PLAYED = 2;
            final int GAME_FREE_TO_PLAY_NEVER_PLAYED = 1;
            final int GAME_WITHOUT_PRICE = 0;

            int typeGame1;
            int typeGame2;

            // First, we need to determine what kind of game is o1
            // It's a game without price
            if (game1.getGamePrice() == -1) {
                typeGame1 = GAME_WITHOUT_PRICE;
            } else if (game1.getGamePrice() == 0) {
                // It's a free to play, we have to check if it has already been played
                if (game1.getGameTimePlayed() == 0) {
                    typeGame1 = GAME_FREE_TO_PLAY_NEVER_PLAYED;
                } else {
                    typeGame1 = GAME_FREE_TO_PLAY_PLAYED;
                }
            } else if (game1.getGameTimePlayed() == 0) {
                // It's a paid game never played yet
                typeGame1 = GAME_WITH_PRICE_NEVER_PLAYED;
            } else {
                // It's a paid game already played
                typeGame1 = GAME_WITH_PRICE_PLAYED;
            }

            if (game2.getGamePrice() == -1) {
                typeGame2 = GAME_WITHOUT_PRICE;
            } else if (game2.getGamePrice() == 0) {
                // It's a free to play, we have to check if it has already been played
                if (game2.getGameTimePlayed() == 0) {
                    typeGame2 = GAME_FREE_TO_PLAY_NEVER_PLAYED;
                } else {
                    typeGame2 = GAME_FREE_TO_PLAY_PLAYED;
                }
            } else if (game2.getGameTimePlayed() == 0) {
                // It's a paid game never played yet
                typeGame2 = GAME_WITH_PRICE_NEVER_PLAYED;
            } else {
                // It's a paid game already played
                typeGame2 = GAME_WITH_PRICE_PLAYED;
            }

            // The first game have a stronger type than the second
            if (typeGame1 > typeGame2) return -1;
            // The first game have a weaker type than the second
            if (typeGame1 < typeGame2) return 1;

            switch (typeGame1) {
                case GAME_FREE_TO_PLAY_NEVER_PLAYED:
                    return 0;

                case GAME_FREE_TO_PLAY_PLAYED:
                    // If the types are the same and they are F2P, we sort them by there playing time
                    if (game1.getGameTimePlayed() > game2.getGameTimePlayed()) return -1;
                    if (game1.getGameTimePlayed() == game2.getGameTimePlayed()) return 0;
                    if (game1.getGameTimePlayed() < game2.getGameTimePlayed()) return 1;
                    break;

                case GAME_WITH_PRICE_PLAYED:
                    // If they are paid games already played, we sort them by price per hour
                    double pricePerHourGame1 = game1.getGamePrice() / ((double) game1.getGameTimePlayed() / 60);
                    double pricePerHourGame2 = game2.getGamePrice() / ((double) game2.getGameTimePlayed() / 60);

                    if (pricePerHourGame1 > pricePerHourGame2) return 1;
                    if (pricePerHourGame1 == pricePerHourGame2) return 0;
                    if (pricePerHourGame1 < pricePerHourGame2) return -1;
                    break;

                case GAME_WITH_PRICE_NEVER_PLAYED:
                    // If they are paid games never played, we sort them by price
                    if (game1.getGamePrice() > game2.getGamePrice()) return 1;
                    if (game1.getGamePrice() == game2.getGamePrice()) return 0;
                    if (game1.getGamePrice() < game2.getGamePrice()) return -1;
                    break;

                default:
                    // For the games without price, we sort them by play time
                    // If they are paid games never played, we sort them by price
                    if (game1.getGameTimePlayed() > game2.getGameTimePlayed()) return 1;
                    if (game1.getGameTimePlayed() == game2.getGameTimePlayed()) return 0;
                    if (game1.getGameTimePlayed() < game2.getGameTimePlayed()) return -1;
            }
            return 0;

        }
    }

    private static class PricePerHourComparatorDsc implements Comparator<GameListItem> {

        @Override
        public int compare(GameListItem game1, GameListItem game2) {

            // We define the order than we want to display the types of games
            final int GAME_WITH_PRICE_PLAYED = 4;
            final int GAME_WITH_PRICE_NEVER_PLAYED = 3;
            final int GAME_FREE_TO_PLAY_PLAYED = 2;
            final int GAME_FREE_TO_PLAY_NEVER_PLAYED = 1;
            final int GAME_WITHOUT_PRICE = 0;

            int typeGame1;
            int typeGame2;

            // First, we need to determine what kind of game is o1
            // It's a game without price
            if (game1.getGamePrice() == -1) {
                typeGame1 = GAME_WITHOUT_PRICE;
            } else if (game1.getGamePrice() == 0) {
                // It's a free to play, we have to check if it has already been played
                if (game1.getGameTimePlayed() == 0) {
                    typeGame1 = GAME_FREE_TO_PLAY_NEVER_PLAYED;
                } else {
                    typeGame1 = GAME_FREE_TO_PLAY_PLAYED;
                }
            } else if (game1.getGameTimePlayed() == 0) {
                // It's a paid game never played yet
                typeGame1 = GAME_WITH_PRICE_NEVER_PLAYED;
            } else {
                // It's a paid game already played
                typeGame1 = GAME_WITH_PRICE_PLAYED;
            }

            if (game2.getGamePrice() == -1) {
                typeGame2 = GAME_WITHOUT_PRICE;
            } else if (game2.getGamePrice() == 0) {
                // It's a free to play, we have to check if it has already been played
                if (game2.getGameTimePlayed() == 0) {
                    typeGame2 = GAME_FREE_TO_PLAY_NEVER_PLAYED;
                } else {
                    typeGame2 = GAME_FREE_TO_PLAY_PLAYED;
                }
            } else if (game2.getGameTimePlayed() == 0) {
                // It's a paid game never played yet
                typeGame2 = GAME_WITH_PRICE_NEVER_PLAYED;
            } else {
                // It's a paid game already played
                typeGame2 = GAME_WITH_PRICE_PLAYED;
            }

            // The first game have a stronger type than the second
            if (typeGame1 > typeGame2) return -1;
            // The first game have a weaker type than the second
            if (typeGame1 < typeGame2) return 1;

            switch (typeGame1) {
                case GAME_FREE_TO_PLAY_NEVER_PLAYED:
                    return 0;

                case GAME_FREE_TO_PLAY_PLAYED:
                    // If the types are the same and they are F2P, we sort them by there playing time
                    if (game1.getGameTimePlayed() > game2.getGameTimePlayed()) return 1;
                    if (game1.getGameTimePlayed() == game2.getGameTimePlayed()) return 0;
                    if (game1.getGameTimePlayed() < game2.getGameTimePlayed()) return -1;
                    break;

                case GAME_WITH_PRICE_PLAYED:
                    // If they are paid games already played, we sort them by price per hour
                    double pricePerHourGame1 = game1.getGamePrice() / ((double) game1.getGameTimePlayed() / 60);
                    double pricePerHourGame2 = game2.getGamePrice() / ((double) game2.getGameTimePlayed() / 60);

                    if (pricePerHourGame1 > pricePerHourGame2) return -1;
                    if (pricePerHourGame1 == pricePerHourGame2) return 0;
                    if (pricePerHourGame1 < pricePerHourGame2) return 1;
                    break;

                case GAME_WITH_PRICE_NEVER_PLAYED:
                    // If they are paid games never played, we sort them by price
                    if (game1.getGamePrice() > game2.getGamePrice()) return -1;
                    if (game1.getGamePrice() == game2.getGamePrice()) return 0;
                    if (game1.getGamePrice() < game2.getGamePrice()) return 1;
                    break;

                default:
                    // For the games without price, we sort them by play time
                    // If they are paid games never played, we sort them by price
                    if (game1.getGameTimePlayed() > game2.getGameTimePlayed()) return -1;
                    if (game1.getGameTimePlayed() == game2.getGameTimePlayed()) return 0;
                    if (game1.getGameTimePlayed() < game2.getGameTimePlayed()) return 1;
            }
            return 0;
        }
    }
}
