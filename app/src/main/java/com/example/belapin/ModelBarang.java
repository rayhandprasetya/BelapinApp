package com.example.belapin;

public class ModelBarang {
    private String barangId, barangJudul, barangDeskripsi, barangKategori,
            barangKuantiti, hargaAsli, barangIcon, hargaDiskon, hargaDiskonNote,
            diskonTersedia, timestamp, uid;

    public ModelBarang() {
    }

    public ModelBarang(String barangId, String barangJudul, String barangDeskripsi, String barangKategori,
                       String barangKuantiti, String hargaAsli, String barangIcon, String hargaDiskon,
                       String hargaDiskonNote, String diskonTersedia, String timestamp, String uid) {

        this.barangId = barangId;
        this.barangJudul = barangJudul;
        this.barangDeskripsi = barangDeskripsi;
        this.barangKategori = barangKategori;
        this.barangKuantiti = barangKuantiti;
        this.hargaAsli = hargaAsli;
        this.barangIcon = barangIcon;
        this.hargaDiskon = hargaDiskon;
        this.hargaDiskonNote = hargaDiskonNote;
        this.diskonTersedia = diskonTersedia;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getBarangId() {
        return barangId;
    }

    public void setBarangId(String barangId) {
        this.barangId = barangId;
    }

    public String getBarangJudul() {
        return barangJudul;
    }

    public void setBarangJudul(String barangJudul) {
        this.barangJudul = barangJudul;
    }

    public String getBarangDeskripsi() {
        return barangDeskripsi;
    }

    public void setBarangDeskripsi(String barangDeskripsi) {
        this.barangDeskripsi = barangDeskripsi;
    }

    public String getBarangKategori() {
        return barangKategori;
    }

    public void setBarangKategori(String barangKategori) {
        this.barangKategori = barangKategori;
    }

    public String getBarangKuantiti() {
        return barangKuantiti;
    }

    public void setBarangKuantiti(String barangKuantiti) {
        this.barangKuantiti = barangKuantiti;
    }

    public String getHargaAsli() {
        return hargaAsli;
    }

    public void setHargaAsli(String hargaAsli) {
        this.hargaAsli = hargaAsli;
    }

    public String getBarangIcon() {
        return barangIcon;
    }

    public void setBarangIcon(String barangIcon) {
        this.barangIcon = barangIcon;
    }

    public String getHargaDiskon() {
        return hargaDiskon;
    }

    public void setHargaDiskon(String hargaDiskon) {
        this.hargaDiskon = hargaDiskon;
    }

    public String getHargaDiskonNote() {
        return hargaDiskonNote;
    }

    public void setHargaDiskonNote(String hargaDiskonNote) {
        this.hargaDiskonNote = hargaDiskonNote;
    }

    public String getDiskonTersedia() {
        return diskonTersedia;
    }

    public void setDiskonTersedia(String diskonTersedia) {
        this.diskonTersedia = diskonTersedia;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
