package project;

import java.time.LocalDate;

public class PerishableItem extends Item {
    private LocalDate expirDate;

    public PerishableItem(long inventoryNum, String name, int amt, double unitPrice, int minimumStock, LocalDate expirDate) {
        super(inventoryNum, name, amt, unitPrice, minimumStock);
        this.expirDate = expirDate;
    }

    public LocalDate getExpirDate() {
        return expirDate;
    }

    public void setExpirDate(LocalDate expirDate) {
        this.expirDate = expirDate;
    }

    @Override
    public void depleteStock(int amt) {
        if (amt <= getAmtInStock()) {
            setAmtInStock(getAmtInStock() - amt);
            if (getAmtInStock() < getMinimumStock()) {
                System.out.println("Warning: Stock below minimum level. Consider restocking.");
            }
            if (LocalDate.now().plusDays(2).isAfter(expirDate)) {
                System.out.println("Warning: Item is expired or will expire within 2 days. Consider removing from inventory.");
            }
        } else {
            System.out.println("Insufficient stock to deplete the requested amount.");
        }
    }


    @Override
    public double calculatePrice() {
        if (LocalDate.now().isAfter(expirDate)) {
            return (getUnitPrice() + (getUnitPrice() * 0.17)) * 0.5;
        } else {
            return getUnitPrice() + (getUnitPrice() * 0.17);
        }
    }
}
