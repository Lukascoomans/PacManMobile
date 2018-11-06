package be.pxl.pacmanapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NameSubmitFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_namesubmit, container, false);
        editButton(view);
        return view;
    }

    private void editButton(View view) {
        if(view != null){
            final EditText nameTextBox = view.findViewById(R.id.name_textbox);
            Button submitButton = view.findViewById(R.id.name_submit_button);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameTextBox.getText().toString();
                    if (!name.isEmpty()){

                    }
                }
            });
        }
    }
}
