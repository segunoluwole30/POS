import java.sql.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryPage extends JPanel {

    private Connection conn;
    private POS pos;

    // UI Elements
    private JPanel navbar;
    private JPanel mainPanel;
    private JTable inventoryTable;
    private JTable suggestionsTable;
    private JButton addButton, deleteButton;
    private DefaultTableModel tableModel;

    // Data Members
    private List<Integer> ItemIDs = new ArrayList<>(); // store menu item id's for database operations
    private List<String[]> tableData = new ArrayList<>();

    private String InventoryQuery = "SELECT * FROM ingredientsinventory ORDER BY ingredientid;";
    private String SuggestionQuery = "";

    // Constructor
    public InventoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();
        refreshTableData(InventoryQuery);
        repaint();
    }

    private void initializeUI() {
        // Use GridBagLayout for page layout
        setBackground(Common.DARKCYAN);
        setLayout(new GridBagLayout());

        // Creating the top navbar
        navbar = Utils.createHeaderPanel(pos);
        navbar.setPreferredSize(new Dimension(getWidth(), 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(navbar, gbc);

        // Create the main panel which will store everything
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Common.DARKCYAN);
        mainPanel.setPreferredSize(new Dimension(Common.WIDTH, Common.HEIGHT));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(mainPanel, gbc);

        // Create the body which will store tables and buttons
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.lightGray);
        bodyPanel.setPreferredSize(new Dimension(Common.WIDTH * 15 / 16, Common.HEIGHT * 13 / 16));
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(bodyPanel, gbc);

        // Create panel for Inventory Report (Inventory Report)
        JPanel inventoryPanel = new JPanel(new GridBagLayout());
        inventoryPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bodyPanel.add(inventoryPanel, gbc);

        // Create label for Inventory Report
        JTextArea inventoryTitle = new JTextArea("Inventory Report");
        inventoryTitle.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        inventoryTitle.setOpaque(false);
        inventoryTitle.setEditable(false);

        tableModel = new DefaultTableModel(new String[] { "Item ID", "Name", "Stock", "MaxStock", "Units" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setModel(tableModel);

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
                    refreshTableData(InventoryQuery);

                } else {
                    // Existing row, handle the update operation
                    Object value = tableModel.getValueAt(row, column);
                    updateMenuItemInDatabase(id, column, value);
                }
            }
        });

        inventoryTable.setRowHeight(Common.HEIGHT / 16);
        inventoryTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        inventoryTable.getTableHeader().setFont(new Font("Times New Roman", Font.PLAIN, 16));

        // > scroll pane for table
        JScrollPane InventoryScrollPane = new JScrollPane(inventoryTable);
        InventoryScrollPane
                .setPreferredSize(new Dimension(InventoryScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

        // > set gbc constraints to be used for both
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // > display components
        inventoryPanel.add(inventoryTitle, gbc);
        gbc.gridy++;
        inventoryPanel.add(InventoryScrollPane, gbc);

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = inventoryTable.getSelectedRow() >= 0;
                // editIngredientsButton.setEnabled(rowSelected);
            }
        });

        JButton addButton = new JButton("Add New Row");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.addRow(new Object[] { "", "", "", "", "" }); // Adjust based on your data structure
            }
        });

        JButton deleteButton = new JButton("Delete Selected Item");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = inventoryTable.getSelectedRow();
                if (selectedRow != -1) {
                    Object id = tableModel.getValueAt(selectedRow, 0); // Assuming first column is ID
                    tableModel.removeRow(selectedRow);
                    deleteItemFromDatabase(id);
                    // Add code to delete the row from the database using `id`
                }
            }
        });

        gbc.gridx = 0; // Adjust gridx and gridy as needed for layout
        gbc.gridy = 3; // Position where the buttons should be in the grid
        inventoryPanel.add(addButton, gbc); // Or add to another panel as desired

        gbc.gridx = 1; // Adjust for layout
        inventoryPanel.add(deleteButton, gbc); // Or add to another panel

        // Create panel for Restocking Suggestions (Next Order Suggestion)
        JPanel suggestionsPanel = new JPanel(new GridBagLayout());
        suggestionsPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(50, 0, 0, 0);
        bodyPanel.add(suggestionsPanel, gbc);

        // Create label and table for Inventory Report
        // > text area
        JTextArea suggestionsTitle = new JTextArea("Restock Report");
        suggestionsTitle.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        suggestionsTitle.setOpaque(false);
        suggestionsTitle.setEditable(false);
        // > get table data
        tableData = requestInventoryTable(
                "SELECT * FROM ingredientsInventory WHERE stock / maxstock < 0.6 ORDER BY stock / maxstock ASC;");
        String[][] rowEntries = new String[tableData.size()][];
        String[] columnEntries = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };
        for (int i = 0; i < tableData.size(); i++) {
            rowEntries[i] = tableData.get(i);
        }

        // > table
        suggestionsTable = new JTable(rowEntries, columnEntries);
        suggestionsTable.setEnabled(false);
        suggestionsTable.setRowHeight(Common.HEIGHT / 16);
        suggestionsTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        suggestionsTable.getTableHeader().setFont(new Font("Times New Roman", Font.PLAIN, 16));
        // > scroll pane for table
        JScrollPane suggestionsTableScrollPane = new JScrollPane();
        suggestionsTableScrollPane.setViewportView(suggestionsTable);
        suggestionsTableScrollPane.setPreferredSize(
                new Dimension(suggestionsTableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));
        // > button for updating stock
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBackground(Color.GREEN);
        placeOrderButton.setPreferredSize(new Dimension(120, 40));
        final List<String[]> tableDataCopy = new ArrayList<>(tableData);
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String restockIDs = "";
                    for (int i = 0; i < tableDataCopy.size() - 1; i++) {
                        restockIDs += tableDataCopy.get(i)[0] + ", ";
                    }
                    restockIDs += tableDataCopy.get(tableDataCopy.size() - 1)[0];
                    String restockQuery = "UPDATE ingredientsinventory SET stock = CASE WHEN ingredientid IN ("
                            + restockIDs + ") THEN maxstock ELSE stock END WHERE ingredientid IN (" + restockIDs
                            + ");";
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(restockQuery);

                    tableData = requestInventoryTable(
                            "SELECT * FROM ingredientsInventory WHERE stock / maxstock < 0.6 ORDER BY stock / maxstock ASC;");
                    String[][] rowEntries = new String[tableData.size()][];
                    String[] columnEntries = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };
                    for (int i = 0; i < tableData.size(); i++) {
                        rowEntries[i] = tableData.get(i);
                    }
                    suggestionsTable = new JTable(rowEntries, columnEntries);

                    refreshTableData(InventoryQuery);
                    inventoryTable.repaint();
                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(null, "Error accessing Database.");
                }
            }
        });
        // > set gbc constraints to be used for the Inventory Report
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        // > display components
        suggestionsPanel.add(suggestionsTitle, gbc);
        gbc.gridy++;
        suggestionsPanel.add(suggestionsTableScrollPane, gbc);
        gbc.gridy++;
        suggestionsPanel.add(placeOrderButton, gbc);

    }

    public void refreshHeader() {
        // Remove the old navbar using GridBagConstraints
        GridBagConstraints gbc = getConstraints(navbar);
        remove(navbar);

        // Directly update the class field `navbar` with a new header panel
        navbar = Utils.createHeaderPanel(pos);

        // Add the updated navbar to the panel using GridBagConstraints
        add(navbar, gbc);

        // Revalidate and repaint to ensure UI updates are displayed
        revalidate();
        repaint();
    }

    private List<String[]> requestInventoryTable(String sqlStatement) {
        List<String[]> tableData = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                String[] str = { String.valueOf(result.getInt("ingredientid")),
                        result.getString("name"),
                        String.valueOf(result.getInt("stock")),
                        String.valueOf(result.getInt("maxstock")),
                        result.getString("units") };
                tableData.add(str);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }

        return tableData;
    }

    // Helper method to get GridBagConstraints of a component
    private GridBagConstraints getConstraints(Component component) {
        LayoutManager layout = getLayout();
        if (layout instanceof GridBagLayout) {
            GridBagLayout gbl = (GridBagLayout) layout;
            return gbl.getConstraints(component);
        } else {
            return null;
        }
    }

    private void insertNewItem(String name, float stock, float maxstock, String units) {
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

    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                int DelID = ItemIDs.get(selectedRow); // Get the ID from the list
                deleteItemFromDatabase(DelID);
                refreshTableData(InventoryQuery);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }

    private void deleteItemFromDatabase(Object itemId) {
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

    private void updateMenuItemInDatabase(Object id, int column, Object value) {
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

    public void refreshTableData(String query) {
        tableModel.setRowCount(0); // Clear existing data
        ItemIDs.clear();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
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