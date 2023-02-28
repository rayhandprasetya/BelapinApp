package com.example.belapin;

public class ModelBelanjaanDetail {

    private String bId, name, cost, harga, kuantiti;

    public ModelBelanjaanDetail() {
    }

    public ModelBelanjaanDetail(String bId, String name, String cost, String harga, String kuantiti) {
        this.bId = bId;
        this.name = name;
        this.cost = cost;
        this.harga = harga;
        this.kuantiti = kuantiti;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKuantiti() {
        return kuantiti;
    }

    public void setKuantiti(String kuantiti) {
        this.kuantiti = kuantiti;
    }
}
