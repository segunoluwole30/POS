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
    private JPanel navbar;
    private JPanel mainPanel;

    // Data Members
    private List<Integer> ItemIds = new ArrayList<>(); // store item id's for database operations
    private List<String[]> tableData = new ArrayList<>();
    private String getItemsSqlStatement = "SELECT * FROM ingredientsinventory ORDER BY ingredientid;";
    
    // UI Elements
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JTable suggestionsTable;

    public InventoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();
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

    private void initializeUI() {
        // Use GridBagLayout for page layout
        setBackground(Common.DARKCYAN);
        setLayout(new GridBagLayout());

        // Create the top navbar
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

        // --------------------------------------------------------------------------------------------------------------
        // TODO: Make Inventory Editable

        // Get table data
        tableData = requestInventoryTable("SELECT * FROM ingredientsinventory ORDER BY ingredientid;");
        String[][] rowEntries = new String[tableData.size()][];
        for (int i = 0; i < tableData.size(); i++) {
            rowEntries[i] = tableData.get(i);
        }
        String[] columnEntries = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };

        // Create table
        tableModel = new DefaultTableModel(rowEntries, columnEntries) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

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
                    //insertNewItem(name, stock, maxstock, units);
                    refreshTableData(getItemsSqlStatement);
                } else {
                    // Existing row, handle the update operation
                    Object value = tableModel.getValueAt(row, column);
                    //updateItemInDatabase(id, column, value);
                }
            }
        });

        inventoryTable = new JTable(tableModel);
        inventoryTable.setModel(tableModel);

        inventoryTable.setEnabled(false);
        inventoryTable.setRowHeight(Common.HEIGHT / 16);
        inventoryTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        inventoryTable.getTableHeader().setFont(new Font("Times New Roman", Font.PLAIN, 16));

        // > scroll pane for table
        JScrollPane inventoryTableScrollPane = new JScrollPane();
        inventoryTableScrollPane.setViewportView(inventoryTable);
        inventoryTableScrollPane
                .setPreferredSize(new Dimension(inventoryTableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

        // > set gbc constraints to be used for both
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // > display components
        inventoryPanel.add(inventoryTitle, gbc);
        gbc.gridy++;
        inventoryPanel.add(inventoryTableScrollPane, gbc);

        // addButton = new JButton("Add");
        // addButton.addActionListener(e -> addMenuItem());

        // deleteButton = new JButton("Delete");
        // deleteButton.addActionListener(e -> deleteMenuItem());

        // --------------------------------------------------------------------------------

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
        JTextArea suggestionsTitle = new JTextArea("Next Order Suggestions");
        suggestionsTitle.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        suggestionsTitle.setOpaque(false);
        suggestionsTitle.setEditable(false);

        // > get table data
        tableData = requestInventoryTable("SELECT * FROM ingredientsInventory WHERE stock / maxstock < 0.2 ORDER BY stock / maxstock ASC;");
        rowEntries = new String[tableData.size()][];
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

                    // TODO: Update query to properly restock
                    String restockQuery = "UPDATE ingredientsinventory SET stock = CASE WHEN ingredientid IN ("
                            + restockIDs + ") THEN maxstock ELSE stock END WHERE ingredientid IN (" + restockIDs
                            + ");";

                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(restockQuery);
                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(null, "Error accessing Database.");
                }
            }
        });
        // > set gbc constraints to be used for both
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        // > display components
        suggestionsPanel.add(suggestionsTitle, gbc);
        gbc.gridy++;
        suggestionsPanel.add(suggestionsTableScrollPane, gbc);
        gbc.gridy++;
        suggestionsPanel.add(placeOrderButton, gbc);

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
                    String id = (String) tableModel.getValueAt(selectedRow, 0); // Assuming first column is ID
                    tableModel.removeRow(selectedRow);
                    // Add code to delete the row from the database using `id`
                }
            }
        });

        gbc.gridx = 0; // Adjust gridx and gridy as needed for layout
        gbc.gridy = 3; // Position where the buttons should be in the grid
        inventoryPanel.add(addButton, gbc); // Or add to another panel as desired

        gbc.gridx = 1; // Adjust for layout
        inventoryPanel.add(deleteButton, gbc); // Or add to another panel
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

    public void refreshTableData(String sqlStatement) {
        tableModel.setRowCount(0); // Clear existing data
        ItemIds.clear();
        tableData.clear();

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
                ItemIds.add(Integer.parseInt(str[0]));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
            e.printStackTrace();
        }
    }

    // public static void main(String[] args) {
    // JFrame f = new JFrame();
    // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // InventoryPage p = new InventoryPage(conn, pos);
    // f.getContentPane().add(p);
    // f.pack();
    // f.setLocationRelativeTo(null);
    // f.setVisible(true);
    // }
}