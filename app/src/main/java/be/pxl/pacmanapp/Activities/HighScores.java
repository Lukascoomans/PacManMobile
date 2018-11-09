package be.pxl.pacmanapp.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import be.pxl.pacmanapp.Data.DataBaseExecutor;
import be.pxl.pacmanapp.Data.DataBaseHelper;
import be.pxl.pacmanapp.JSONArrayCursor;
import be.pxl.pacmanapp.Models.HighScoreModel;
import be.pxl.pacmanapp.Models.ScoreBoardModel;
import be.pxl.pacmanapp.R;
import be.pxl.pacmanapp.ScoreListAdapter;

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
        setScoreList("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }).start();


    }

    public void getData(){
        DataBaseHelper helper = new DataBaseHelper(this);

        DataBaseExecutor executor = new DataBaseExecutor(helper);
        ArrayList<HighScoreModel> models = executor.ReadFromDatabase();

        Collections.sort(models,new Comparator<HighScoreModel>() {
            @Override
            public int compare(HighScoreModel s1, HighScoreModel s2) {
                return -Integer.compare(Integer.parseInt(s1.getScore()),Integer.parseInt(s2.getScore()));
            }
        });

        String json="[";
        int i =1;
        for (HighScoreModel model: models) {
            json+="{";
            json+="\"name\":\""+model.getName();
            json+="\",\"points\":\""+model.getScore();
            json+="\",\"position\":\""+i++;
            json+="\",\"country\":\""+model.getCountry();
            json+="\"},";
        }
        json =json.substring(0, json.length() - 1);
        json+="]";
        helper.close();
        setScoreList(json);
    }

    public void setScoreList(String json){
        cursor = getJSONCursor(json);

        adapter = new ScoreListAdapter(cursor, this, false,new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    TextView nameView = (TextView)v.findViewById(R.id.name);
                    TextView countryView = (TextView)v.findViewById(R.id.country);
                    TextView pointsView = (TextView)v.findViewById(R.id.points);

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                        TextView localNameView = (TextView)HighScores.this.findViewById(R.id.article_fragment).findViewById(R.id.name);
                        TextView localCountryView =(TextView)HighScores.this.findViewById(R.id.article_fragment).findViewById(R.id.country);
                        TextView localPointsView =(TextView)HighScores.this.findViewById(R.id.article_fragment).findViewById(R.id.points);

                        localNameView.setText(nameView.getText());
                        localCountryView.setText(countryView.getText());
                        localPointsView.setText(pointsView.getText());

                    } else {

                        Intent nextActivity = new Intent( getBaseContext(), ScoreBoardActivity.class);

                        Bundle args = new Bundle();
                        nextActivity.putExtra("name",nameView.getText());
                        nextActivity.putExtra("country",countryView.getText());
                        nextActivity.putExtra("points",pointsView.getText());

                        startActivity(nextActivity);
                    }

            }
        });

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
