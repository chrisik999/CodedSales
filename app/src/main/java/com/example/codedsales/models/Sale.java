package com.example.codedsales.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Sale implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Fields">
    private Long id;

    private Double total;

    private String items;

    private String business;

    private String initiator;

    private int discount;

    private Double amount;

    private String description;

    private String code;

    private Timestamp date;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    //new Sale constructor
    public Sale(Double total, String items, String business, String initiator, int discount, Double amount, String description, String code) {
        this.total = total;
        this.items = items;
        this.business = business;
        this.initiator = initiator;
        this.discount = discount;
        this.amount = amount;
        this.description = description;
        this.code = code;
    }

    //Sales details from database constructor


    public Sale(Long id, Double total, String items, String business, String initiator, int discount, Double amount, String description, String code, Timestamp date) {
        this.id = id;
        this.total = total;
        this.items = items;
        this.business = business;
        this.initiator = initiator;
        this.discount = discount;
        this.amount = amount;
        this.description = description;
        this.code = code;
        this.date = date;
    }

    //Find Sale by id constructor
    public Sale(Long id) {
        this.id = id;
    }

    //Find Sale by initiator constructor
    public Sale(String initiator) {
        this.initiator = initiator;
    }

    //Empty sale constructor
    public Sale() {
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    //</editor-fold>

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", total=" + total +
                ", items='" + items + '\'' +
                ", business='" + business + '\'' +
                ", initiator='" + initiator + '\'' +
                ", discount=" + discount +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", date=" + date +
                '}';
    }
    //</editor-fold>

}
