package be.pxl.pacmanapp;

import android.content.Context;
import android.os.AsyncTask;
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
        requestQueue= Volley.newRequestQueue(context);;
    }


    public Object callDatabase() {
        String url ="http://web.stanford.edu/class/cs221/leaderboardData.js";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        jsonResponse=response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

// Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
        return jsonResponse;
    }
}
