package be.pxl.pacmanapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import static be.pxl.pacmanapp.FeedReaderContract.FeedEntry.*;

public class HelloWorld extends AppCompatActivity {

    DataBaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        myDb = new DataBaseHelper(this);


        final TextView mTextView = (TextView) findViewById(R.id.text);
        mTextView.setText("SJDNALSDNOJSA");


        ScoreBoardCaller scoreBoardCaller = new ScoreBoardCaller(this,findViewById(R.id.text));

        scoreBoardCaller.callDatabase();
    }
}
