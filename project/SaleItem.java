package project;

public class SaleItem{
    private long inventoryNum;
    private String name;
    private double unitPrice;
    private double quantity;
    private double totalPrice;

    public SaleItem(long inventoryNum, String name, double unitPrice, double quantity) {
        this.inventoryNum = inventoryNum;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = unitPrice * quantity;
    }

    public long getInventoryNum() {
        return inventoryNum;
    }

    public void setInventoryNum(long inventoryNum) {
        this.inventoryNum = inventoryNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
