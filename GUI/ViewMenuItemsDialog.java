// import javax.swing.*;
// import javax.swing.event.TableModelEvent;
// import javax.swing.event.TableModelListener;
// import javax.swing.table.DefaultTableModel;
// import javax.swing.text.View;

// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.sql.*;

// public class ViewMenuItemsDialog extends JDialog {
//     private JTable table;
//     private JButton addButton;
//     private JButton editButton;
//     private JButton deleteButton;
//     private DefaultTableModel tableModel;
//     private POS pos;
//     private Connection conn;

//     public ViewMenuItemsDialog(Frame owner, Connection conn, POS pos) {
//         super(owner, "Menu Items", true);
//         this.pos = pos;
//         this.conn = conn;

//         setLayout(new BorderLayout());

//         // Define table columns
//         String[] columnNames = { "MenuItemID", "Name", "Price", "Type" };
//         tableModel = new DefaultTableModel(columnNames, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 // Prevent editing the ID column
//                 return column != 0;
//             }
//         };

//         tableModel.addTableModelListener(new TableModelListener() {
//             @Override
//             public void tableChanged(TableModelEvent e) {
//                 int row = e.getFirstRow();
//                 int column = e.getColumn();
//                 if (row >= 0 && column >= 0) {
//                     Object value = tableModel.getValueAt(row, column);
//                     Object id = tableModel.getValueAt(row, 0); // Assuming ID is in the first column
//                     // Update the database
//                     updateMenuItemInDatabase(id, column, value);
//                 }
//             }
//         });

//         table = new JTable(tableModel);

//         add(new JScrollPane(table), BorderLayout.CENTER);

//         JPanel buttonPanel = new JPanel();
//         addButton = new JButton("Add");
//         deleteButton = new JButton("Delete");

//         addButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 // Open dialog to add item
//             }
//         });

//         // editButton.addActionListener(new ActionListener() {
//         // @Override
//         // public void actionPerformed(ActionEvent e) {
//         // int selectedRow = table.getSelectedRow();
//         // System.out.println(selectedRow);
//         // if (selectedRow != -1) {
//         // int menuItemId = (int) tableModel.getValueAt(selectedRow, 0);
//         // String name = (String) tableModel.getValueAt(selectedRow, 1);
//         // float price = (Float) tableModel.getValueAt(selectedRow, 2);
//         // String type = (String) tableModel.getValueAt(selectedRow, 3);

//         // EditMenuItemDialog editDialog = new EditMenuItemDialog(owner, true, conn,
//         // pos, menuItemId, name,
//         // price, type);
//         // editDialog.setLocationRelativeTo(ViewMenuItemsDialog.this);
//         // editDialog.setVisible(true);

//         // // Refresh table data to reflect any changes
//         // refreshTableData();
//         // } else {
//         // JOptionPane.showMessageDialog(ViewMenuItemsDialog.this, "Please select an
//         // item to edit.");
//         // }
//         // }
//         // });

//         deleteButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 // Confirm and delete selected item
//             }
//         });

//         buttonPanel.add(addButton);
//         // buttonPanel.add(editButton);
//         buttonPanel.add(deleteButton);

//         add(buttonPanel, BorderLayout.SOUTH);

//         setSize(400, 300);
//     }

//     private void updateMenuItemInDatabase(Object id, int column, Object value) {
//         String columnName;
//         // Translate the column index to your database column name
//         switch (column) {
//             case 1: // Assuming 0 is ID, 1 is Name, etc.
//                 columnName = "Name";
//                 break;
//             case 2:
//                 columnName = "Price";
//                 break;
//             case 3:
//                 columnName = "Type";
//                 break;
//             default:
//                 throw new IllegalArgumentException("Invalid column index");
//         }

//         // Construct SQL query
//         String sql = "UPDATE MenuItems SET " + columnName + " = ? WHERE MenuItemID = ?";

//         try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             // Set the value; since types might differ, some checks or conversions might be
//             // necessary
//             if (value instanceof String) {
//                 pstmt.setString(1, (String) value);
//             } else if (value instanceof Float) {
//                 pstmt.setFloat(1, (Float) value);
//             } // Add more cases as necessary

//             // Assuming ID is an integer
//             pstmt.setInt(2, (Integer) id);

//             // Execute the update
//             int affectedRows = pstmt.executeUpdate();
//             if (affectedRows > 0) {
//                 System.out.println("Update was successful.");
//             } else {
//                 System.out.println("No rows affected. Possible error in update operation.");
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//             JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage(), "Database Error",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     public void refreshTableData() {
//         // Clear existing data
//         tableModel.setRowCount(0);

//         // Query database and fill the table model
//         // Example: Fetch data from database and add to table model
//         try {
//             // Assuming you have a method getConnection() to get your DB connection

