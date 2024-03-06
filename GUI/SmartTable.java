import java.sql.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import java.util.List;
import java.util.ArrayList;

/**
 * SmartTable class extends JPanel to create a dynamic table connected to a
 * database.
 * It allows users to insert, update, and delete records in a database through a
 * graphical interface.
 */

public class SmartTable extends JPanel {

    // Data Members
    private Connection conn;
    public DefaultTableModel tableModel;
    public JTable table;
    private List<Integer> ItemIDs = new ArrayList<>(); // store menu item id's for database operations
    private String Query = "";

    /**
     * Constructor to initialize the SmartTable with a database connection and a
     * query.
     * 
     * @param conn  The database connection object.
     * @param Query The SQL query used to fetch data to populate the table.
     */
    public SmartTable(Connection conn, String Query) {
        this.Query = Query;
        this.conn = conn;
        this.tableModel = new DefaultTableModel(new String[] { "Item ID", "Name", "Stock", "MaxStock", "Units" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
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
                    float stock = Float.parseFloat(tableModel.getValueAt(row, 2).toString());
                    float maxstock = Float.parseFloat(tableModel.getValueAt(row, 3).toString());
                    String units = tableModel.getValueAt(row, 4).toString();
                    insertNewItem(name, stock, maxstock, units);
                    refreshTableData();

                } else {
                    // Existing row, handle the update operation
                    Object value = tableModel.getValueAt(row, column);
                    updateMenuItemInDatabase(id, column, value);
                }
            }
        });
    }

    /**
     * Inserts a new item into the database and updates the table.
     * 
     * @param name     The name of the new item.
     * @param stock    The stock level of the new item.
     * @param maxstock The maximum stock level of the new item.
     * @param units    The units of measure for the new item.
     */
    public void insertNewItem(String name, float stock, float maxstock, String units) {
        String sql = "INSERT INTO IngredientsInventory (Name, Stock, MaxStock, Units) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setFloat(2, stock);
            pstmt.setFloat(3, maxstock);
            pstmt.setString(4, units);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ItemIDs.add(generatedKeys.getInt(1)); // Store the new MenuItemID
                } else {
                    throw new SQLException("Creating item failed, no ID obtained.");
                }
            }
            JOptionPane.showMessageDialog(this, "Item added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected item from the table and database.
     */
    public void deleteItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                int DelID = ItemIDs.get(selectedRow); // Get the ID from the list
                deleteItemFromDatabase(DelID);
                refreshTableData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }

    /**
     * Deletes the selected item from the table and database.
     */
    public void deleteItemFromDatabase(Object itemId) {
        String sql = "DELETE FROM IngredientsInventory WHERE IngredientID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, (Integer) itemId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Item deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No item was deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting item: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Updates a specific item's attribute in the database.
     * 
     * @param id     The ID of the item to update.
     * @param column The column index of the attribute to update.
     * @param value  The new value for the attribute.
     */
    public void updateMenuItemInDatabase(Object id, int column, Object value) {
        String columnName;
        String sql;
        switch (column) {
            case 1:
                columnName = "Name";
                sql = "UPDATE IngredientsInventory SET " + columnName + " = ? WHERE IngredientID = ?";
                break;
            case 2:
                columnName = "Stock";
                // Explicitly casting the parameter to double precision in PostgreSQL
                sql = "UPDATE IngredientsInventory SET " + columnName
                        + " = CAST(? AS double precision) WHERE IngredientID = ?";
                break;
            case 3:
                columnName = "MaxStock";
                sql = "UPDATE IngredientsInventory SET " + columnName
                        + " = CAST(? AS double precision) WHERE IngredientID = ?";
                break;
            case 4:
                columnName = "Units";
                sql = "UPDATE IngredientsInventory SET " + columnName + " = ? WHERE IngredientID = ?";
                break;

            default:
                throw new IllegalArgumentException("Invalid column index");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (column == 2) { // For "Stock" column
                // Use setObject with explicit parsing for the Stock column
                float priceValue = Float.parseFloat(value.toString());
                pstmt.setObject(1, priceValue);

            } else if (column == 3) { // For "MaxStock" column
                // Use setObject with explicit parsing for the MaxStock column
                float priceValue = Float.parseFloat(value.toString());
                pstmt.setObject(1, priceValue);

            } else {
                pstmt.setObject(1, value); // For other columns
            }
            pstmt.setInt(2, (Integer) id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Update was successful for IngredientID " + id + " for column " + columnName
                        + " with new value: " + value);
            } else {
                System.out.println("No rows affected. Possible error in update operation for MenuItemID " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error updating database for IngredientID " + id + ": " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the table data by re-querying the database.
     */
    public void refreshTableData() {
        tableModel.setRowCount(0); // Clear existing data
        ItemIDs.clear();

        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(this.Query)) {
            while (rs.next()) {
                int id = rs.getInt("IngredientID");
                String name = rs.getString("Name");
                float stock = rs.getFloat("Stock");
                float maxstock = rs.getFloat("MaxStock");
                String units = rs.getString("Units");

                ItemIDs.add(id);
                tableModel.addRow(new Object[] { id, name, stock, maxstock, units });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching menu items: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}