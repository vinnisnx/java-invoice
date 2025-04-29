package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.*;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Invoice invoice = new Invoice();
        invoice.addProduct(new TaxFreeProduct("Baton", new BigDecimal("1.04")), 3);
        Product water = new TaxFreeProduct("Woda", new BigDecimal("1.99"));
        invoice.addProduct(water);
        invoice.addProduct(water);
        invoice.addProduct(new DairyProduct("Chleb", new BigDecimal("3.54")), 2);
        invoice.addProduct(new OtherProduct("Kamen", new BigDecimal("3.50")), 5);
        invoice.addProduct(new BottleOfWine("Wine", new BigDecimal("4.99")), 4);

        Printer printer = new PrintInvoice(invoice);
        printer.print();
    }
}
