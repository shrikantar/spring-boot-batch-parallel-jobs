package com.ctcs.paralleljobs.processors;

import com.ctcs.paralleljobs.model.Invoice;
import com.ctcs.paralleljobs.model.Product;
import org.springframework.batch.item.ItemProcessor;

import java.util.Calendar;


public class InvoiceProcessor implements ItemProcessor<Product, Invoice> {
    @Override
    public Invoice process(Product product) throws Exception {
        Invoice invoice = new Invoice();
        invoice.setId(product.getId());
        invoice.setInvoiceDate(new java.sql.Timestamp(System.currentTimeMillis()));
        return invoice;
    }
}
