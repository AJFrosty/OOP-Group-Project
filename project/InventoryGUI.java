package project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class InventoryGUI extends JFrame {

    class NotEnoughStockException extends Exception {
        public NotEnoughStockException(String msg) {
            super(msg);
        }
    }

    class InvalidEntryException extends Exception {
        public InvalidEntryException(String msg) {
            super(msg);
        }
    }

    private JTabbedPane tabbedPane;
    private boolean isNewItem = false;


    private void checkWarnings(Item item) {
        //MIN Stock
        if (item.getAmtInStock() < item.getMinimumStock()) {
            JOptionPane.showMessageDialog(this,
                    "Warning: Stock (" + item.getName()+") below minimum level (" + item.getMinimumStock() + "). Consider restocking.",
                    "Stock Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        //Expiry
        if (item instanceof PerishableItem p) {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate expiry = p.getExpirDate();
            if (!expiry.isBefore(today) && !expiry.isAfter(today.plusDays(5))) {
                JOptionPane.showMessageDialog(this,
                        "Warning: Item (" +item.getName()+") will expire within 5 days or is already expired!",
                        "Expiry Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private DefaultListModel<Item> getInventoryListModel() {
        JScrollPane scrollPane = (JScrollPane) ((JPanel) tabbedPane.getComponentAt(0)).getComponent(1);
        JList<Item> list = (JList<Item>) scrollPane.getViewport().getView();
        return (DefaultListModel<Item>) list.getModel();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateTotal(DefaultTableModel model, JLabel lblTotal) {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            total += (double) model.getValueAt(i, 4);
        }
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    public InventoryGUI() {
        setTitle("Inventory Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        //TWO MAIN TABS
        tabbedPane.addTab("Inventory", createInventoryPanel());
        tabbedPane.addTab("Sales", createSalesPanel());

        add(tabbedPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private long getNextInventoryNumber(DefaultListModel<Item> model) {
        long maxNum = 1000;
        for (int i = 0; i < model.size(); i++) {
            long num = model.get(i).getInventoryNum();
            if (num > maxNum)
                maxNum = num;
        }
        return maxNum + 1;
    }

    private JPanel createInventoryPanel() {

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

        DefaultListModel<Item> itemListModel = new DefaultListModel<>();
        JList<Item> itemList = new JList<>(itemListModel);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(itemList);
        panel.add(scrollPane, BorderLayout.CENTER);

        //Data Panel
        JPanel dataPanel = new JPanel();
        dataPanel.setBorder(BorderFactory.createTitledBorder("Item Details"));
        dataPanel.setLayout(new GridLayout(8, 2, 5, 5));

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
        JLabel lblExpiry = new JLabel("Expiry Date (YYYY-MM-DD):");
        JTextField txtExpiry = new JTextField();
        JLabel lblWarranty = new JLabel("Warranty Days:");
        JTextField txtWarranty = new JTextField();

        txtInvNum.setEditable(false);
        txtStock.setEditable(false);

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

        itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Item item = itemList.getSelectedValue();
                if (item == null) return;

                txtInvNum.setText(String.valueOf(item.getInventoryNum()));
                txtName.setText(item.getName());
                txtStock.setText(String.valueOf(item.getAmtInStock()));
                txtMinStock.setText(String.valueOf(item.getMinimumStock()));
                txtPrice.setText(String.valueOf(item.getUnitPrice()));
                txtExpiry.setText("");
                txtWarranty.setText("");
                rbtnPerishable.setSelected(false);
                rbtnNonPerishable.setSelected(false);

                if (item instanceof PerishableItem p) {
                    txtExpiry.setText(p.getExpirDate().toString());
                    rbtnPerishable.setSelected(true);
                } else if (item instanceof NonPerishableItem np) {
                    txtWarranty.setText(String.valueOf(np.getWarrantyPeriod()));
                    rbtnNonPerishable.setSelected(true);
                }

                isNewItem = false;
            }
        });

        //SAVE
        btnSave.addActionListener(e -> {
            Item selectedItem = itemList.getSelectedValue();
            if (selectedItem == null) {
                showError("No item selected!");
                return;
            }

            String name = txtName.getText().trim();
            String minStr = txtMinStock.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String expiryStr = txtExpiry.getText().trim();
            String warrantyStr = txtWarranty.getText().trim();

            if (name.isEmpty() || !name.matches("^[a-zA-Z\\s']+$")) {
                showError("Name must contain only letters, spaces, or apostrophes.");
                return;
            }

            int minStock;
            try {
                minStock = Integer.parseInt(minStr);
                if (minStock <= 0) throw new Exception("Minimum stock must be > 0");
            } catch (Exception ex) {
                showError("Invalid Minimum Stock: " + ex.getMessage());
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) throw new Exception("Unit price must be > 0");
            } catch (Exception ex) {
                showError("Invalid Unit Price: " + ex.getMessage());
                return;
            }

            long invNum = selectedItem.getInventoryNum();
            int amtInStock = selectedItem.getAmtInStock();

            boolean wantsPerishable = rbtnPerishable.isSelected();
            boolean wantsNonPerishable = rbtnNonPerishable.isSelected();

            if (!wantsPerishable && !wantsNonPerishable) {
                showError("Please select Perishable or Non-Perishable.");
                return;
            }

            if (wantsPerishable) {
                LocalDate expiry;
                try {
                    expiry = LocalDate.parse(expiryStr);
                    if (!expiry.isAfter(LocalDate.now())) {
                        showError("Expiry date must be after today.");
                        return;
                    }
                } catch (Exception ex) {
                    showError("Invalid Expiry Date. Use YYYY-MM-DD.");
                    return;
                }

                PerishableItem updatedItem = new PerishableItem(invNum, name, amtInStock, price, minStock, expiry);

                int idx = itemList.getSelectedIndex();
                itemListModel.set(idx, updatedItem);
                itemList.setSelectedIndex(idx);

                checkWarnings(updatedItem);

            } else {
                int warranty;
                try {
                    warranty = Integer.parseInt(warrantyStr);
                    if (warranty <= 0) throw new Exception("Warranty must be > 0");
                } catch (Exception ex) {
                    showError("Invalid Warranty: " + ex.getMessage());
                    return;
                }

                NonPerishableItem updatedItem = new NonPerishableItem(invNum, name, amtInStock, price, minStock, warranty);

                int idx = itemList.getSelectedIndex();
                itemListModel.set(idx, updatedItem);
                itemList.setSelectedIndex(idx);

                checkWarnings(updatedItem);
            }

            JOptionPane.showMessageDialog(this, "Item updated successfully!");
        });

        btnAddItem.addActionListener(e -> {
            if (!isNewItem) {
                isNewItem = true;
                txtInvNum.setText(String.valueOf(getNextInventoryNumber(itemListModel)));
                txtName.setText("");
                txtStock.setText("0");
                txtMinStock.setText("");
                txtPrice.setText("");
                txtExpiry.setText("");
                txtWarranty.setText("");
                txtInvNum.setEditable(true);
                txtStock.setEditable(true);
                rbtnPerishable.setSelected(false);
                rbtnNonPerishable.setSelected(false);
                JOptionPane.showMessageDialog(this, "Enter details for new item, then click Add Item again to save.");
                return;
            }

            String invStr = txtInvNum.getText().trim();
            String name = txtName.getText().trim();
            String stockStr = txtStock.getText().trim();
            String minStr = txtMinStock.getText().trim();
            String priceStr = txtPrice.getText().trim();

            long invNum;
            try {
                invNum = Long.parseLong(invStr);
                if (invNum <= 0) throw new Exception("Inventory Number > 0");
            } catch (Exception ex) {
                showError("Invalid Inventory Number: " + ex.getMessage());
                return;
            }

            for (int i = 0; i < itemListModel.size(); i++) {
                Item it = itemListModel.get(i);
                if (it.getInventoryNum() == invNum) { showError("Inventory number exists"); return; }
                if (it.getName().equalsIgnoreCase(name)) { showError("Item name exists"); return; }
            }

            if (!name.matches("^[a-zA-Z\\s']+$")) {
                showError("Name must contain only letters, spaces, or apostrophes");
                return;
            }

            int stock, minStock;
            double price;
            try { stock = Integer.parseInt(stockStr); if (stock < 0) throw new Exception(); } catch(Exception ex){showError("Stock must be >=0"); return;}
            try { minStock = Integer.parseInt(minStr); if (minStock <= 0) throw new Exception(); } catch(Exception ex){showError("Minimum Stock must be >0"); return;}
            try { price = Double.parseDouble(priceStr); if (price <= 0) throw new Exception(); } catch(Exception ex){showError("Unit Price must be >0"); return;}

            Item newItem;
            if (rbtnPerishable.isSelected()) {
                LocalDate expiry;
                try {
                    expiry = LocalDate.parse(txtExpiry.getText().trim());
                    if (!expiry.isAfter(LocalDate.now())) { showError("Expiry must be after today"); return; }
                } catch(Exception ex){ showError("Invalid Expiry Date"); return;}
                newItem = new PerishableItem(invNum, name, stock, price, minStock, expiry);
            } else if (rbtnNonPerishable.isSelected()) {
                int warranty;
                try { warranty = Integer.parseInt(txtWarranty.getText().trim()); if (warranty <= 0) throw new Exception(); } catch(Exception ex){ showError("Warranty > 0 required"); return; }
                newItem = new NonPerishableItem(invNum, name, stock, price, minStock, warranty);
            } else {
                showError("Select item type");
                return;
            }

            itemListModel.addElement(newItem);
            JOptionPane.showMessageDialog(this, "Item added successfully!");
            isNewItem = false;
            txtInvNum.setEditable(false);
            txtStock.setEditable(false);
            txtInvNum.setText(""); txtName.setText(""); txtStock.setText("");
            txtMinStock.setText(""); txtPrice.setText(""); txtExpiry.setText(""); txtWarranty.setText("");
            rbtnPerishable.setSelected(false); rbtnNonPerishable.setSelected(false);
        });

        btnAddStock.addActionListener(e -> {
            Item item = itemList.getSelectedValue();
            if(item==null) { showError("No item selected"); return; }
            String input = JOptionPane.showInputDialog(this, "Enter amount to add:");
            if(input==null) return;
            try{
                int amt = Integer.parseInt(input);
                if(amt<=0) throw new InvalidEntryException("Amount must be >0");
                item.setAmtInStock(item.getAmtInStock()+amt);
                txtStock.setText(String.valueOf(item.getAmtInStock()));
                itemList.repaint();
                checkWarnings(item);
            }catch(NumberFormatException ex){ showError("Invalid number"); }
            catch(InvalidEntryException ex){ showError(ex.getMessage()); }
        });

        btnDepleteStock.addActionListener(e -> {
            Item item = itemList.getSelectedValue();
            if(item==null){ showError("No item selected"); return;}
            String input = JOptionPane.showInputDialog(this, "Enter amount to remove:");
            if(input==null) return;
            try{
                int amt = Integer.parseInt(input);
                if(amt<=0) throw new InvalidEntryException("Amount must be >0");
                if(amt>item.getAmtInStock()) throw new NotEnoughStockException("Not enough stock");

                item.setAmtInStock(item.getAmtInStock()-amt);
                txtStock.setText(String.valueOf(item.getAmtInStock()));
                itemList.repaint();

                checkWarnings(item);

            }catch(NumberFormatException ex){ showError("Invalid number"); }
            catch(InvalidEntryException | NotEnoughStockException ex){ showError(ex.getMessage()); }
        });


        btnDeleteItem.addActionListener(e -> {
            Item item = itemList.getSelectedValue();
            if(item==null){ showError("No item selected"); return;}
            int confirm = JOptionPane.showConfirmDialog(this,"Delete "+item.getName()+"?","Confirm Delete",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                itemListModel.removeElement(item);
                txtInvNum.setText(""); txtName.setText(""); txtStock.setText(""); txtMinStock.setText(""); txtPrice.setText("");
                txtExpiry.setText(""); txtWarranty.setText(""); rbtnPerishable.setSelected(false); rbtnNonPerishable.setSelected(false);
            }
        });

        itemListModel.addElement(new PerishableItem(1001,"Milk",25,12.5,5,LocalDate.now().plusDays(5)));
        itemListModel.addElement(new NonPerishableItem(2002,"Laptop",5,850,1,365));

        return panel;
    }

    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblItem = new JLabel("Select Item:");
        JComboBox<Item> cmbItems = new JComboBox<>();
        JLabel lblQty = new JLabel("Quantity:");
        JTextField txtQty = new JTextField(5);
        JButton btnAdd = new JButton("Add");
        JButton btnRemove = new JButton("Remove");
        JButton btnRefresh = new JButton("Refresh");
        topPanel.add(lblItem); topPanel.add(cmbItems); topPanel.add(lblQty); topPanel.add(txtQty);
        topPanel.add(btnAdd); topPanel.add(btnRemove); topPanel.add(btnRefresh);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Inventory #","Name","Qty","Unit Price","Line Total"};
        DefaultTableModel salesModel = new DefaultTableModel(columns,0);
        JTable salesTable = new JTable(salesModel);
        panel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTotal = new JLabel("Total: $0.00"); lblTotal.setFont(new Font("Arial", Font.BOLD,16));
        JButton btnComplete = new JButton("COMPLETE SALE");
        btnComplete.setBackground(Color.GREEN); btnComplete.setForeground(Color.BLACK);
        btnComplete.setFont(new Font("Arial",Font.BOLD,14));
        btnComplete.setPreferredSize(new Dimension(180,35));
        bottomPanel.add(lblTotal); bottomPanel.add(btnComplete);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        Runnable refreshDropdown = () -> {
            cmbItems.removeAllItems();
            DefaultListModel<Item> model = getInventoryListModel();
            for(int i=0;i<model.size();i++) cmbItems.addItem(model.get(i));
        };
        refreshDropdown.run();

        btnAdd.addActionListener(e -> {
            Item item = (Item) cmbItems.getSelectedItem();
            if (item == null) { 
                showError("No item selected"); 
                return; 
            }

            int qty;
            try {
                qty = Integer.parseInt(txtQty.getText().trim());
                if (qty <= 0) throw new InvalidEntryException("Quantity must be > 0");
                if (qty > item.getAmtInStock()) throw new NotEnoughStockException("Not enough stock available");
            } catch (NumberFormatException ex) {
                showError("Invalid quantity"); 
                return;
            } catch (InvalidEntryException | NotEnoughStockException ex) {
                showError(ex.getMessage()); 
                return;
            }

            boolean found = false;
            for (int i = 0; i < salesModel.getRowCount(); i++) {
                long invNum = (long) salesModel.getValueAt(i, 0);
                if (invNum == item.getInventoryNum()) {
                    int currentQty = (int) salesModel.getValueAt(i, 2);
                    int newQty = currentQty + qty;

                    if (newQty > item.getAmtInStock()) {
                        showError("Not enough stock to add this quantity");
                        return;
                    }

                    salesModel.setValueAt(newQty, i, 2);
                    double lineTotal = newQty * item.getUnitPrice();
                    salesModel.setValueAt(lineTotal, i, 4);
                    found = true;
                    break;
                }
            }

            if (!found) {
                double lineTotal = qty * item.getUnitPrice();
                salesModel.addRow(new Object[]{
                    item.getInventoryNum(),
                    item.getName(),
                    qty,
                    item.getUnitPrice(),
                    lineTotal
                });
            }

            updateTotal(salesModel, lblTotal);
        });

        btnRemove.addActionListener(e -> {
            int row = salesTable.getSelectedRow();
            if(row==-1){ showError("Select a line item"); return;}
            salesModel.removeRow(row); updateTotal(salesModel, lblTotal);
        });

        btnRefresh.addActionListener(e -> refreshDropdown.run());

        btnComplete.addActionListener(e -> {
            if(salesModel.getRowCount()==0){ showError("No items in sale"); return;}
            DefaultListModel<Item> invList = getInventoryListModel();
            try{
                for(int i=0;i<salesModel.getRowCount();i++){
                    long invNum = (long)salesModel.getValueAt(i,0);
                    int qty = (int)salesModel.getValueAt(i,2);
                    for(int j=0;j<invList.size();j++){
                        if(invList.get(j).getInventoryNum()==invNum){
                            if(qty>invList.get(j).getAmtInStock()) throw new NotEnoughStockException("Not enough stock for "+invList.get(j).getName());
                            invList.get(j).setAmtInStock(invList.get(j).getAmtInStock()-qty);
                            checkWarnings(invList.get(j));
                        }
                    }
                }
            }catch(NotEnoughStockException ex){ showError(ex.getMessage()); return; }

            JOptionPane.showMessageDialog(this,"Sale completed successfully!");
            salesModel.setRowCount(0);
            txtQty.setText(""); lblTotal.setText("Total: $0.00");
        });

        return panel;
    }

    public static void main(String[] args) {
        new InventoryGUI();
    }
}
