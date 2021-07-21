package com.example.calendarapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_EVENTS_TABLE = "create table "+DbStructure.EVENT_TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            +DbStructure.EVENT+" TEXT, "+DbStructure.TIME+" TEXT, "+DbStructure.DATE+" TEXT, "+DbStructure.MONTH+" TEXT, "+DbStructure.YEAR+"" +
            " TEXT, "+DbStructure.NOTIFY+" TEXT)";
    private static final String DROP_EVENTS_TABLE = "DROP TABLE IF EXISTS "+DbStructure.EVENT_TABLE_NAME;
    private static final String CREATE_USERS_TABLE = "create table "+DbStructure.USERS_TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            +DbStructure.USERNAME+" TEXT,"+DbStructure.EMAIL+" TEXT, "+DbStructure.PASSWORD+" TEXT)";

    public DbOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbOpenHelper(@Nullable Context context) {
        super(context,DbStructure.DB_NAME,null,DbStructure.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EVENTS_TABLE);
        onCreate(db);
    }

    public void SaveEvent(String event, String time,String date,String month,String year,String notify,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbStructure.EVENT,event);
        contentValues.put(DbStructure.TIME,time);
        contentValues.put(DbStructure.DATE,date);
        contentValues.put(DbStructure.MONTH,month);
        contentValues.put(DbStructure.YEAR,year);
        contentValues.put(DbStructure.NOTIFY,notify);
        database.insert(DbStructure.EVENT_TABLE_NAME,null,contentValues);
    }

    public Cursor ReadEvents(String date,SQLiteDatabase database){
        String [] projections = {DbStructure.EVENT,DbStructure.TIME,DbStructure.DATE,DbStructure.MONTH,DbStructure.YEAR};
        String selection = DbStructure.DATE +"=?";
        String [] selectionArgs = {date};

        return database.query(DbStructure.EVENT_TABLE_NAME,projections,selection,selectionArgs,null,null,null);
    }

    public Cursor ReadIDEvents(String date,String event,String time,SQLiteDatabase database){
        String [] projections = {DbStructure.ID,DbStructure.NOTIFY};
        String selection = DbStructure.DATE +"=? and "+DbStructure.EVENT+"=? and "+DbStructure.TIME+"=?";
        String [] selectionArgs = {date,event,time};

        return database.query(DbStructure.EVENT_TABLE_NAME,projections,selection,selectionArgs,null,null,null);
    }

    public Cursor ReadEventsPerMonth(String month, String year, SQLiteDatabase database){
        String [] projections = {DbStructure.EVENT,DbStructure.TIME,DbStructure.DATE,DbStructure.MONTH,DbStructure.YEAR};
        String selection = DbStructure.MONTH +"=? and "+ DbStructure.YEAR +"=?";
        String [] selectionArgs = {month,year};

        return database.query(DbStructure.EVENT_TABLE_NAME,projections,selection,selectionArgs,null,null,null);
    }

    public Cursor ReadAllEvents(SQLiteDatabase database){
        String [] projections = {DbStructure.EVENT,DbStructure.TIME,DbStructure.DATE,DbStructure.MONTH,DbStructure.YEAR};
        return database.query(DbStructure.EVENT_TABLE_NAME,projections,null,null,null,null,DbStructure.DATE+" ASC");
    }

    public void DeleteEvent(String event,String date,String time,SQLiteDatabase database){
        String selection = DbStructure.EVENT+"=? and "+DbStructure.DATE+"=? and "+DbStructure.TIME+"=?";
        String[] args = {event,date,time};
        database.delete(DbStructure.EVENT_TABLE_NAME,selection,args);
    }

    public void UpdateEvent(String date,String event,String time,String notify,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbStructure.NOTIFY,notify);
        String selection = DbStructure.DATE +"=? and "+DbStructure.EVENT+"=? and "+DbStructure.TIME+"=?";
        String [] selectionArgs = {date,event,time};
        database.update(DbStructure.EVENT_TABLE_NAME,contentValues,selection,selectionArgs);
    }

    public Boolean insertUser(String username, String password,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbStructure.USERNAME,username);
        contentValues.put(DbStructure.PASSWORD,password);
        contentValues.put(DbStructure.EMAIL,email);
        long result = db.insert(DbStructure.USERS_TABLE_NAME,null,contentValues);
        return result != -1;
    }

    public Boolean checkUser(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from userstable where username =?",new String[] {username});
        return cursor.getCount() > 0;
    }

    public Boolean checkUserPass(String username,String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String [] projections = {DbStructure.USERNAME,DbStructure.PASSWORD,DbStructure.EMAIL};
        String selection = DbStructure.USERNAME +"=? and "+DbStructure.PASSWORD+"=?";
        String [] selectionArgs = {username,password};
        Cursor cursor = db.query(DbStructure.USERS_TABLE_NAME,projections,selection,selectionArgs,null,null,null);

        return cursor.getCount() > 0;
    }
}
