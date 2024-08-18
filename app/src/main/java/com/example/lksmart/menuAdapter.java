package com.example.lksmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class menuAdapter extends ArrayAdapter<menuModel> implements Filterable {

    private Context context;
    private int resourse;
    private List<menuModel> menuModelList, menuList;
    private List<String> listid, listnm, listhg, listjml;
    TextView total;
    int totalbayar= 0;
    Filterfilter ff;


    public menuAdapter(@NonNull Context context, int resource, List<menuModel> menuModelList, List<String> listid, List<String> listnm, List<String> listhg, List<String> listjml, TextView total) {
        super(context, resource, menuModelList);

        this.context = context;
        this.resourse = resource;
        this.menuList = menuModelList;
        this.menuModelList = menuModelList;
        this.listid = listid;
        this.listnm = listnm;
        this.listhg = listhg;
        this.listjml = listjml;
        this.total = total;
    }

    class Filterfilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0){
                constraint = constraint.toString().toUpperCase();
                ArrayList<menuModel> arrayList = new ArrayList<>();
                for (int i = 0; i < menuList.size(); i++){

                    if(menuList.get(i).getNama().toString().toUpperCase().contains(constraint)){
                        arrayList.add(new menuModel(menuList.get(i).getId(), menuList.get(i).getNama(), menuList.get(i).getHarga(), menuList.get(i).getGambar()));
                    }


                }
                filterResults.values = arrayList;
            }else{
                filterResults.values = menuModelList;
            }
            return filterResults;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            menuList = (ArrayList<menuModel>) results.values;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(ff == null){
            ff = new Filterfilter();
        }
        return ff;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(resourse, null, false);
        }

        TextView nama = convertView.findViewById(R.id.nama_barang);
        TextView harga = convertView.findViewById(R.id.harga_barang);
        TextView jml = convertView.findViewById(R.id.jml);
        ImageView image = convertView.findViewById(R.id.image_barang);
        ImageView add = convertView.findViewById(R.id.add);
        ImageView remove = convertView.findViewById(R.id.remove);

        menuModel menuModel = menuList.get(position);
        nama.setText(menuModel.getNama());
        harga.setText("Rp. " + menuModel.getHarga());
        Picasso.get().load(menuModel.getGambar()).into(image);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jml.setText(String.valueOf(Integer.parseInt(jml.getText().toString()) + 1  ));

                if(listid.contains(menuModel.getId())){
                    int idx = listid.indexOf(menuModel.getId());

                    listhg.set(idx, menuModel.getHarga());
                    listjml.set(idx, jml.getText().toString());
                }else{
                    listid.add(menuModel.getId());
                    listnm.add(menuModel.getNama());
                    listhg.add(menuModel.getHarga());
                    listjml.add(jml.getText().toString());

                }

                totalbayar += Integer.parseInt(menuModel.getHarga());
                total.setText(format_rupiah(Double.parseDouble(String.valueOf(totalbayar))));

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(jml.getText().toString()) > 0){
                    jml.setText(String.valueOf(Integer.parseInt(jml.getText().toString()) - 1  ));

                    if(listid.contains(menuModel.getId())){
                        int idx = listid.indexOf(menuModel.getId());

                        listhg.set(idx, menuModel.getHarga());
                        listjml.set(idx, jml.getText().toString());
                    }else{
                        listid.add(menuModel.getId());
                        listnm.add(menuModel.getNama());
                        listhg.add(menuModel.getHarga());
                        listjml.add(jml.getText().toString());

                    }

                    totalbayar -= Integer.parseInt(menuModel.getHarga());
                    total.setText(format_rupiah(Double.parseDouble(String.valueOf(totalbayar))));
                }

            }
        });

        return convertView;
    }

    public String format_rupiah(double nummber){

        Locale locale = new Locale("in", "ID");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return numberFormat.format(nummber);

    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
