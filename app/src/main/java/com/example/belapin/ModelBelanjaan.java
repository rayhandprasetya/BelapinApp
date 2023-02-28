package com.example.belapin;

public class ModelBelanjaan {

    String pesananId, waktoOrder, statusOrder, cost, yangOrder, orderKe;

    public ModelBelanjaan() {

    }

    public ModelBelanjaan(String pesananId, String waktoOrder, String statusOrder, String cost, String yangOrder, String orderKe) {
        this.pesananId = pesananId;
        this.waktoOrder = waktoOrder;
        this.statusOrder = statusOrder;
        this.cost = cost;
        this.yangOrder = yangOrder;
        this.orderKe = orderKe;
    }

    public String getPesananId() {
        return pesananId;
    }

    public void setPesananId(String pesananId) {
        this.pesananId = pesananId;
    }

    public String getWaktoOrder() {
        return waktoOrder;
    }

    public void setWaktoOrder(String waktoOrder) {
        this.waktoOrder = waktoOrder;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getYangOrder() {
        return yangOrder;
    }

    public void setYangOrder(String yangOrder) {
        this.yangOrder = yangOrder;
    }

    public String getOrderKe() {
        return orderKe;
    }

    public void setOrderKe(String orderKe) {
        this.orderKe = orderKe;
    }
}
