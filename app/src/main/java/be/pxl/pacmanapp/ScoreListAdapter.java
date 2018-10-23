package be.pxl.pacmanapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListAdapter.ScoreListViewHolder> {
    private Cursor cursor;
    private Context context;

    public ScoreListAdapter(Cursor cursor, Context context){
        this.cursor = cursor;
        this.context = context;
    }

    @NonNull
    @Override
    public ScoreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.score_list_item, parent,false);
        return new ScoreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreListViewHolder viewHolder, int position) {
        if(!cursor.moveToPosition(position)){
            return;
        }

        String scorePosition = null;
        scorePosition = cursor.getColumnName(cursor.getColumnIndex("position"));

        String scorePoints = null;
        scorePoints = cursor.getColumnName(cursor.getColumnIndex("points"));

        viewHolder.positionTextView.setText(scorePosition);
        viewHolder.positionTextView.setVisibility(View.VISIBLE);
        viewHolder.pointsTextView.setText(scorePoints);
        viewHolder.pointsTextView.setVisibility(View.VISIBLE);

        }

    @Override
    public int getItemCount() {
        if(cursor == null){
            return 0;
        }

        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(cursor != null){
            cursor.close();
        }

        cursor = newCursor;

        if(newCursor != null){
            this.notifyDataSetChanged();
        }
    }

    public class ScoreListViewHolder extends RecyclerView.ViewHolder{
        public TextView positionTextView;
        public TextView pointsTextView;

        public ScoreListViewHolder(final View itemView) {
            super(itemView);
            positionTextView = itemView.findViewById(R.id.position);
            pointsTextView = itemView.findViewById(R.id.points);
        }
    }
}
