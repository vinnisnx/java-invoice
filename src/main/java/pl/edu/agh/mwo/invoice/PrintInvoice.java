package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class PrintInvoice implements Printer {
    private final Invoice invoice;

    public PrintInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void print() {
        Set<Map.Entry<Product, Integer>> entrySet = invoice.getProductsEntrySet();
        int maxNameSize = 26;
        int maxDistanceNameQuantity = 37;
        int maxDistanceQuantityPrice = 20;
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

            if (name.length() > maxNameSize) name = name.substring(0, maxNameSize);
            int distanceNameQuantity = Math.max(1, maxDistanceNameQuantity - name.length() - quantity.length());
            int distanceQuantityPrice = Math.max(1, maxDistanceQuantityPrice - price.length());

            sb.append(name).append(" ".repeat(distanceNameQuantity)).append(quantity)
                    .append(" ".repeat(distanceQuantityPrice)).append(price).append("\n");
        }

        String finalPrice = invoice.getGrossTotal().toString();
        sb.append("\n").append("Total product types: ").
                append(" ".repeat(Math.max(1, 10-String.valueOf(entrySet.size()).length())))
                .append(entrySet.size()).append("\n");
        sb.append("Total quantity: ").append(" ".repeat(Math.max(1, 15-String.valueOf(totalQuantity).length())))
                .append(totalQuantity).append("\n");
        sb.append("Total price: ").append(" ".repeat(Math.max(1, 18-finalPrice.length())))
                .append(finalPrice).append("\n");

        System.out.println(sb);
    }
}
