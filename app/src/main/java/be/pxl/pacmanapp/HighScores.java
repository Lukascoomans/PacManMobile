package be.pxl.pacmanapp;

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

        setScoreList(test);




    }

    public void setScoreList(String json){
        cursor = getJSONCursor(json);

        adapter = new ScoreListAdapter(cursor, this, true,new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    TextView nameView = (TextView)v.findViewById(R.id.name);
                    TextView countryView = (TextView)v.findViewById(R.id.country);
                    TextView pointsView = (TextView)v.findViewById(R.id.points);

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                        TextView localNameView = (TextView)HighScores.this.findViewById(R.id.detailname);
                        TextView localCountryView =(TextView)HighScores.this.findViewById(R.id.detailcountry);
                        TextView loaclPointsView =(TextView)HighScores.this.findViewById(R.id.detailpoints);

                        localNameView.setText(nameView.getText());
                        localCountryView.setText(countryView.getText());
                        loaclPointsView.setText(pointsView.getText());

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
