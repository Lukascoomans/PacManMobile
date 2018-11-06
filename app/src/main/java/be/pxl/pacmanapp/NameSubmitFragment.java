package be.pxl.pacmanapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
                        sendNotification();
                    }
                }
            });
        }
    }

    private void sendNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), "Default")
                .setContentTitle("Thanks for playing!")
                .setContentText("We hope to see you soon.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }
}
