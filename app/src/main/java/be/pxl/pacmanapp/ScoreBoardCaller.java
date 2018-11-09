package be.pxl.pacmanapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ScoreBoardCaller {

    RequestQueue requestQueue ;

    String jsonResponse= "";

    public ScoreBoardCaller(Context context) {
        requestQueue= Volley.newRequestQueue(context);
    }

/*
    public void callDatabase() {

        (TextView) findViewById(R.id.text);

        String url ="https://10.0.2.2:44343/PacMan";

      // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        (TextView) findViewById(R.id.text);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);

    }
    */
}
