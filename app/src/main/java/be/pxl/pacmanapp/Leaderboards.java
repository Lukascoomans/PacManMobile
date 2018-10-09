package be.pxl.pacmanapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Leaderboards extends AppCompatActivity {
    ListLayoutBinding listLayoutBinding;
    private List<Score> scoreList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance){
        View view = inflater.inflate(R.layout.list_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.scoreRecyclerView);


    }

    public void fillList(){
        listLayoutBinding = DataBindingUtil.setContentView();
    }
}
