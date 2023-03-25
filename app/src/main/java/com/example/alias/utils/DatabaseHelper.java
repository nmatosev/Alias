package com.example.alias.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class responsible for CRUD operations on Sqlite DB.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Alias.db";

    public static final String PLAYERS_TABLE = "players_table";
    public static final String TEAMS_TABLE = "teams_table";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "ORDER_IN_LIST";

    public static final String PLAYER1 = "PLAYER1";
    public static final String PLAYER2 = "PLAYER2";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPlayersTable = "CREATE TABLE " + PLAYERS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT)";
        String createTeamTable = "CREATE TABLE " + TEAMS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, PLAYER1 TEXT, PLAYER2 TEXT)";
        db.execSQL(createPlayersTable);
        db.execSQL(createTeamTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop = "DROP TABLE IF EXISTS ";
        db.execSQL(drop + PLAYERS_TABLE);
        db.execSQL(drop + TEAMS_TABLE);
        onCreate(db);
    }

    public boolean addPlayerData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, item);
        long result = db.insert(PLAYERS_TABLE, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addTeamData(String player1, String player2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYER1, player1);
        contentValues.put(PLAYER2, player2);

        long result = db.insert(TEAMS_TABLE, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deletePlayer(String name) {
        Log.d("Deleted Log", "Deleted " + name);
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PLAYERS_TABLE, COL_2 + "=" + name, null) > 0;
    }

    public boolean deleteTeams(String pair) {

        String player1 = pair.split("&")[0];
        String player2 = pair.split("&")[1];

        Log.d("Deleted Log", "Deleted " + player1);
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("team table ", getTableAsString(db, TEAMS_TABLE));

        return db.delete(TEAMS_TABLE, "PLAYER1='" + player1 + "' AND PLAYER2='" + player2 + "'", null) > 0;
    }

    public Cursor getPlayers() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + PLAYERS_TABLE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getTeams() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TEAMS_TABLE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /**
     * Helper function that parses a given table into a string
     * and returns it for easy printing. The string consists of
     * the table name and then each row is iterated through with
     * column_name: value pairs printed out.
     *
     * @param db        the database to get the table from
     * @param tableName the the name of the table to parse
     * @return the table tableName as a string
     */
    public String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d("getteble", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }
}
