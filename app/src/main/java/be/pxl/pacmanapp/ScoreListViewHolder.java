package be.pxl.pacmanapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ScoreListViewHolder extends RecyclerView.ViewHolder {
    public TextView positionTextView;
    public TextView pointsTextView;

    public ScoreListViewHolder(final View itemView) {
        super(itemView);
        positionTextView = itemView.findViewById(R.id.position);
        pointsTextView = itemView.findViewById(R.id.points);
    }

    public void bindData(final ScoreListViewModel viewModel) {
        positionTextView.setText(viewModel.getPosition());
        pointsTextView.setText(viewModel.getPoints());
    }
}
