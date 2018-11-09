package be.pxl.pacmanapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreDetail extends Fragment {


    public ScoreDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        Bundle bundle= this.getArguments();
        String name = bundle.getString("name");
        String country = bundle.getString("country");
        String points = bundle.getString("points");

        TextView nameView = (TextView)getView().findViewById(R.id.name);
        TextView countryView = (TextView)getView().findViewById(R.id.country);
        TextView pointsView = (TextView)getView().findViewById(R.id.points);

        nameView.setText(name);
        countryView.setText(country);
        pointsView.setText(points);
        */

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score_detail, container, false);
    }

}
