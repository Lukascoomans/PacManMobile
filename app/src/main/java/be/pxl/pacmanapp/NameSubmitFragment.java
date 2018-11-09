package be.pxl.pacmanapp;

import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                    String country = getActivity().getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
                    if (!name.isEmpty()){

                        Bundle args = getArguments();
                        String score= args.getString("score", "0");
                        arrangeData(name,country,score);

                        sendNotification();


                    }


                }
            });
        }
    }
    private void arrangeData(final String name, final String country, final String score){

        DataBaseExecutor executor = new DataBaseExecutor(new DataBaseHelper(getActivity().getApplicationContext()));

        HighScoreModel model = new HighScoreModel("0",name,score,country);
        executor.WriteToDatabase(model);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        final  String url = "http://10.0.2.2:8080/PacMan";


        JSONObject json = new JSONObject();
        try {
            json.put("name",name);
            json.put("country",country);
            json.put("points",score);
            json.put("position","0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("d",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("e",error.toString());
            }
        });
        jsonObjectRequest.setTag("VACTIVITY");
        queue.add(jsonObjectRequest);



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
