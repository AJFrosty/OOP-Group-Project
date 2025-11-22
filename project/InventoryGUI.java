package project;

import javax.swing.*;
import java.awt.*;

public class InventoryGUI extends JFrame {

    private JTabbedPane tabbedPane;
    private boolean isNewItem = false;

    public InventoryGUI() {

        setTitle("Inventory Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        //Add the two required tabs
        tabbedPane.addTab("Inventory", createInventoryPanel());
        tabbedPane.addTab("Sales", createSalesPanel());

        add(tabbedPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private long getNextInventoryNumber(DefaultListModel<Item> model) {
        long maxNum = 1000; //start number if list is empty
        for (int i = 0; i < model.size(); i++) {
            long num = model.get(i).getInventoryNum();
            if (num > maxNum)
                maxNum = num;
        }
        return maxNum + 1;
    }

    //Inventory
    private JPanel createInventoryPanel() {

        //MAIN PANEL
        JPanel panel = new JPanel(new BorderLayout());

        //Tool Bar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnAddItem = new JButton("Add Item");
        JButton btnDeleteItem = new JButton("Delete Item");
        JButton btnAddStock = new JButton("Add Stock");
        JButton btnDepleteStock = new JButton("Deplete Stock");
        JButton btnSave = new JButton("Save");

        toolBar.add(btnAddItem);
        toolBar.add(btnDeleteItem);
        toolBar.add(btnAddStock);
        toolBar.add(btnDepleteStock);
        toolBar.add(btnSave);

        panel.add(toolBar, BorderLayout.NORTH);

        //LIST OF ITEMS
        DefaultListModel<Item> itemListModel = new DefaultListModel<>();
        JList<Item> itemList = new JList<>(itemListModel);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(itemList);
        panel.add(scrollPane, BorderLayout.CENTER);

        //Data Panels
        JPanel dataPanel = new JPanel();
        dataPanel.setBorder(BorderFactory.createTitledBorder("Item Details"));
        dataPanel.setLayout(new GridLayout(8, 2, 5, 5)); // extra row for type

        //Radio buttons for item type
        JLabel lblType = new JLabel("Item Type:");
        JRadioButton rbtnPerishable = new JRadioButton("Perishable");
        JRadioButton rbtnNonPerishable = new JRadioButton("Non-Perishable");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(rbtnPerishable);
        typeGroup.add(rbtnNonPerishable);
        JPanel typePanel = new JPanel();
        typePanel.add(rbtnPerishable);
        typePanel.add(rbtnNonPerishable);

        JLabel lblInvNum = new JLabel("Inventory Number:");
        JTextField txtInvNum = new JTextField();

        JLabel lblName = new JLabel("Name:");
        JTextField txtName = new JTextField();

        JLabel lblStock = new JLabel("Amount in Stock:");
        JTextField txtStock = new JTextField();

        JLabel lblMinStock = new JLabel("Minimum Stock:");
        JTextField txtMinStock = new JTextField();

        JLabel lblPrice = new JLabel("Unit Price:");
        JTextField txtPrice = new JTextField();

        JLabel lblExpiry = new JLabel("Expiry Date (Perishable):");
        JTextField txtExpiry = new JTextField();

        JLabel lblWarranty = new JLabel("Warranty Days (Non-Perishable):");
        JTextField txtWarranty = new JTextField();

        //Make non-editable fields
        txtInvNum.setEditable(false);
        txtStock.setEditable(false);

        //Add fields to panel
        dataPanel.add(lblType);
        dataPanel.add(typePanel);
        dataPanel.add(lblInvNum);
        dataPanel.add(txtInvNum);
        dataPanel.add(lblName);
        dataPanel.add(txtName);
        dataPanel.add(lblStock);
        dataPanel.add(txtStock);
        dataPanel.add(lblMinStock);
        dataPanel.add(txtMinStock);
        dataPanel.add(lblPrice);
        dataPanel.add(txtPrice);
        dataPanel.add(lblExpiry);
        dataPanel.add(txtExpiry);
        dataPanel.add(lblWarranty);
        dataPanel.add(txtWarranty);

        panel.add(dataPanel, BorderLayout.SOUTH);

        //Auto fill when selected an item
        itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {

                Item item = itemList.getSelectedValue();
                if (item == null)
                    return;

                //Fill common fields
                txtInvNum.setText(String.valueOf(item.getInventoryNum()));
                txtName.setText(item.getName());
                txtStock.setText(String.valueOf(item.getAmtInStock()));
                txtMinStock.setText(String.valueOf(item.getMinimumStock()));
                txtPrice.setText(String.valueOf(item.getUnitPrice()));

                //Reset fields
                txtExpiry.setText("");
                txtWarranty.setText("");
                rbtnPerishable.setSelected(false);
                rbtnNonPerishable.setSelected(false);

                //Fill perishable or non-perishable fields
                if (item instanceof PerishableItem p) {
                    txtExpiry.setText(p.getExpirDate().toString());
                    rbtnPerishable.setSelected(true);
                } else if (item instanceof NonPerishableItem np) {
                    txtWarranty.setText(String.valueOf(np.getWarrantyPeriod()));
                    rbtnNonPerishable.setSelected(true);
                }

                //Not in new item mode
                isNewItem = false;
            }
        });

        //Saving items
        btnSave.addActionListener(e -> {
            Item item = itemList.getSelectedValue();
            if (item == null) {
                JOptionPane.showMessageDialog(panel, "No item selected!");
                return;
            }

            try {
                item.setName(txtName.getText());
                item.setMinimumStock(Integer.parseInt(txtMinStock.getText()));
                item.setUnitPrice(Double.parseDouble(txtPrice.getText()));

                if (item instanceof PerishableItem p) {
                    p.setExpirDate(java.time.LocalDate.parse(txtExpiry.getText()));
                } else if (item instanceof NonPerishableItem np) {
                    np.setWarrantyPeriod(Integer.parseInt(txtWarranty.getText()));
                }

                itemList.repaint();
                JOptionPane.showMessageDialog(panel, "Item updated successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input: " + ex.getMessage());
            }
        });

        //Add to inventory / Clear for new item
        btnAddItem.addActionListener(e -> {

            //FIRST CLICK — ENABLE ENTRY MODE
            if (!isNewItem) {
                isNewItem = true;

                //Clear fields for new item
                txtInvNum.setText("");
                txtName.setText("");
                txtStock.setText("");
                txtMinStock.setText("");
                txtPrice.setText("");
                txtExpiry.setText("");
                txtWarranty.setText("");

                //Make fields editable for NEW item creation
                txtInvNum.setEditable(true);
                txtStock.setEditable(true);

                JOptionPane.showMessageDialog(panel, "Enter details for new item, then click Add Item again to save.");
                return;
            }

            //SECOND CLICK — VALIDATE AND SAVE ITEM
            if (txtInvNum.getText().trim().isEmpty() ||
                    txtName.getText().trim().isEmpty() ||
                    txtMinStock.getText().trim().isEmpty() ||
                    txtPrice.getText().trim().isEmpty() ||
                    txtStock.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(panel, "Please fill in all required fields.");
                return;
            }

            if (!rbtnPerishable.isSelected() && !rbtnNonPerishable.isSelected()) {
                JOptionPane.showMessageDialog(panel, "Please select Perishable or Non-Perishable.");
                return;
            }

            if (rbtnPerishable.isSelected() && txtExpiry.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Expiry date is required for perishable items.");
                return;
            }

            if (rbtnNonPerishable.isSelected() && txtWarranty.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Warranty days are required for non-perishable items.");
                return;
            }

            try {
                long inv = Long.parseLong(txtInvNum.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());
                int min = Integer.parseInt(txtMinStock.getText().trim());
                double price = Double.parseDouble(txtPrice.getText().trim());
                String name = txtName.getText().trim();

                //Check duplicates
                for (int i = 0; i < itemListModel.size(); i++) {
                    Item test = itemListModel.get(i);
                    if (test.getInventoryNum() == inv) {
                        JOptionPane.showMessageDialog(panel, "Inventory number already exists!");
                        return;
                    }
                    if (test.getName().equalsIgnoreCase(name)) {
                        JOptionPane.showMessageDialog(panel, "Name already exists!");
                        return;
                    }
                }

                Item newItem;

                if (rbtnPerishable.isSelected()) {
                    java.time.LocalDate exp = java.time.LocalDate.parse(txtExpiry.getText().trim());
                    newItem = new PerishableItem(inv, name, stock, price, min, exp);
                } else {
                    int warranty = Integer.parseInt(txtWarranty.getText().trim());
                    newItem = new NonPerishableItem(inv, name, stock, price, min, warranty);
                }

                itemListModel.addElement(newItem);

                JOptionPane.showMessageDialog(panel, "Item added successfully!");

            
                isNewItem = false;
                txtInvNum.setEditable(false);
                txtStock.setEditable(false);

                //Clear again
                txtInvNum.setText("");
                txtName.setText("");
                txtStock.setText("");
                txtMinStock.setText("");
                txtPrice.setText("");
                txtExpiry.setText("");
                txtWarranty.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input: " + ex.getMessage());
            }
        });

        //Add to inventory with dialouge box
        btnAddStock.addActionListener(e -> {
            Item item = itemList.getSelectedValue();
            if (item == null) {
                JOptionPane.showMessageDialog(panel, "No item selected!");
                return;
            }

            String input = JOptionPane.showInputDialog(panel, "Enter amount to add:");
            if (input == null)
                return;

            try {
                int amount = Integer.parseInt(input);
                item.setAmtInStock(item.getAmtInStock() + amount);
                txtStock.setText(String.valueOf(item.getAmtInStock()));
                itemList.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid number!");
            }
        });

        //Depleting a strock with dialogue box
        btnDepleteStock.addActionListener(e -> {
            Item item = itemList.getSelectedValue();
            if (item == null) {
                JOptionPane.showMessageDialog(panel, "No item selected!");
                return;
            }

            String input = JOptionPane.showInputDialog(panel, "Enter amount to remove:");
            if (input == null)
                return;

            try {
                int amount = Integer.parseInt(input);
                if (amount > item.getAmtInStock()) {
                    JOptionPane.showMessageDialog(panel, "Not enough stock!");
                    return;
                }

                item.setAmtInStock(item.getAmtInStock() - amount);
                txtStock.setText(String.valueOf(item.getAmtInStock()));
                itemList.repaint();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid number!");
            }
        });

        //Deleting an item with confirmation box
        btnDeleteItem.addActionListener(e -> {
            Item item = itemList.getSelectedValue();
            if (item == null) {
                JOptionPane.showMessageDialog(panel, "No item selected!");
                return;
            }

            if (!isNewItem) {
                //Clear fields
                txtInvNum.setText(String.valueOf(getNextInventoryNumber(itemListModel)));
                txtName.setText("");
                txtStock.setText("0"); // start stock at 0
                txtMinStock.setText("");
                txtPrice.setText("");
                txtExpiry.setText("");
                txtWarranty.setText("");
                rbtnPerishable.setSelected(false);
                rbtnNonPerishable.setSelected(false);

                isNewItem = true;
                JOptionPane.showMessageDialog(panel,
                        "Enter details for the new item and click Add Item again to save.");
            }

            int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Are you sure you want to delete \"" + item.getName() + "\"?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                itemListModel.removeElement(item);

                //Clearing each fields
                txtInvNum.setText("");
                txtName.setText("");
                txtStock.setText("");
                txtMinStock.setText("");
                txtPrice.setText("");
                txtExpiry.setText("");
                txtWarranty.setText("");
                rbtnPerishable.setSelected(false);
                rbtnNonPerishable.setSelected(false);
            }
        });

        //This is 2 example items used for testing ofc
        itemListModel.addElement(new PerishableItem(1001, "Milk", 25, 12.50, 5, java.time.LocalDate.now().plusDays(5)));
        itemListModel.addElement(new NonPerishableItem(2002, "Laptop", 5, 850.00, 1, 365));

        return panel;
    }

