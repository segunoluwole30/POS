import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        table.setModel(tableModel);

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
        tableModel.addRow(new Object[] { null, "", 0.0, "Entree" });
        int newRow = tableModel.getRowCount() - 1;

        // Ensure the new row is visible and editable
        table.scrollRectToVisible(table.getCellRect(newRow, 0, true));
        table.setRowSelectionInterval(newRow, newRow);

        // Make specific cells editable
        table.editCellAt(newRow, 1);
        table.editCellAt(newRow, 2);
        table.editCellAt(newRow, 3);

        // Request focus on the first editable cell
        Component editor = table.getEditorComponent();
        if (editor != null) {
            editor.requestFocusInWindow();
        }

        table.revalidate();
        table.repaint();
    }

    private void insertNewItem(String name, float price, String type) {
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
                float priceValue = Float.parseFloat(value.toString());
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
