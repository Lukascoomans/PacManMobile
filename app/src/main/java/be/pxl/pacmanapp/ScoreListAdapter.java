package be.pxl.pacmanapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ScoreListViewHolder> {
    private int position;
    private int score;

    public ScoreListAdapter(int position, int score){
        this.position = position;
        this.score = score;
    }

    @NonNull
    @Override
    public ScoreListAdapter.ScoreListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreListAdapter.ScoreListViewHolder scoreListViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
