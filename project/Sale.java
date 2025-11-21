package project;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;


public class Sale {
    private LocalDate date;
    private long invoiceNumber;
    private ArrayList<SaleItem> saleItems;

    public Sale(ArrayList<SaleItem> saleItems) {
        this.date = LocalDate.now();
        this.invoiceNumber = new Random().nextLong();
        this.saleItems = saleItems;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public ArrayList<SaleItem> getSaleItems() {
        return saleItems;
    }

    public double calculateTotal() {
        double total = 0;
        for (SaleItem item : saleItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

}
