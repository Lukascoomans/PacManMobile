package be.pxl.pacmanapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import be.pxl.pacmanapp.Activities.PlayActivity;
import be.pxl.pacmanapp.R;

public class StartButtonFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startbutton, container, false);
        editButton(view);
        return view;
    }

    private void editButton(View view) {
        if(view != null){
            Button button = view.findViewById(R.id.start_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PlayActivity) getActivity()).startGame();
                }
            });
        }
    }
}
