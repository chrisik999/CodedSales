package com.example.codedsales.models;

import java.io.Serializable;
import java.util.Date;

public class Receipt implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="Fields">
    private Long id;

    private String receiptCode;

    private Double total;

    private Double discount;

    private String business;

    private String sales;

    private Date date;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public Receipt() {
    }

    //New Receipt Constructor
    public Receipt(String receiptId, Double total, Double discount, String business, String sales) {
        this.receiptCode = receiptId;
        this.total = total;
        this.discount = discount;
        this.business = business;
        this.sales = sales;
    }

    //Get Receipts from database constructors
    public Receipt(Long id, String receiptCode, Double total, Double discount, String business, String sales, Date date) {
        this.id = id;
        this.receiptCode = receiptCode;
        this.total = total;
        this.discount = discount;
        this.business = business;
        this.sales = sales;
        this.date = date;
    }

    public Receipt(String business) {
        this.business = business;
    }

    public Receipt(Long id) {
        this.id = id;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters And Setters">
    public Long getId() {
        return id;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double Total) {
        this.total = Total;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public Date getDate() {
        return date;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="To String Method">
    @Override
    public String toString() {
        return "Receipt{" + "id=" + id + ", receiptId=" + receiptCode + ", Total=" + total + ", discount=" + discount + ", business=" + business + ", sales=" + sales + '}';
    }
    //</editor-fold>

}
