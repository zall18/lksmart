package com.example.lksmart;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class invoice extends AppCompatActivity {

    File file, f;

    String tipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice);
        Intent intent = getIntent();
        LinearLayout linear = findViewById(R.id.linear);
        AppCompatButton save = findViewById(R.id.save);
        AppCompatButton share = findViewById(R.id.share);
        AppCompatButton selesai = findViewById(R.id.selesai);

        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), menu.class));
            }
        });


        try {

            JSONArray jsonArray = new JSONArray(intent.getExtras().getString("detail"));
            for (int i = 0; i < jsonArray.length(); i++){

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("product"));

                TextView textView = new TextView(this);
                textView.setText(jsonObject1.getString("name"));
                textView.setTextColor(Color.BLACK);
                textView.setPadding(60, 0, 60, 0);
                linearLayout.addView(textView);

                TextView textView2 = new TextView(this);
                textView2.setText(jsonObject1.getString("price"));
                textView2.setTextColor(Color.BLACK);
                textView2.setPadding(60, 0, 120, 0);
                linearLayout.addView(textView2);

                TextView textView3 = new TextView(this);
                textView3.setText(jsonObject.getString("qty"));
                textView3.setTextColor(Color.BLACK);
                textView3.setPadding(60, 0, 60, 0);
                linearLayout.addView(textView3);

                TextView textView4 = new TextView(this);
                textView4.setText(jsonObject.getString("subtotal"));
                textView4.setTextColor(Color.BLACK);
                textView4.setPadding(60, 0, 60, 0);
                linearLayout.addView(textView4);
                linear.addView(linearLayout);

            }
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setText("Total Bayar");
            textView.setTextColor(Color.BLACK);
            textView.setPadding(120, 0, 120, 0);
            linearLayout.addView(textView);



            TextView textView1 = new TextView(this);
            textView1.setText("Rp. " + intent.getExtras().getString("total"));
            textView1.setTextColor(Color.BLACK);
            textView1.setPadding(120, 0, 120, 0);
            linearLayout.addView(textView1);
            linear.addView(linearLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipe == "image"){
                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                    intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(f)));
                    intent1.setType("image/png");
                    startActivity(Intent.createChooser(intent1, "Share file via.."));
                }else{
                    Uri pdfUri = FileProvider.getUriForFile(getApplicationContext(), "lksmart", f.getAbsoluteFile());
                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                    intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(pdfUri)));
                    intent1.setType("application/pdf");
                    startActivity(Intent.createChooser(intent1, "Share file via.."));

                }
            }
        });


    }

    public void showDialog(){

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet);

        AppCompatButton pdf  = dialog.findViewById(R.id.pdfbutton);
        AppCompatButton image = dialog.findViewById(R.id.imagebutton);
        LinearLayout content = findViewById(R.id.content);

        content.setDrawingCacheEnabled(true);
        Bitmap bitmap = content.getDrawingCache();

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                Canvas canvas = page.getCanvas();
                canvas.drawBitmap(bitmap, 0, 0, null);
                pdfDocument.finishPage(page);
                try {
                    if(android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        file = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "lksmart" );
                        if(!file.exists()){
                            file.mkdir();
                        }

                        f = new File(file.getAbsoluteFile() + "/" + "invoice.pdf");
                        pdfDocument.writeTo(new FileOutputStream(f));
                        tipe = "PDF";

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(invoice.this, "Pdf berhasil disimpan", Toast.LENGTH_SHORT).show();
                pdfDocument.close();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        file = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "lksmart" );
                        if(!file.exists()){
                            file.mkdir();
                        }

                        f = new File(file.getAbsoluteFile() + "/" + "invoice.png");
                        FileOutputStream fileOutputStream = new FileOutputStream(f);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 10, fileOutputStream);
                        Toast.makeText(invoice.this, "Image berhasil disimpan", Toast.LENGTH_SHORT).show();
                        tipe = "image";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));




    }
}