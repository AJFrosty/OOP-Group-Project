package project;

public abstract class Item {
    private long inventoryNum;
    private String name;
    private int amtInStock;
    private double unitPrice;
    private int minimumStock;

    public Item(long inventoryNum, String name, int amtInStock, double unitPrice, int minimumStock) {
        this.inventoryNum = inventoryNum;
        this.name = name;
        this.amtInStock = amtInStock;
        this.unitPrice = unitPrice;
        this.minimumStock = minimumStock;
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


    public int getAmtInStock() {
        return amtInStock;
    }


    public void setAmtInStock(int amtInStock) {
        this.amtInStock = amtInStock;
    }


    public double getUnitPrice() {
        return unitPrice;
    }


    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }


    public int getMinimumStock() {
        return minimumStock;
    }


    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }


    public void addStock(int amount) {
        if (amount > 0) {
            this.amtInStock += amount;
        }
    }
    
    public void displayItem() {
        System.out.println("Inventory Number: " + inventoryNum);
        System.out.println("Name: " + name);
        System.out.println("Amount in Stock: " + amtInStock);
        System.out.println("Unit Price: $" + unitPrice);
        System.out.println("Minimum Stock Level: " + minimumStock);
    }

    @Override
    public String toString() {
        String type = (this instanceof PerishableItem) ? "Perishable" : "Non-Perishable";
        return inventoryNum + " - " + name + " (" + type + ") | Stock: " + amtInStock;
    }

    public abstract void depleteStock(int amount);
    public abstract double calculatePrice();
}

