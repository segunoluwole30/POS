import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ViewMenuItemsDialog extends JDialog {
    private JTable table;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private DefaultTableModel tableModel;
    private POS pos;
    private Connection conn;

    public ViewMenuItemsDialog(Frame owner, Connection conn, POS pos) {
        super(owner, "Menu Items", true);
        this.pos = pos;
        this.conn = conn;

        setLayout(new BorderLayout());

        // Define table columns
        String[] columnNames = { "MenuItemID", "Name", "Price", "Type" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Add the table to the dialog
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open dialog to add item
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int menuItemId = (int) tableModel.getValueAt(selectedRow, 0);
                    String name = (String) tableModel.getValueAt(selectedRow, 1);
                    float price = (Float) tableModel.getValueAt(selectedRow, 2);
                    String type = (String) tableModel.getValueAt(selectedRow, 3);

                    EditMenuItemDialog editDialog = new EditMenuItemDialog(owner, true, conn, pos, menuItemId, name,
                            price, type);
                    editDialog.setLocationRelativeTo(ViewMenuItemsDialog.this);
                    editDialog.setVisible(true);

                    // Refresh table data to reflect any changes
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(ViewMenuItemsDialog.this, "Please select an item to edit.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Confirm and delete selected item
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 300);
    }

    public void refreshTableData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Query database and fill the table model
        // Example: Fetch data from database and add to table model
        try {
            // Assuming you have a method getConnection() to get your DB connection

            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM MenuItems;"; // Adjust query according to your DB schema
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("Fetching Data");
                int id = rs.getInt("MenuItemID");
                String name = rs.getString("Name");
                float price = rs.getFloat("Price");
                String type = rs.getString("Type");

                // Add data to table model
                tableModel.addRow(new Object[] { id, name, price, type });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static void main(String[] args) {
    // MenuPage p = new MenuPage(null, null);
    // JFrame f = new JFrame();
    // f.setSize(1600, 900);
    // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // f.add(p);
    // f.setVisible(true);
    // }
}
