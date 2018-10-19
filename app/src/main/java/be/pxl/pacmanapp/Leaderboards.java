package be.pxl.pacmanapp;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOError;

public class Leaderboards extends AppCompatActivity {
    private ScoreListAdapter adapter;
    private RecyclerView scoreList;
    private Cursor cursor;

    private String sample_response = "[{\"points\":300,\"position\":3},{\"points\":1000,\"position\":2},{\"points\":12300,\"position\":1}]";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        scoreList = (RecyclerView) this.findViewById(R.id.rv_score);

        cursor = getJSONCursor(sample_response);

        adapter = new ScoreListAdapter(cursor, this);

        scoreList.setLayoutManager(new LinearLayoutManager(this));
        scoreList.setAdapter(adapter);
    }

    private Cursor getJSONCursor(String response){
        try
        {
            JSONArray array = new JSONArray(response);
            return new com.example.android.gamescores.JSONArrayCursor(array);
        }
        catch(JSONException exception)
        {
            String ex = exception.getMessage();
        }
        return null;
    }

}
