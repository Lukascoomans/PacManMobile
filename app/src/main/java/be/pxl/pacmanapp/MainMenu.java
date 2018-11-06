package be.pxl.pacmanapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
