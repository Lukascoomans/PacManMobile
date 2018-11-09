package be.pxl.pacmanapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.view.View.OnClickListener;

import be.pxl.pacmanapp.Activities.PlayActivity;
import be.pxl.pacmanapp.R;

public class PlayButtonFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playbutton, container, false);
        editImageButton(view);
        return view;
    }

    private void editImageButton(View view){
        if (view != null){
            ImageButton imageButton = view.findViewById(R.id.play_button);
            imageButton.setImageResource(R.drawable.play_buttonwithborder);
            imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextActivity = new Intent(getActivity(), PlayActivity.class);
                    startActivity(nextActivity);
                }
            });
        }
    }
}
