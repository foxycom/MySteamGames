package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameTongueAdapter;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * SampleGeneratorTests.java
 * Purpose: Unit tests for HomeFragmentTests.java.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-09
 */

public class SampleGeneratorTests {

    /**
     * Check if the method generateListGameTongue is returning at least 1 GameTongue.
     */
    @Test
    public void generateListGameTongue_returnGameTongues() {
        Context context = InstrumentationRegistry.getTargetContext();
        List<GameTongueAdapter.GameTongue> games = SampleGenerator.generateListGameTongue(context);
        assertTrue(games.size() > 0);
    }

    /**
     * Check if the method generateMostProfitableListGameTongue is returning at least 1 GameTongue.
     */
    @Test
    public void generateMostProfitableListGameTongue_returnGameTongues() {
        Context context = InstrumentationRegistry.getTargetContext();
        List<GameTongueAdapter.GameTongue> games = SampleGenerator.generateListMostProfitableGameTongue(context);
        assertTrue(games.size() > 0);
    }

}
