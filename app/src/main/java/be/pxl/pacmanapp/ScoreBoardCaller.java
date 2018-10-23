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

    private TextView mTextView;

    RequestQueue requestQueue ;

    public ScoreBoardCaller(Context context,View view) {
        this.mTextView = (TextView) view;
        requestQueue= Volley.newRequestQueue(context);;
    }


    public Object callDatabase() {



        String url ="https://swapi.co/api/films/2/";


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mTextView.setText("That didn't work!"+error.toString());
            }
        });

// Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
        return null;
    }
}
