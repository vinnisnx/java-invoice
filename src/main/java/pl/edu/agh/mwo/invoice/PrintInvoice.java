package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class PrintInvoice implements Printer {
    private static final int MAX_NAME_SIZE = 26;
    private static final int MAX_DISTANCE_NAME_QUANTITY = 37;
    private static final int MAX_DISTANCE_QUANTITY_PRICE = 20;

    private static final int MAX_DISTANCE_INVOICE_TOTAL_TYPES = 10;
    private static final int MAX_DISTANCE_INVOICE_TOTAL_QUANTITY = 15;
    private static final int MAX_DISTANCE_INVOICE_TOTAL_PRICE = 18;

    private final Invoice invoice;

    public PrintInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void print() {
        Set<Map.Entry<Product, Integer>> entrySet = invoice.getProductsEntrySet();
        int totalQuantity = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("Invoice Number: ").append(invoice.getInvoiceNumber()).append("\n");

        for (Map.Entry<Product, Integer> entry : entrySet) {
            String name = entry.getKey().getName();
            totalQuantity += entry.getValue();
            String quantity = String.valueOf(entry.getValue());
            BigDecimal numPrice = new BigDecimal(entry.getKey().getPriceWithTax().toString());
            String price = numPrice.multiply(BigDecimal.valueOf(entry.getValue()))
                    .setScale(2, BigDecimal.ROUND_HALF_UP).toString();

            if (name.length() > MAX_NAME_SIZE) { name = name.substring(0, MAX_NAME_SIZE); }
            int distanceNameQuantity =
                    Math.max(1, MAX_DISTANCE_NAME_QUANTITY - name.length() - quantity.length());
            int distanceQuantityPrice = Math.max(1, MAX_DISTANCE_QUANTITY_PRICE - price.length());

            sb.append(name).append(" ".repeat(distanceNameQuantity)).append(quantity)
                    .append(" ".repeat(distanceQuantityPrice)).append(price).append("\n");
        }

        String finalPrice = invoice.getGrossTotal().toString();

        sb.append("\n").append("Total product types: ").
                append(" ".repeat(Math.max(1, MAX_DISTANCE_INVOICE_TOTAL_TYPES -
                        String.valueOf(entrySet.size()).length())))
                .append(entrySet.size()).append("\n");
        sb.append("Total quantity: ")
                .append(" ".repeat(Math.max(1, MAX_DISTANCE_INVOICE_TOTAL_QUANTITY -
                        String.valueOf(totalQuantity).length())))
                .append(totalQuantity).append("\n");
        sb.append("Total price: ").append(" ".repeat(Math.max(1, MAX_DISTANCE_INVOICE_TOTAL_PRICE -
                        finalPrice.length()))).append(finalPrice).append("\n");

        System.out.println(sb);
    }
}
