package com.example.lksmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SharedPreferences session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        EditText username = findViewById(R.id.username_input);
        EditText password = findViewById(R.id.password_input);
        AppCompatButton login = findViewById(R.id.login_button);
        TextView register = findViewById(R.id.daftar_text);
        connection connection = new connection();
        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        ProgressBar progressBar = findViewById(R.id.loader_login);
        LinearLayout loginContent = findViewById(R.id.login_content);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), daftar.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().length() == 0){
                    username.setError("Usernamw wajib disii");
                }else if(password.getText().length() == 0){
                    password.setError("Password wajib diisi");
                }else{

                    progressBar.setVisibility(View.VISIBLE);
                    loginContent.setVisibility(View.GONE);

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, connection.getUrl() + "api/login", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {

                                JSONObject jsonObject = new JSONObject(s);
                                if(jsonObject.getString("status").contains("200")){
                                    editor.putString("token", jsonObject.getString("data"));
                                    editor.commit();
                                    Toast.makeText(MainActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), menu.class));
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    loginContent.setVisibility(View.VISIBLE);

                                    Toast.makeText(MainActivity.this, "Periksa kembli username dan password anda", Toast.LENGTH_SHORT).show();
                                }



                            } catch (Exception e) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                Toast.makeText(MainActivity.this, "Periksa kembli username dan password anda", Toast.LENGTH_SHORT).show();
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

                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> param = new HashMap<>();
                            param.put("username", username.getText().toString());
                            param.put("password", password.getText().toString());
                            return param;
                        }
                    };
                    requestQueue.getCache().clear();
                    requestQueue.add(stringRequest);

                }

            }
        });
    }
}