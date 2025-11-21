package project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class RunInventory {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Item> inventory = new ArrayList<>();
        ArrayList<Sale> sales = new ArrayList<>();
        boolean running = true;

        System.out.println("Welcome to the Inventory Management System.");

        while (running) {
            System.out.println("\nInventory Management Menu:");
            System.out.println("1. Add Perishable Item");
            System.out.println("2. Add Non-Perishable Item");
            System.out.println("3. Add to Stock");
            System.out.println("4. Deplete Stock");
            System.out.println("5. View Item Details");
            System.out.println("6. Input Sale");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String pname = scanner.next();
                    System.out.print("Enter inventory number: ");
                    long pinum = scanner.nextLong();
                    System.out.print("Enter amount: ");
                    int pamt = scanner.nextInt();
                    System.out.print("Enter unit price: ");
                    double pprice = scanner.nextDouble();
                    System.out.print("Enter minimum stock: ");
                    int pmin = scanner.nextInt();
                    System.out.print("Enter expiry date (YYYY-MM-DD): ");
                    LocalDate expDate = LocalDate.parse(scanner.next());
                    inventory.add(new PerishableItem(pinum, pname, pamt, pprice, pmin, expDate));
                    System.out.println("Perishable item added.");
                    break;

                case 2:
                    System.out.print("Enter name: ");
                    String npname = scanner.next();
                    System.out.print("Enter inventory number: ");
                    long npnum = scanner.nextLong();
                    System.out.print("Enter amount: ");
                    int npamt = scanner.nextInt();
                    System.out.print("Enter unit price: ");
                    double npprice = scanner.nextDouble();
                    System.out.print("Enter minimum stock: ");
                    int npmin = scanner.nextInt();
                    System.out.print("Enter warranty (months): ");
                    int warranty = scanner.nextInt();
                    inventory.add(new NonPerishableItem(npnum, npname, npamt, npprice, npmin, warranty));
                    System.out.println("Non-perishable item added.");
                    break;

                case 3:
                    System.out.print("Enter inventory number: ");
                    long addNum = scanner.nextLong();
                    boolean added = false;
                    System.out.print("Enter amount to add: ");
                    int addAmt = scanner.nextInt();

                    for (Item item : inventory) {
                        if (item.getInventoryNum() == addNum) {
                            item.setAmtInStock(item.getAmtInStock() + addAmt);
                            added = true;
                            System.out.println("Stock updated.");
                            break;
                        }
                    }
                    if (!added) System.out.println("Item not found.");
                    break;

                case 4:
                    System.out.print("Enter inventory number: ");
                    long depNum = scanner.nextLong();
                    System.out.print("Enter amount to deplete: ");
                    int depAmt = scanner.nextInt();

                    boolean depleted = false;
                    for (Item item : inventory) {
                        if (item.getInventoryNum() == depNum) {
                            item.depleteStock(depAmt);
                            depleted = true;
                            break;
                        }
                    }
                    if (!depleted) System.out.println("Item not found.");
                    break;

                case 5:
                    System.out.print("Enter inventory number: ");
                    long viewNum = scanner.nextLong();
                    boolean found = false;

                    for (Item item : inventory) {
                        if (item.getInventoryNum() == viewNum) {
                            System.out.println("\n--- Item Details ---");
                            System.out.println("Name: "+item.getName() + ", Item #: " +item.getInventoryNum() + ", Unit Price: $"+item.getUnitPrice()+ ", Amt in Stock: " +item.getAmtInStock());
                            found = true;
                            break;
                        }
                    }
                    if (!found) System.out.println("Item not found.");
                    break;

                case 6:
                    //Sales Entry
                    ArrayList<SaleItem> saleItems = new ArrayList<>();
                    System.out.print("How many items in this sale? ");
                    int saleCount = scanner.nextInt();

                    for (int i = 0; i < saleCount; i++) {
                        System.out.print("Enter inventory number: ");
                        long sinv = scanner.nextLong();

                        Item foundItem = null;
                        for (Item item : inventory) {
                            if (item.getInventoryNum() == sinv) {
                                foundItem = item;
                                break;
                            }
                        }

                        if (foundItem != null) {
                            System.out.print("Enter quantity: ");
                            int qty = scanner.nextInt();

                            if (qty > foundItem.getAmtInStock()) {
                                System.out.println("Not enough stock! Item skipped.");
                                continue;
                            }

                            saleItems.add(new SaleItem(
                                    foundItem.getInventoryNum(),
                                    foundItem.getName(),
                                    foundItem.getUnitPrice(),
                                    qty
                            ));

                            foundItem.depleteStock(qty);

                        } else {
                            System.out.println("Item not found.");
                        }
                    }

                    // Create Sale + total
                    Sale sale = new Sale(saleItems);
                    double total = sale.calculateTotal();
                    sales.add(sale);

                    System.out.println("Sale recorded. Total: $" + total);
                    break;

                case 7:
                    running = false;
                    System.out.println("Exiting Inventory Management System.");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