//             Statement stmt = conn.createStatement();
//             String query = "SELECT * FROM MenuItems;"; // Adjust query according to your DB schema
//             ResultSet rs = stmt.executeQuery(query);
//             while (rs.next()) {
//                 System.out.println("Fetching Data");
//                 int id = rs.getInt("MenuItemID");
//                 String name = rs.getString("Name");
//                 float price = rs.getFloat("Price");
//                 String type = rs.getString("Type");

//                 // Add data to table model
//                 tableModel.addRow(new Object[] { id, name, price, type });
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     // public static void main(String[] args) {
//     // MenuPage p = new MenuPage(null, null);
//     // JFrame f = new JFrame();
//     // f.setSize(1600, 900);
//     // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//     // f.add(p);
//     // f.setVisible(true);
//     // }
// }

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewMenuItemsDialog extends JDialog {
    private JTable table;
    private JButton addButton, deleteButton;
    private DefaultTableModel tableModel;
    private Connection conn;

    public ViewMenuItemsDialog(Frame owner, Connection conn) {
        super(owner, "Menu Items", true);
        this.conn = conn;
        initializeUI();
        refreshTableData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[] { "MenuItemID", "Name", "Price", "Type" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID column is not editable
            }
        };
        table = new JTable(tableModel);

        // Listen to cell edits
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                Object id = tableModel.getValueAt(row, 0);

                if (id == null || id.toString().isEmpty()) {
                    // This is a new row, handle the insert operation
                    String name = tableModel.getValueAt(row, 1).toString();
                    float price = Float.parseFloat(tableModel.getValueAt(row, 2).toString());
                    String type = tableModel.getValueAt(row, 3).toString();
                    insertNewItem(name, price, type);
                    // Refresh data to get new IDs and ensure table is up-to-date
                    refreshTableData();
                } else {
                    // Existing row, handle the update operation
                    Object value = tableModel.getValueAt(row, column);
                    updateMenuItemInDatabase(id, column, value);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        addButton = new JButton("Add");
        addButton.addActionListener(e -> addMenuItem());
        buttonPanel.add(addButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteMenuItem());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setSize(500, 300);
    }

    private void addMenuItem() {
        // Add a new empty row at the end of the table
        tableModel.addRow(new Object[] { null, "", 0.0, "" });
        int newRow = tableModel.getRowCount() - 1;

        // Ensure the new row is visible and editable
        table.scrollRectToVisible(table.getCellRect(newRow, 0, true));
        table.setRowSelectionInterval(newRow, newRow);
        table.editCellAt(newRow, 1);
        Component editor = table.getEditorComponent();
        if (editor != null) {
            editor.requestFocusInWindow();
        }

        table.revalidate();
        table.repaint();
    }

    private void insertNewItem(String name, float price, String type) {
        // Similar implementation as your existing insertMenuItem method
        String sql = "INSERT INTO MenuItems (Name, Price, Type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setFloat(2, price);
            pstmt.setString(3, type);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Item added successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No item was added.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void deleteMenuItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                Object itemId = tableModel.getValueAt(selectedRow, 0);
                deleteItemFromDatabase(itemId);
                refreshTableData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }

    private void deleteItemFromDatabase(Object itemId) {
        String sql = "DELETE FROM MenuItems WHERE MenuItemID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, (Integer) itemId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item deleted successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting item: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateMenuItemInDatabase(Object id, int column, Object value) {
        String columnName;
        String sql;
        switch (column) {
            case 1:
                columnName = "Name";
                sql = "UPDATE MenuItems SET " + columnName + " = ? WHERE MenuItemID = ?";
                break;
            case 2:
                columnName = "Price";
                // Explicitly casting the parameter to double precision in PostgreSQL
                sql = "UPDATE MenuItems SET " + columnName + " = CAST(? AS double precision) WHERE MenuItemID = ?";
                break;
            case 3:
                columnName = "Type";
                sql = "UPDATE MenuItems SET " + columnName + " = ? WHERE MenuItemID = ?";
                break;
            default:
                throw new IllegalArgumentException("Invalid column index");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (column == 2) { // For "Price" column
                // Use setObject with explicit parsing for the price column
                double priceValue = Double.parseDouble(value.toString());
                pstmt.setObject(1, priceValue);
            } else {
                pstmt.setObject(1, value); // For other columns
            }
            pstmt.setInt(2, (Integer) id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Update was successful for MenuItemID " + id + " for column " + columnName
                        + " with new value: " + value);
            } else {
                System.out.println("No rows affected. Possible error in update operation for MenuItemID " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating database for MenuItemID " + id + ": " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTableData() {
        tableModel.setRowCount(0); // Clear existing data

        String query = "SELECT * FROM MenuItems;";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("MenuItemID");
                String name = rs.getString("Name");
                float price = rs.getFloat("Price");
                String type = rs.getString("Type");
                tableModel.addRow(new Object[] { id, name, price, type });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching menu items: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
