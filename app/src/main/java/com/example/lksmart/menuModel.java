package com.example.lksmart;

public class menuModel {

    private String id, nama, harga, gambar;

    public menuModel(String id, String nama, String harga, String gambar){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.gambar = gambar;

    }


    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getHarga() {
        return harga;
    }

    public String getGambar() {
        return gambar;
    }
}
