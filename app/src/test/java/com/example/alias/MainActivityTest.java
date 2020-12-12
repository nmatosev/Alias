package com.example.alias;

import android.content.Intent;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, packageName = "com.example.alias", manifest=Config.NONE)
public class MainActivityTest {
    MainActivity mainActivity;
    @Before
    public void init(){

        mainActivity = Robolectric.setupActivity(MainActivity.class);

    }

    @Test
    public void MainActivity_testPairUpIntent(){
        mainActivity.findViewById(R.id.button_play).performClick();

        Intent expected = new Intent(mainActivity, PairUpPlayersActivity.class);
        Intent actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }

    @Test
    public void MainActivity_testSettingIntent(){
        mainActivity.findViewById(R.id.button_settings).performClick();

        Intent expected = new Intent(mainActivity, SettingsActivity.class);
        Intent actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }

    @Test
    public void MainActivity_testRulesIntent(){
        mainActivity.findViewById(R.id.button_rules).performClick();

        Intent expected = new Intent(mainActivity, RulesActivity.class);
        Intent actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }
}
