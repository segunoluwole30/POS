import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A dialog for viewing, adding, editing, and deleting menu items. This dialog
 * allows users to manage
 * menu items including their names, prices, and types, with functionalities to
 * interact with
 * a database for persistent storage of menu item data. All database access
 * errors are handled internally,
 * presenting user-friendly error messages as necessary.
 * 
 * @author Segun Oluwole
 */
public class ViewMenuItemsDialog extends JDialog {
    private JTable table;
    private JButton addButton, deleteButton, editIngredientsButton;
    private DefaultTableModel tableModel;
    private List<Integer> menuItemIds = new ArrayList<>(); // store menu item id's for database operations
    private Connection conn;

    /**
     * Constructs a ViewMenuItemsDialog which serves as a management interface for
     * menu items.
     *
     * @param owner the Frame from which the dialog is displayed
     * @param conn  the Connection object that represents the database connection
     */
    public ViewMenuItemsDialog(Frame owner, Connection conn) {
        super(owner, "Menu Items", true);
        this.conn = conn;
        initializeUI();
        refreshTableData();
        repaint();
    }

    /**
     * Initializes the user interface components and layouts for the dialog. It sets
     * up the table model,
     * buttons, and action listeners for interacting with menu items.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[] { "Name", "Price", "Type" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
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

        editIngredientsButton = new JButton("Edit Ingredients");
        editIngredientsButton.addActionListener(e -> editIngredients());
        editIngredientsButton.setEnabled(false); // Initially disabled
        buttonPanel.add(editIngredientsButton);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = table.getSelectedRow() >= 0;
                editIngredientsButton.setEnabled(rowSelected);
            }
        });

        add(buttonPanel, BorderLayout.SOUTH);
        setSize(500, 300);
    }

    /**
     * Prompts the user to input details for a new menu item and adds it to the
     * database. Input validation
     * is performed, and any errors during database operations are handled by
     * displaying error messages.
     */
    private void addMenuItem() {
        String name = JOptionPane.showInputDialog(this, "Enter item name:");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String priceAsString = JOptionPane.showInputDialog(this, "Enter item price:");
        float price;
        try {
            price = Float.parseFloat(priceAsString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] types = { "Entree", "Side", "Drink", "Dessert" }; // Example types, adjust as necessary
        String type = (String) JOptionPane.showInputDialog(this, "Select item type:",
                "Item Type", JOptionPane.QUESTION_MESSAGE,
                null, types, types[0]);
        if (type == null) {
            JOptionPane.showMessageDialog(this, "Item type is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        insertNewItem(name, price, type);

        // Refresh the table to show the newly added item
        refreshTableData();
    }

    /**
     * Inserts a new item into the database with the specified name, price, and
     * type. Handles any database
     * errors by displaying appropriate error messages to the user.
     *
     * @param name  the name of the new menu item
     * @param price the price of the new menu item
     * @param type  the type of the new menu item
     */
    private void insertNewItem(String name, float price, String type) {
        String sql = "INSERT INTO MenuItems (Name, Price, Type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setFloat(2, price);
            pstmt.setString(3, type);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    menuItemIds.add(generatedKeys.getInt(1)); // Store the new MenuItemID
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
     * Deletes the selected menu item from the database after confirming with the
     * user. Handles any database
     * errors by displaying appropriate error messages to the user.
     */
    private void deleteMenuItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                int menuItemId = menuItemIds.get(selectedRow); // Get the ID from the list
                deleteItemFromDatabase(menuItemId);
                refreshTableData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }

    /**
     * Deletes a menu item from the database using its item ID. Handles any database
     * errors by displaying
     * appropriate error messages to the user.
     *
     * @param itemId the ID of the menu item to be deleted
     */
    private void deleteItemFromDatabase(Object itemId) {
        // First, delete any associated ingredients from the MenuItemIngredients table
        String deleteAssociatedIngredientsSql = "DELETE FROM MenuItemIngredients WHERE MenuItemID = ?";
        try (PreparedStatement pstmtIngredients = conn.prepareStatement(deleteAssociatedIngredientsSql)) {
            pstmtIngredients.setInt(1, (Integer) itemId);
            pstmtIngredients.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting associated ingredients: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return; // Return early if there was a problem deleting associated ingredients
        }

        // Then, delete the item from the MenuItems table
        String sql = "DELETE FROM MenuItems WHERE MenuItemID = ?";
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
     * Updates a menu item in the database based on user edits. Handles any database
     * errors by displaying
     * appropriate error messages to the user.
     *
     * @param id     the ID of the menu item to update
     * @param column the column index indicating which attribute to update
     * @param value  the new value for the specified attribute
     */
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

    /**
     * Opens a dialog for editing the ingredients of the selected menu item. Any
     * errors during the operation
     * are handled internally, ensuring a robust user experience.
     */
    private void editIngredients() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Integer menuItemId = menuItemIds.get(selectedRow); // Get MenuItemID from the list
            IngredientsDialog ingredientsDialog = new IngredientsDialog(
                    (Frame) SwingUtilities.getWindowAncestor(ViewMenuItemsDialog.this), conn, menuItemId);
            ingredientsDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a menu item first.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Refreshes the table data by fetching updated menu items from the database and
     * displaying them. Handles
     * any database errors by displaying appropriate error messages to the user.
     */
    public void refreshTableData() {
        tableModel.setRowCount(0); // Clear existing data
        menuItemIds.clear();

        String query = "SELECT * FROM MenuItems;";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("MenuItemID");
                String name = rs.getString("Name");
                float price = rs.getFloat("Price");
                String type = rs.getString("Type");

                menuItemIds.add(id);
                tableModel.addRow(new Object[] { name, price, type });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching menu items: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
