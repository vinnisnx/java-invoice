package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.*;

import java.math.BigDecimal;

public class Main {
    private static final int TEST_VALUE_2 = 2;
    private static final int TEST_VALUE_3 = 3;
    private static final int TEST_VALUE_4 = 4;
    private static final int TEST_VALUE_5 = 5;

    public static void main(String[] args) {
        Invoice invoice = new Invoice();
        invoice.addProduct(new TaxFreeProduct("Baton", new BigDecimal("1.04"))
                , TEST_VALUE_3);
        Product water = new TaxFreeProduct("Woda", new BigDecimal("1.99"));
        invoice.addProduct(water);
        invoice.addProduct(water);
        invoice.addProduct(new DairyProduct("Chleb", new BigDecimal("3.54"))
                , TEST_VALUE_2);
        invoice.addProduct(new OtherProduct("Kamen", new BigDecimal("3.50"))
                , TEST_VALUE_5);
        invoice.addProduct(new BottleOfWine("Wine", new BigDecimal("4.99"))
                , TEST_VALUE_4);

        Printer printer = new PrintInvoice(invoice);
        printer.print();
    }
}
