package be.pxl.pacmanapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ScoreBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        String name = (String)getIntent().getExtras().get("name");
        String country = (String)getIntent().getExtras().get("country");
        String points = (String)getIntent().getExtras().get("points");

        TextView nameView = (TextView)findViewById(R.id.name);
        TextView countryView = (TextView)findViewById(R.id.country);
        TextView pointsView = (TextView)findViewById(R.id.points);
        nameView.setText(name);
        countryView.setText(country);
        pointsView.setText(points);

    }
}
