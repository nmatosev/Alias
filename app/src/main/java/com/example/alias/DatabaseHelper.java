package com.example.alias;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.PublicKey;

import static android.content.ContentValues.TAG;
import static android.os.Build.ID;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static java.sql.Types.INTEGER;

public  class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Alias.db";

    public static final String TABLE_NAME  = "players_table";
    public static final String COL_1 =  "ID";
    public static final String COL_2 =  "NAME";
    public static final String COL_3 = "ORDER_IN_LIST";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 +  " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String drop = "DROP TABLE IF EXISTS ";
        db.execSQL(drop + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, item);
        long result = db.insert(TABLE_NAME,null, contentValues);
        if(result == -1){
            return false;
        } else{
            return true;
        }
    }

    public boolean deleteItem(String name){
        Log.d("Deleted Log", "Deleted " + name);
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_2+"="+name,null)>0;

    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " +TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }
}
