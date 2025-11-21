package project;

public class NonPerishableItem extends Item {
    private int warrantyPeriod;

    public NonPerishableItem(long inventoryNum, String name, int amt, double unitPrice, int minimumStock, int warrantyPeriod) {
        super(inventoryNum, name, amt, unitPrice, minimumStock);
        this.warrantyPeriod = warrantyPeriod;
    }

    public int getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(int warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    @Override
    public void depleteStock(int amt) {
        if (amt <= getAmtInStock()) {
            setAmtInStock(getAmtInStock() - amt);
            if (getAmtInStock() < getMinimumStock()) {
                System.out.println("Warning: Stock below minimum level. Consider restocking.");
            }
        } else {
            System.out.println("Insufficient stock to deplete the requested amount.");
        }
    }

    @Override
    public double calculatePrice() {
        return getUnitPrice() + (getUnitPrice() * 0.17);
    }
}
