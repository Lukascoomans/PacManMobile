package be.pxl.pacmanapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {
    private int count = 0;
    private boolean gameBusy = false;
    private CountDownTimer prepareTimer;
    private CountDownTimer gameTimer;

    private TextView timerTextView;
    private TextView countTextView;
    private View layoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        timerTextView = this.findViewById(R.id.timer_textview);
        countTextView = this.findViewById(R.id.count_textview);
        layoutView = this.findViewById(R.id.play_activity_layout);
        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameBusy){
                    count++;
                    countTextView.setText(String.valueOf(count));
                }
            }
        });

        gameTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int currSecond = (int) millisUntilFinished / 1000;
                switch(currSecond){
                    case 0:
                        timerTextView.setText("Finish!");
                        break;
                    default:
                        timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
                        break;

                }
            }

            public void onFinish() {
                gameBusy = false;
            }
        };

        prepareTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                int currSecond = (int) millisUntilFinished / 1000;
                switch(currSecond){
                    case 1:
                        countTextView.setText("Go!");
                        break;
                    case 0:
                        countTextView.setText("");
                        break;
                    default:
                        countTextView.setText(String.valueOf(currSecond - 1));
                        break;
                }
            }

            public void onFinish() {
                gameBusy = true;
                Log.i("GameTimerStart", "Starting the gameTimer.");
                gameTimer.start();
                countTextView.setText(String.valueOf(count));
            }
        };

        Log.i("PrepareTimerStart", "Starting the prepareTimer.");
        prepareTimer.start();
    }
}
