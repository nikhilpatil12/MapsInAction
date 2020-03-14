package com.journaldev.MapsInAction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="drone.db";
    public static final String TABLE_NAME ="locations";
    public static final String COL_1 ="name";
    public static final String COL_2 ="latitude";
    public static final String COL_3 ="longitude";
    public static final String COL_4 ="message";
    Context context;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_NAME +" (NAME TEXT,LATITUDE TEXT,LONGITUDE TEXT,MESSAGE INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public boolean insertData(String nm,String lat,String lon,String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,nm);
        contentValues.put(COL_2,lat);
        contentValues.put(COL_3,lon);
        contentValues.put(COL_4,msg);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result== -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }
}