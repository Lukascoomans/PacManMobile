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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import be.pxl.pacmanapp.JSONArrayCursor;
import be.pxl.pacmanapp.R;
import be.pxl.pacmanapp.ScoreListAdapter;

public class Leaderboards extends AppCompatActivity {
    private static final String LIST_NAME = "Leaderboards";
    private ScoreListAdapter adapter;
    private RecyclerView scoreList;
    private Cursor cursor;

    private String no_connection_response = "[{\"name\":No Connection,\"points\":,\"position\":,\"country\":}]";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        TextView listNameTextView = this.findViewById(R.id.list_name_textview);
        listNameTextView.setText(LIST_NAME);

        setScoreList("");
        getWebString();
    }

    public void setScoreList(String json){
        scoreList = this.findViewById(R.id.rv_score);

        cursor = getJSONCursor(json);

        adapter = new ScoreListAdapter(cursor, this, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView nameView = (TextView)v.findViewById(R.id.name);
                TextView countryView = (TextView)v.findViewById(R.id.country);
                TextView pointsView = (TextView)v.findViewById(R.id.points);

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {



                    TextView localNameView = (TextView)Leaderboards.this.findViewById(R.id.article_fragment).findViewById(R.id.name);
                    TextView localCountryView =(TextView)Leaderboards.this.findViewById(R.id.article_fragment).findViewById(R.id.country);
                    TextView loaclPointsView =(TextView)Leaderboards.this.findViewById(R.id.article_fragment).findViewById(R.id.points);

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

    public void getWebString(){

        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://10.0.2.2:8080/PacMan";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        setScoreList(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                setScoreList(no_connection_response);
            }
        });

        queue.add(stringRequest);
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
