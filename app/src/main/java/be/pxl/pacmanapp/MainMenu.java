package be.pxl.pacmanapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createNotificationChannel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        PlayButtonFragment playButton = new PlayButtonFragment();
        HighScoresButtonFragment highScoresButton = new HighScoresButtonFragment();
        LeaderBoardsButtonFragment leaderBoardsButtonFragment = new LeaderBoardsButtonFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.playbutton_container, playButton);
        transaction.add(R.id.highscoresbutton_container, highScoresButton);
        transaction.add(R.id.leaderboardsbutton_container, leaderBoardsButtonFragment);
        transaction.commit();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default";
            String description = "The default notification channel.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
