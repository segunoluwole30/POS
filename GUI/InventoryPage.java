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

/**
 * Represents the inventory page in the Point of Sale (POS) system.
 * It includes functionality to display, add, update, and delete inventory items,
 * as well as suggest restocking for low-stock items.
 * 
 * @author Abhishek Bhattacharyya
 * @author Kaili Fogle
 * 
 */
public class InventoryPage extends JPanel {

    private Connection conn;
    private POS pos;

    private JPanel navbar;
    private JPanel mainPanel;
    private JTable inventoryTable;
    private JTable suggestionsTableDisplay;
    private DefaultTableModel tableModel;

    private List<Integer> itemIDs = new ArrayList<>();

    private String inventoryQuery = "SELECT * FROM ingredientsinventory ORDER BY ingredientid;";
    private String suggestionQuery = "SELECT * FROM ingredientsInventory WHERE stock / maxstock < 0.2 ORDER BY stock / maxstock ASC;";

    private SmartTable suggestionTable;

    /**
     * Constructs the inventory page with references to the database connection and the main POS instance.
     * Initializes UI components and loads inventory data.
     *
     * @param conn Database connection
     * @param pos Main POS instance
     */
    public InventoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        suggestionTable = new SmartTable(conn, suggestionQuery, true);
        suggestionTable.refreshTableData();
        initializeUI();
        refreshTableData(inventoryQuery);
        repaint();
    }

    /**
     * Initializes and arranges UI components on the panel.
     */
    private void initializeUI() {
        setBackground(Common.DARKCYAN);
        setLayout(new GridBagLayout());

        navbar = Utils.createHeaderPanel(pos);
        navbar.setPreferredSize(new Dimension(getWidth(), 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(navbar, gbc);

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

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Common.DARKCYAN);
        bodyPanel.setPreferredSize(new Dimension(Common.WIDTH * 15 / 16, Common.HEIGHT * 13 / 16));
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(bodyPanel, gbc);

        JPanel inventoryPanel = new JPanel(new GridBagLayout());
        inventoryPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bodyPanel.add(inventoryPanel, gbc);

        JTextArea inventoryTitle = new JTextArea("Inventory Report");
        inventoryTitle.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        inventoryTitle.setOpaque(false);
        inventoryTitle.setEditable(false);
        inventoryTitle.setForeground(Color.white);

        tableModel = new DefaultTableModel(new String[]{"Item ID", "Name", "Stock", "MaxStock", "Units"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setModel(tableModel);

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
                    insertNewItem(name, stock, maxstock, units);
                    refreshTableData(inventoryQuery);

                } else {
                    Object value = tableModel.getValueAt(row, column);
                    updateMenuItemInDatabase(id, column, value);
                }
            }
        });

        inventoryTable.setRowHeight(Common.HEIGHT / 16);
        inventoryTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        inventoryTable.getTableHeader().setFont(new Font("Times New Roman", Font.PLAIN, 16));

        JScrollPane InventoryScrollPane = new JScrollPane(inventoryTable);
        InventoryScrollPane.setPreferredSize(new Dimension(InventoryScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        inventoryPanel.add(inventoryTitle, gbc);
        gbc.gridy++;
        inventoryPanel.add(InventoryScrollPane, gbc);

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = inventoryTable.getSelectedRow() >= 0;
            }
        });

        JButton addButton = new JButton("Add New Row");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.addRow(new Object[]{"", "", "", "", ""});
                suggestionTable.refreshTableData();
            }
        });

        JButton deleteButton = new JButton("Delete Selected Item");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = inventoryTable.getSelectedRow();
                if (selectedRow != -1) {
                    Object id = tableModel.getValueAt(selectedRow, 0);
                    tableModel.removeRow(selectedRow);
                    deleteItemFromDatabase(id);
                    suggestionTable.refreshTableData();
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        inventoryPanel.add(addButton, gbc);

        gbc.gridy = 4;
        inventoryPanel.add(deleteButton, gbc);

        JPanel suggestionsPanel = new JPanel(new GridBagLayout());
        suggestionsPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(50, 0, 0, 0);
        bodyPanel.add(suggestionsPanel, gbc);

        JTextArea suggestionsTitle = new JTextArea("Restock Report");
        suggestionsTitle.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        suggestionsTitle.setOpaque(false);
        suggestionsTitle.setEditable(false);
        suggestionsTitle.setForeground(Color.white);

        suggestionsTableDisplay = new JTable(suggestionTable.tableModel);
        JScrollPane suggestionsTableScrollPane = new JScrollPane(suggestionsTableDisplay);
        suggestionsTableScrollPane.setPreferredSize(new Dimension(suggestionsTableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBackground(Color.GREEN);
        placeOrderButton.setPreferredSize(new Dimension(120, 40));
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String restockIDs = "";
                    if (suggestionTable.tableModel.getRowCount() >= 1) {
                        for (int i = 0; i < suggestionTable.tableModel.getRowCount() - 1; i++) {
                            restockIDs += suggestionTable.tableModel.getValueAt(i, 0) + ", ";
                        }
                        restockIDs += suggestionTable.tableModel.getValueAt(suggestionTable.tableModel.getRowCount() - 1, 0);

                        String restockQuery = "UPDATE ingredientsinventory SET stock = CASE WHEN ingredientid IN ("
                                + restockIDs + ") THEN maxstock ELSE stock END WHERE ingredientid IN (" + restockIDs
                                + ");";
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate(restockQuery);
                        suggestionTable.refreshTableData();
                        refreshTableData(inventoryQuery);
                    } else {
                        JOptionPane.showMessageDialog(null, "No items to restock.");
                    }
                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(null, "Error accessing Database.");
                }
            }
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        suggestionsPanel.add(suggestionsTitle, gbc);
        gbc.gridy++;
        suggestionsPanel.add(suggestionsTableScrollPane, gbc);
        gbc.gridy++;
        suggestionsPanel.add(placeOrderButton, gbc);
    }

    /**
     * Refreshes the header of the inventory page.
     */
    public void refreshHeader() {
        GridBagConstraints gbc = getConstraints(navbar);
        remove(navbar);

        navbar = Utils.createHeaderPanel(pos);
        add(navbar, gbc);
        revalidate();
        repaint();
    }

    /**
     * Retrieves and returns the GridBagConstraints of a specified component.
     *
     * @param component The component to get constraints for
     * @return GridBagConstraints of the specified component
     */
    private GridBagConstraints getConstraints(Component component) {
        LayoutManager layout = getLayout();
        if (layout instanceof GridBagLayout) {
            GridBagLayout gbl = (GridBagLayout) layout;
            return gbl.getConstraints(component);
        } else {
            return null;
        }
    }

    /**
     * Inserts a new item into the inventory database.
     *
     * @param name Item name
     * @param stock Item stock quantity
     * @param maxstock Maximum stock quantity for the item
     * @param units Measurement units of the item
     */
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
                    itemIDs.add(generatedKeys.getInt(1));
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
        suggestionTable.refreshTableData();
    }

    /**
     * Deletes the selected item from the database.
     */
    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                int DelID = itemIDs.get(selectedRow);
                deleteItemFromDatabase(DelID);
                refreshTableData(inventoryQuery);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }

    /**
     * Deletes an item from the inventory database based on the provided ID.
     *
     * @param itemId The ID of the item to delete
     */
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
        suggestionTable.refreshTableData();
    }

    /**
     * Updates an item's information in the database based on provided values.
     *
     * @param id     The ID of the item to update
     * @param column The column index of the information to update
     * @param value  The new value to update the item with
     */
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
            if (column == 2 || column == 3) {
                float newValue = Float.parseFloat(value.toString());
                pstmt.setObject(1, newValue);
            } else {
                pstmt.setObject(1, value);
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
        suggestionTable.refreshTableData();
    }

    /**
     * Refreshes the data displayed in the inventory table.
     *
     * @param query SQL query to fetch inventory data
     */
    public void refreshTableData(String query) {
        tableModel.setRowCount(0);
        itemIDs.clear();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("IngredientID");
                String name = rs.getString("Name");
                float stock = rs.getFloat("Stock");
                float maxstock = rs.getFloat("MaxStock");
                String units = rs.getString("Units");

                itemIDs.add(id);
                tableModel.addRow(new Object[]{id, name, stock, maxstock, units});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching menu items: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}