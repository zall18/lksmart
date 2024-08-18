package com.example.lksmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    SharedPreferences session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        TextView username = findViewById(R.id.username);
        TextView telepon = findViewById(R.id.telpon);
        TextView alamaat = findViewById(R.id.alamat);
        LinearLayout logout = findViewById(R.id.logout);
        connection connection = new connection();
        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        LinearLayout menuselect = findViewById(R.id.menuselect);
        ProgressBar progressBar = findViewById(R.id.loader_profile);
        LinearLayout content = findViewById(R.id.profile_content);

        menuselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), menu.class));
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, connection.getUrl() + "api/user", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!jsonObject.getString("id").isEmpty()) {

                        username.setText(jsonObject.getString("username"));
                        alamaat.setText(jsonObject.getString("address"));
                        telepon.setText(jsonObject.getString("name"));
                        progressBar.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + session.getString("token", ""));
                return header;
            }
        };
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, connection.getUrl() + "api/logout", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getString("status").contains("200")) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }){

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded;charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("Authorization", "Bearer " + session.getString("token", ""));
                        return header;
                    }


                };
                requestQueue1.getCache().clear();
                requestQueue1.add(stringRequest1);
            }
        });
    }
}