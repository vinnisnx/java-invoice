package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private HashMap<Product, Integer> products = new HashMap<>();

    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        products.put(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (quantity == 0) throw new IllegalArgumentException("Quantity cannot be zero");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        products.put(product, quantity);

    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            subtotal = subtotal.add(product.getPrice().multiply(new BigDecimal(products.get(product))));
        }
        return subtotal;
    }

    public BigDecimal getTax() {
        BigDecimal tax = BigDecimal.ZERO;

        for (Product product : products.keySet()) {
            tax = tax.add(product.getPrice().multiply(new BigDecimal(products.get(product))).multiply(product.getTaxPercent()));
        }
        return tax;
    }

    public BigDecimal getTotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            subtotal = subtotal.add(product.getPriceWithTax().multiply(new BigDecimal(products.get(product))));
        }
        return subtotal;
    }
}
