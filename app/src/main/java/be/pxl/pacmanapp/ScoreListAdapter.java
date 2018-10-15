package be.pxl.pacmanapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScoreListAdapter extends RecyclerView.Adapter<ScoreListViewHolder> {
    private Cursor cursor;
    private Context context;

    private int position;
    private int points;

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
    public void onBindViewHolder(@NonNull ScoreListViewHolder viewHolder, int pos) {
        if(!cursor.moveToPosition(pos)){
            return;
        }

        String scorePosition = null;
        scorePosition = cursor.getColumnName(cursor.getColumnIndex("position"));

        String scorePoints = null;
        scorePoints = cursor.getColumnName(cursor.getColumnIndex("points"));

        viewHolder.positionTextView.setText(scorePosition);
        viewHolder.pointsTextView.setText(scorePoints);
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
}
