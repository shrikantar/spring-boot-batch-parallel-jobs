package com.ctcs.paralleljobs.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;


public class Invoice implements Serializable {

    private Long id;
    private Timestamp invoiceDate;

    public Invoice(){
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Timestamp invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
