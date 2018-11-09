package be.pxl.pacmanapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class HighScores extends AppCompatActivity {
    private static final String LIST_NAME = "Highscores";
    private ScoreListAdapter adapter;
    private RecyclerView scoreList;
    private Cursor cursor;

    private String sample_response = "[{\"name\":Stefan,\"points\":12300,\"position\":1,\"country\":Belgium},{\"name\":David,\"points\":6000,\"position\":2,\"country\":England},{\"name\":Steve,\"points\":2400,\"position\":3,\"country\":America}]";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        TextView listNameTextView = this.findViewById(R.id.list_name_textview);
        listNameTextView.setText(LIST_NAME);

        scoreList = this.findViewById(R.id.rv_score);

        DataBaseExecutor executor = new DataBaseExecutor(new DataBaseHelper(this));
        ArrayList<ScoreBoardModel> models = executor.ReadFromDatabase();

        String test="[";

        for (ScoreBoardModel model: models) {
            test+="{";
            test+="\"name\":\""+model.NAME;
            test+="\",\"points\":\""+model.SCORE;
            test+="\",\"position\":\""+model.ID;
            test+="\",\"country\":\""+model.COUNTRY;
            test+="\"},";
        }
        test =test.substring(0, test.length() - 1);
        test+="]";
        cursor = getJSONCursor(test);

        adapter = new ScoreListAdapter(cursor, this, false);

        scoreList.setLayoutManager(new LinearLayoutManager(this));
        scoreList.setAdapter(adapter);


    }

    public void setScoreList(String json,RecyclerView scoreList){
        cursor = getJSONCursor(json);

        adapter = new ScoreListAdapter(cursor, this, false);

        scoreList.setLayoutManager(new LinearLayoutManager(this));
        scoreList.setAdapter(adapter);
    }


    private Cursor getJSONCursor(String response){
        try
        {
            JSONArray array = new JSONArray(response);
            return new JSONArrayCursor(array);
        }
        catch(JSONException exception)
        {
            String ex = exception.getMessage();
        }
        return null;
    }
}
