package com.example.lksmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Phaser;

public class daftar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar);
        EditText username =  findViewById(R.id.un_input);
        EditText nama = findViewById(R.id.nl_input);
        EditText alammat =findViewById(R.id.alamat_input);
        EditText pass = findViewById(R.id.pass_input);
        EditText kpass = findViewById(R.id.kpass_input);
        AppCompatButton register = findViewById(R.id.daftar_button);
        TextView loginPage = findViewById(R.id.login_text);
        connection connection = new connection();

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nama.getText().length() == 0){
                    nama.setError("Nama wajib diisi");
                }else if(alammat.getText().length() == 0){
                    alammat.setError("Alaamat wajib diisi!");
                }else if(username.getText().length() == 0){
                    username.setError("Username wajib diisi");
                }else if(pass.getText().length() == 0){
                    pass.setError("Password wajib diisi");
                }else if(kpass.getText().length() == 0){
                    kpass.setError("Konfirmasi passwrod wajib diisi");
                }else{

                    if(pass.getText().toString().equals(kpass.getText().toString())){

                        Map<String, String> param = new HashMap<>();
                        param.put("username", username.getText().toString());
                        param.put("name", nama.getText().toString());
                        param.put("address", alammat.getText().toString());
                        param.put("password", pass.getText().toString());
                        param.put("password_confirmation", kpass.getText().toString());

                        JSONObject jsonObject = new JSONObject(param);

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, connection.getUrl() + "api/register", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {

                                try {
                                    if (jsonObject.getString("status").contains("201")) {

                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        Toast.makeText(daftar.this, "Daftar berhasil!, silahkan login", Toast.LENGTH_SHORT).show();

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
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> header = new HashMap<>();
                                header.put("Content-Type", "application/json");;
                                return header;
                            }
                        };
                        requestQueue.getCache().clear();
                        requestQueue.add(jsonObjectRequest);

                    }else{
                        Toast.makeText(daftar.this, "Konfirmasi password tidak sesuai!", Toast.LENGTH_SHORT).show();
                        kpass.setError("Konfirmasi password tidak sesuai!");
                    }

                }

            }
        });
    }
}