package com.example.belapin;

public class ModelKeranjang {

    String id,bId, name, harga, cost, kuantiti;

    public ModelKeranjang() {
    }

    public ModelKeranjang(String id, String bId, String name, String harga, String cost, String kuantiti) {
        this.id = id;
        this.bId = bId;
        this.name = name;
        this.harga = harga;
        this.cost = cost;
        this.kuantiti = kuantiti;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getKuantiti() {
        return kuantiti;
    }

    public void setKuantiti(String kuantiti) {
        this.kuantiti = kuantiti;
    }
}
