package be.pxl.pacmanapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static be.pxl.pacmanapp.FeedReaderContract.FeedEntry.*;

public class DataBaseExecutor {

    private DataBaseHelper db;


    public DataBaseExecutor(DataBaseHelper db) {
        this.db = db;
    }

    public long WriteToDatabase(HighScoreModel model){

        SQLiteDatabase writeable = db.getWritableDatabase();

        ContentValues values = new ContentValues();


        String score = model.getScore();
        String country = model.getCountry();
        String name = model.getName();

        values.put(COLUMN_NAME_SCORE,score);
        values.put(COLUMN_NAME_COUNTRY,country);
        values.put(COLUMN_NAME_NAME,name);

        long newRowid = writeable.insert(TABLE_NAME,null,values);

        writeable.close();
        return newRowid;

    }

    //Read from database

    public ArrayList<ScoreBoardModel> ReadFromDatabase(){
        SQLiteDatabase readable = db.getWritableDatabase();

        String[] projection = {
                COLUMN_NAME_ID,
                COLUMN_NAME_NAME,
                COLUMN_NAME_SCORE,
                COLUMN_NAME_COUNTRY
        };

        String selection = COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { "test name" };

        Cursor cursor = readable.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        ArrayList<ScoreBoardModel> itemIds = new ArrayList<ScoreBoardModel>();
        while(cursor.moveToNext()) {
            ScoreBoardModel model = new ScoreBoardModel();
                model.ID=cursor.getString(0);
                model.NAME=cursor.getString(1);
                model.SCORE=cursor.getString(2);
                model.COUNTRY=cursor.getString(3);

            itemIds.add(model);
        }

        return itemIds;

    }


}
