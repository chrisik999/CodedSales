package com.example.codedsales.models;

import java.io.Serializable;
import java.sql.Date;

public class StockActivity implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Fields">
    private int id;

    private String initiator;

    private String item;

    private String business;

    private String type;

    private Date date;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public StockActivity(int id, String initiator, String item, String business, String type, Date date) {
        this.id = id;
        this.initiator = initiator;
        this.item = item;
        this.business = business;
        this.type = type;
        this.date = date;
    }

    public StockActivity(String initiator, String item, String business, String type) {
        this.initiator = initiator;
        this.item = item;
        this.business = business;
        this.type = type;
    }

    public StockActivity() {
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public int getId() {
        return id;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="To String Method">
    @Override
    public String toString() {
        return "StockActivity{" + "id=" + id + ", initiator=" + initiator + ", item=" + item + ", business=" + business + ", type=" + type + ", date=" + date + '}';
    }
    //</editor-fold>

}
