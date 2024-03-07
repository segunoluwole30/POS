import java.sql.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a customizable and interactive table component for displaying
 * and manipulating database records. It supports functionalities such as insert, update,
 * and delete operations directly through the UI.
 */
public class SmartTable extends JPanel {

    private Connection conn;
    public DefaultTableModel tableModel;
    public JTable table;
    private List<Integer> ItemIDs = new ArrayList<>();
    private String Query = "";
    private Boolean SoftMode;

    /**
     * Constructs a new SmartTable instance.
     *
     * @param conn   the database connection
     * @param Query  the SQL query to fetch data for the table
     * @param IsSoft indicates whether the table is in soft mode (i.e., changes do not affect the database)
     */
    public SmartTable(Connection conn, String Query, Boolean IsSoft) {
        this.Query = Query;
        this.conn = conn;
        SoftMode = IsSoft;
        this.tableModel = new DefaultTableModel(new String[]{"Item ID", "Name", "Stock", "MaxStock", "Units"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        table = new JTable(tableModel);
        table.setModel(tableModel);

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                Object id = tableModel.getValueAt(row, 0);

                if (id == null || id.toString().isEmpty()) {
                    String name = tableModel.getValueAt(row, 1).toString();
                    float stock = Float.parseFloat(tableModel.getValueAt(row, 2).toString());
                    float maxstock = Float.parseFloat(tableModel.getValueAt(row, 3).toString());
                    String units = tableModel.getValueAt(row, 4).toString();

                    if (!SoftMode)
                        insertNewItem(name, stock, maxstock, units);

                    refreshTableData();

                } else {
                    Object value = tableModel.getValueAt(row, column);
                    if (!SoftMode)
                        updateMenuItemInDatabase(id, column, value);
                }
            }
        });
    }

    /**
     * Inserts a new item into the database.
     *
     * @param name     the name of the new item
     * @param stock    the stock quantity of the new item
     * @param maxstock the maximum stock quantity of the new item
     * @param units    the units of the new item
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
                    ItemIDs.add(generatedKeys.getInt(1));
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
     * Initiates the deletion process for the selected item in the table.
     */
    public void deleteItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                int DelID = ItemIDs.get(selectedRow);
                deleteItemFromDatabase(DelID);
                refreshTableData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }

    /**
     * Deletes an item from the database based on its ID.
     *
     * @param itemId the ID of the item to be deleted
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
     * Updates the details of an item in the database based on the user's modifications in the table.
     *
     * @param id     the ID of the item to be updated
     * @param column the column index indicating which attribute to update
     * @param value  the new value for the specified attribute
     */
    public void updateMenuItemInDatabase(Object id, int column, Object value) {
        String columnName;
        String sql;
        switch (column) {
            case 1:
                columnName = "Name";
                break;
            case 2:
                columnName = "Stock";
                break;
            case 3:
                columnName = "MaxStock";
                break;
            case 4:
                columnName = "Units";
                break;
            default:
                throw new IllegalArgumentException("Invalid column index");
        }
        sql = "UPDATE IngredientsInventory SET " + columnName + " = ? WHERE IngredientID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, value);
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
     * Refreshes the data displayed in the table according to the original or updated query.
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
                tableModel.addRow(new Object[]{id, name, stock, maxstock, units});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching menu items: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
