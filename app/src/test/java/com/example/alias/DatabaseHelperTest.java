package com.example.alias;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, packageName = "com.example.alias", manifest=Config.NONE)
public class DatabaseHelperTest {

    SQLiteDatabase db;

    /*@Before
    public void init(){
        GameplayActivity gameplayActivity = Robolectric.setupActivity(GameplayActivity.class);
        DatabaseHelper dbHelper = new DatabaseHelper(gameplayActivity);
        dbHelper.onCreate(db);
        dbHelper.addPlayerData("asd");
    }

    @Test
    public void DatabaseHelper_onCreate_dbCreatedAsExpected(){
        assertNotNull(db);
    }*/
}