    //SALES
     private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        //input fields
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblName = new JLabel("Item Name:");
        JTextField txtItemName = new JTextField(12);

        JLabel lblQty = new JLabel("Quantity:");
        JTextField txtQty = new JTextField(5);

        JLabel lblPrice = new JLabel("Unit Price:");
        JTextField txtUnitPrice = new JTextField(7);

        JButton btnAddLine = new JButton("Add");
        JButton btnRemoveLine = new JButton("Remove");
        JButton btnCompleteSale = new JButton("Complete Sale");

        topPanel.add(lblName);
        topPanel.add(txtItemName);
        topPanel.add(lblQty);
        topPanel.add(txtQty);
        topPanel.add(lblPrice);
        topPanel.add(txtUnitPrice);
        topPanel.add(btnAddLine);
        topPanel.add(btnRemoveLine);
        topPanel.add(btnCompleteSale);

        panel.add(topPanel, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"Item Name", "Quantity", "Unit Price", "Line Total"};
        salesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // user shouldn't type directly into table
            }
        };

        JTable table = new JTable(salesTableModel);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        // total 
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTotal = new JLabel("Total: ");
        txtSaleTotal = new JTextField("0.00", 10);
        txtSaleTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        txtSaleTotal.setEditable(false);
        bottomPanel.add(lblTotal);
        bottomPanel.add(txtSaleTotal);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Logic

        // helper to recalc total
        Runnable updateTotal = () -> {
            double total = 0.0;
            for (int i = 0; i < salesTableModel.getRowCount(); i++) {
                String v = salesTableModel.getValueAt(i, 3).toString();
                try {
                    total += Double.parseDouble(v);
                } catch (NumberFormatException ignored) {
                }
            }
            txtSaleTotal.setText(String.format("%.2f", total));
        };

        // Add line
        btnAddLine.addActionListener(e -> {
            String name = txtItemName.getText().trim();
            String qtyText = txtQty.getText().trim();
            String priceText = txtUnitPrice.getText().trim();

            if (name.isEmpty() || qtyText.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in item name, quantity, and unit price.");
                return;
            }

            try {
                int qty = Integer.parseInt(qtyText);
                double price = Double.parseDouble(priceText);

                if (qty <= 0) {
                    JOptionPane.showMessageDialog(panel, "Quantity must be positive.");
                    return;
                }
                if (price < 0) {
                    JOptionPane.showMessageDialog(panel, "Unit price cannot be negative.");
                    return;
                }

                double lineTotal = qty * price;

                salesTableModel.addRow(new Object[]{
                        name,
                        qty,
                        String.format("%.2f", price),
                        String.format("%.2f", lineTotal)
                });

                updateTotal.run();

                // Clear inputs for next entry
                txtItemName.setText("");
                txtQty.setText("");
                txtUnitPrice.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Quantity and Unit Price must be numeric.");
            }
        });

        // Remove selected line
        btnRemoveLine.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel, "No line selected.");
                return;
            }
            salesTableModel.removeRow(row);
            updateTotal.run();
        });

        // Complete sale (simple behaviour: show message + clear table)
        btnCompleteSale.addActionListener(e -> {
            if (salesTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(panel, "No items in the sale.");
                return;
            }

            String totalStr = txtSaleTotal.getText();
            JOptionPane.showMessageDialog(
                    panel,
                    "Sale completed.\nTotal amount: $" + totalStr,
                    "Sale Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Clear sale
            salesTableModel.setRowCount(0);
            txtSaleTotal.setText("0.00");
        });

        return panel;
    }

    public static void main(String[] args) {
        new InventoryGUI();
    }
}
