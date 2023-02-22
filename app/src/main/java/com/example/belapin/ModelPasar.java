package com.example.belapin;

public class ModelPasar {

    private String uid, email, name, namaToko, phone, ongkir, negara, kota,
            provinsi, alamat, latitute, longitude, timestamp, tipeAkun, online, tokoBuka;

    public ModelPasar() {
    }

    public ModelPasar(String uid, String email, String name, String namaToko, String phone, String ongkir, String negara, String kota, String provinsi, String alamat, String latitute, String longitude, String timestamp, String tipeAkun, String online, String tokoBuka) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.namaToko = namaToko;
        this.phone = phone;
        this.ongkir = ongkir;
        this.negara = negara;
        this.kota = kota;
        this.provinsi = provinsi;
        this.alamat = alamat;
        this.latitute = latitute;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.tipeAkun = tipeAkun;
        this.online = online;
        this.tokoBuka = tokoBuka;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOngkir() {
        return ongkir;
    }

    public void setOngkir(String ongkir) {
        this.ongkir = ongkir;
    }

    public String getNegara() {
        return negara;
    }

    public void setNegara(String negara) {
        this.negara = negara;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLatitute() {
        return latitute;
    }

    public void setLatitute(String latitute) {
        this.latitute = latitute;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTipeAkun() {
        return tipeAkun;
    }

    public void setTipeAkun(String tipeAkun) {
        this.tipeAkun = tipeAkun;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getTokoBuka() {
        return tokoBuka;
    }

    public void setTokoBuka(String tokoBuka) {
        this.tokoBuka = tokoBuka;
    }
}
