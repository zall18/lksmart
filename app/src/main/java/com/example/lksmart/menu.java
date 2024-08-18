package com.example.lksmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class menu extends AppCompatActivity {

    SharedPreferences session;

    menuAdapter menuAdapter;
    List<String> listid, listnm, listhg, listjml;

    JSONArray jsonArray;
    JSONObject jsonObject2;
    JSONObject jsonObject3;
    int total_bayar = 0;
    int total_beli = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ListView listView = findViewById(R.id.list_menu);
        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        listid = new ArrayList<>();
        listnm = new ArrayList<>();
        listhg = new ArrayList<>();
        listjml = new ArrayList<>();
        connection connection = new connection();
        ArrayList<menuModel> models = new ArrayList<>();
        TextView total = findViewById(R.id.total);
        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        AppCompatButton bayar = findViewById(R.id.bayar);
        SearchView searc = findViewById(R.id.search);
        LinearLayout profileselect = findViewById(R.id.profile_select);
        ProgressBar progressBar = findViewById(R.id.loader_menu);
        LinearLayout menuContent = findViewById(R.id.menu_content);

        profileselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        menuContent.setVisibility(View.GONE);
        
        
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, connection.getUrl() + "api/products", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                try {

                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = null;
                    if (jsonObject.getString("status").contains("200")) {

                        jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            models.add(new menuModel(jsonObject1.getString("id"), jsonObject1.getString("name"), jsonObject1.getString("price"), jsonObject1.getString("image")));
                        }

                        menuAdapter = new menuAdapter(getApplicationContext(), R.layout.menu_item, models, listid, listnm, listhg, listjml, total);
                        listView.setAdapter(menuAdapter);
                        progressBar.setVisibility(View.GONE);
                        menuContent.setVisibility(View.VISIBLE);

                        searc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                menuAdapter.getFilter().filter(newText);
                                return false;
                            }
                        });

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

        bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listid.size() != 0){
                    progressBar.setVisibility(View.VISIBLE);
                    menuContent.setVisibility(View.GONE);
                    try {
                        jsonArray = new JSONArray();
                        for (int i = 0; i < listid.size(); i++){

                            total_beli += Integer.parseInt(listjml.get(i));
                            total_bayar += Integer.parseInt(listjml.get(i)) * Integer.parseInt(listhg.get(i));

                            jsonObject2 = new JSONObject();
                            jsonObject2.put("product_id", listid.get(i));
                            jsonObject2.put("qty", listjml.get(i));
                            jsonObject2.put("subtotal", String.valueOf(Integer.parseInt(listjml.get(i)) * Integer.parseInt(listhg.get(i)) ));

                            jsonArray.put(jsonObject2);



                        }

                        Map<String, Object> param = new HashMap<>();
                        param.put("qty_total", total_beli);
                        param.put("price_total", total_bayar);
                        param.put("items", jsonArray);

                        jsonObject3 = new JSONObject(param);

                        RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, connection.getUrl() + "api/store-invoice", jsonObject3, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {

                                try {
                                    if (jsonObject.getString("status").contains("201")) {

                                        JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                                        Intent intent = new Intent(getApplicationContext(), invoice.class);
                                        intent.putExtra("total", String.valueOf(total_bayar));
                                        intent.putExtra("detail", jsonObject1.getString("invoice_details").toString());
                                        startActivity(intent);

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
                                header.put("Authorization", "Bearer " + session.getString("token", ""));
                                header.put("Content-Type", "application/json");
                                return header;
                            }
                        };
                        requestQueue1.getCache().clear();
                        requestQueue1.add(jsonObjectRequest);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(menu.this, "Pilih barang minimal satu!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}