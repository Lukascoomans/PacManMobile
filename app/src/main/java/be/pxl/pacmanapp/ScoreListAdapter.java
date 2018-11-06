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
    private boolean showCountry;

    public ScoreListAdapter(Cursor cursor, Context context, boolean showCountry){
        this.cursor = cursor;
        this.context = context;
        this.showCountry = showCountry;
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
        scorePosition = cursor.getString(cursor.getColumnIndex("position"));

        String scorePoints = null;
        scorePoints = cursor.getString(cursor.getColumnIndex("points"));

        String scoreName = null;
        scoreName = cursor.getString(cursor.getColumnIndex("name"));

        String scoreCountry = null;
        scoreCountry = cursor.getString(cursor.getColumnIndex("country"));

        viewHolder.positionTextView.setText(scorePosition);
        viewHolder.pointsTextView.setText(scorePoints);
        viewHolder.nameTextView.setText(scoreName);
        viewHolder.countryTextView.setText(scoreCountry);

        if(showCountry == false){
            viewHolder.HideCountryTextView();
        }
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
        public TextView positionTextView = null;
        public TextView pointsTextView  = null;
        public TextView nameTextView  = null;
        public TextView countryTextView = null;

        public ScoreListViewHolder(final View itemView) {
            super(itemView);
            positionTextView = itemView.findViewById(R.id.position);
            pointsTextView = itemView.findViewById(R.id.points);
            nameTextView = itemView.findViewById(R.id.name);
            countryTextView = itemView.findViewById(R.id.country);
        }

        public void HideCountryTextView(){
            countryTextView.setVisibility(View.GONE);
        }
    }
}
