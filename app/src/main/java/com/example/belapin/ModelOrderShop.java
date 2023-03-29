package com.example.belapin;

public class ModelOrderShop {

    String cost, orderKe, pesananId, statusOrder, waktoOrder, yangOrder;

    public ModelOrderShop() {
    }

    public ModelOrderShop(String cost, String orderKe, String pesananId, String statusOrder, String waktoOrder, String yangOrder) {
        this.cost = cost;
        this.orderKe = orderKe;
        this.pesananId = pesananId;
        this.statusOrder = statusOrder;
        this.waktoOrder = waktoOrder;
        this.yangOrder = yangOrder;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getOrderKe() {
        return orderKe;
    }

    public void setOrderKe(String orderKe) {
        this.orderKe = orderKe;
    }

    public String getPesananId() {
        return pesananId;
    }

    public void setPesananId(String pesananId) {
        this.pesananId = pesananId;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public String getWaktoOrder() {
        return waktoOrder;
    }

    public void setWaktoOrder(String waktoOrder) {
        this.waktoOrder = waktoOrder;
    }

    public String getYangOrder() {
        return yangOrder;
    }

    public void setYangOrder(String yangOrder) {
        this.yangOrder = yangOrder;
    }
}
