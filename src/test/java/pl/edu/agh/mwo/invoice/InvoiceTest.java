package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.*;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testInvoiceNumberIsStandardLength() {
        Assert.assertEquals(22, invoice.getInvoiceNumber().length());
    }

    @Test
    public void testInvoiceNameRandomPartIsBetweenRange() {
        String number = invoice.getInvoiceNumber().substring(18);
        int num = Integer.parseInt(number);
        Assert.assertTrue(num >= 1000 && num <= 9999);
    }

    @Test
    public void testInvoiceBeginIsAlwaysSame() {
        Assert.assertEquals("INV", invoice.getInvoiceNumber().substring(0, 3));
    }

    @Test
    public void testInvoiceDataFormatIsAlwaysCorrect() {
        int year = Integer.parseInt(invoice.getInvoiceNumber().substring(3, 5));
        int month = Integer.parseInt(invoice.getInvoiceNumber().substring(5, 7));
        int day = Integer.parseInt(invoice.getInvoiceNumber().substring(7, 9));
        int hour = Integer.parseInt(invoice.getInvoiceNumber().substring(9, 11));
        int minute = Integer.parseInt(invoice.getInvoiceNumber().substring(11, 13));
        int second = Integer.parseInt(invoice.getInvoiceNumber().substring(13, 15));
        int millisecond = Integer.parseInt(invoice.getInvoiceNumber().substring(15, 18));
        Assert.assertTrue(year >= 0 && year <= 99);
        Assert.assertTrue(month >= 0 && month <= 12);
        Assert.assertTrue(day >= 0 && day <= 31);
        Assert.assertTrue(hour >= 0 && hour <= 23);
        Assert.assertTrue(minute >= 0 && minute <= 59);
        Assert.assertTrue(second >= 0 && second <= 59);
        Assert.assertTrue(millisecond >= 0 && millisecond <= 999);
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        // net price: 15.56
        invoice.addProduct(new FuelCanister("Fuel", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("325.56"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        // tax: 3.5788 = 3.58
        invoice.addProduct(new FuelCanister("Fuel", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("13.88"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200.03
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200.03")));
        // price with tax: 107.9892 = 107.99
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("99.99")));
        // price with tax: 12.2877 = 12.29
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("9.99")));
        // price with tax 21.5988 = 21.60
        invoice.addProduct(new FuelCanister("Fuel", new BigDecimal("12")));

        Assert.assertThat(new BigDecimal("341.91"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        // 30x wine - price: 196.8
        invoice.addProduct(new BottleOfWine("Bottle of Wine", new BigDecimal("1")), 30);
        Assert.assertThat(new BigDecimal("246.8"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.4324 = 32.43
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10.01")), 3);
        // 1000x pinezka - price with tax: 12.2877 = 12.29
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 999);
        // 12x wine - price with tax: 303.318 = 303.32
        invoice.addProduct(new BottleOfWine("Wine", new BigDecimal("14.99")), 12);
        Assert.assertThat(new BigDecimal("358.04"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testAddingSameProductSingleQuantity() {
        Product testProduct = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(testProduct);
        invoice.addProduct(testProduct);

        Assert.assertEquals(2, (long)invoice.getProducts().get(testProduct));
    }

    @Test
    public void testAddingSameProductsMultipleQuantity() {
        Product testProduct1 = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        Product testProduct2 = new DairyProduct("Milk", new BigDecimal("15"));
        invoice.addProduct(testProduct1, 10);
        invoice.addProduct(testProduct1, 5);
        invoice.addProduct(testProduct2, 13);
        invoice.addProduct(testProduct2, 7);

        Assert.assertEquals(15, (long)invoice.getProducts().get(testProduct1));
        Assert.assertEquals(20, (long)invoice.getProducts().get(testProduct2));
    }

    @Test
    public void testProductsWithExciseFixedTaxValue() {
        Product wine = new BottleOfWine("Wine", new BigDecimal("10"));
        Product gas = new FuelCanister("Gasoline", new BigDecimal("15"));

        Assert.assertThat(new BigDecimal("5.56"), Matchers.comparesEqualTo(wine.getExcise()));
        Assert.assertThat(new BigDecimal("5.56"), Matchers.comparesEqualTo(gas.getExcise()));
    }

    @Test
    public void testProductWithExciseHasProperNetValue() {
        Product wine = new BottleOfWine("Wine", new BigDecimal("10"));
        Assert.assertThat(new BigDecimal("15.56"), Matchers.comparesEqualTo(wine.getPrice()));
    }

    @Test
    public void testProductWithExciseCanCalculatePureNetValue() {
        Product wine = new BottleOfWine("Wine", new BigDecimal("10"));
        Assert.assertThat(new BigDecimal("10"),
                Matchers.comparesEqualTo(wine.getPrice().subtract(wine.getExcise())));
    }

    @Test
    public void testProductsWithExciseTotalPrice() {
        Product wine = new BottleOfWine("Wine", new BigDecimal("10"));
        Assert.assertThat(new BigDecimal("19.1388"), Matchers.comparesEqualTo(wine.getPriceWithTax()));
    }
}
