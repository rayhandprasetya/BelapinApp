package com.example.belapin;

public class ModelRecipeProductAdmin {

    String barangId;
    String barangJudul;
    String hargaAsli;
    String barangIcon;
    String timestamp;
    String uid;
    boolean isChecked = false;

    public ModelRecipeProductAdmin() {

    }

    public ModelRecipeProductAdmin(String barangId, String barangJudul, String hargaAsli, String barangIcon, String timestamp, String uid, boolean isChecked) {
        this.barangId = barangId;
        this.barangJudul = barangJudul;
        this.hargaAsli = hargaAsli;
        this.barangIcon = barangIcon;
        this.timestamp = timestamp;
        this.uid = uid;
        this.isChecked = isChecked;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
