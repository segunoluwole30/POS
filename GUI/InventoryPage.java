import java.sql.*;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryPage extends JPanel {
    
    private Connection conn;
    private POS pos;
    private JPanel navbar;
    private JPanel mainPanel;

    public InventoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();
    }

    private List<String[]> requestInventoryTable(String sqlStatement){
        List<String[]> tableOutput = new ArrayList<>();
        try {
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(sqlStatement);
        while (result.next()) {
            String[] str = {String.valueOf(result.getInt("ingredientid")), 
                            result.getString("name"), 
                            String.valueOf(result.getInt("stock")), 
                            String.valueOf(result.getInt("maxstock")), 
                            result.getString("units")};
            tableOutput.add(str);
        }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }

        return tableOutput;
    }

    private void initializeUI() {
        // Use GridBayLayout for page layout
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
        bodyPanel.setPreferredSize(new Dimension(Common.WIDTH * 15/16, Common.HEIGHT * 13/16));
        //gbc = new GridBagConstraints(); //By default, gridbagconstraints will place a component in the middle
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

        // Create label and table for Inventory Report
        // > text area
        JTextArea inventoryTitle = new JTextArea("Inventory Report");
        inventoryTitle.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        inventoryTitle.setOpaque(false);
        inventoryTitle.setEditable(false);
        // > get table data
        List<String[]> tableData = requestInventoryTable("SELECT * FROM ingredientsinventory;");
        String[][] rowEntries = new String[tableData.size()][];
        for(int i = 0; i < tableData.size(); i++){
            rowEntries[i] = tableData.get(i);
        }
        String[] columnEntries = {"Ingredient ID", "Name", "Current Stock", "Max Stock", "Units"};
        // > table
        JTable inventoryTable = new JTable(rowEntries, columnEntries);
        inventoryTable.setEnabled(false);
        inventoryTable.setRowHeight(Common.HEIGHT / 16);
        inventoryTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        inventoryTable.getTableHeader().setFont(new Font("Times New Roman", Font.PLAIN, 16));
        // > scroll pane for table
        JScrollPane inventoryTableScrollPane = new JScrollPane();
        inventoryTableScrollPane.setViewportView(inventoryTable);
        inventoryTableScrollPane.setPreferredSize(new Dimension(inventoryTableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));
        // > set gbc constraints to be used for both
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        // > display components
        inventoryPanel.add(inventoryTitle, gbc);
        gbc.gridy++;
        inventoryPanel.add(inventoryTableScrollPane, gbc);

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
        tableData = requestInventoryTable("SELECT * FROM ingredientsinventory ORDER BY stock / maxstock ASC LIMIT 10;");
        rowEntries = new String[tableData.size()][];
        for(int i = 0; i < tableData.size(); i++){
            rowEntries[i] = tableData.get(i);
        }
        // > table
        JTable suggestionsTable = new JTable(rowEntries, columnEntries);
        suggestionsTable.setEnabled(false);
        suggestionsTable.setRowHeight(Common.HEIGHT / 16);
        suggestionsTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        suggestionsTable.getTableHeader().setFont(new Font("Times New Roman", Font.PLAIN, 16));
        // > scroll pane for table
        JScrollPane suggestionsTableScrollPane = new JScrollPane();
        suggestionsTableScrollPane.setViewportView(suggestionsTable);
        suggestionsTableScrollPane.setPreferredSize(new Dimension(suggestionsTableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));
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
                    for(int i = 0; i < tableDataCopy.size() - 1; i++){
                        restockIDs += tableDataCopy.get(i)[0] + ", ";
                    }
                    restockIDs += tableDataCopy.get(tableDataCopy.size() - 1);

                    String restockQuery = "UPDATE ingredientsinventory SET stock = CASE WHEN ingredientid IN (" + restockIDs + ") THEN stock + 1 ELSE stock END WHERE ingredientid IN (" + restockIDs + ");";

                    Statement stmt = conn.createStatement();
                    stmt.executeQuery(restockQuery);
                } 
                catch (Exception ee) {
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
        
        // // Next Order Suggestion Title Panel
        // GridBagConstraints constraintsTitle2 = new GridBagConstraints();
        // constraintsTitle2.gridx = 0;
        // constraintsTitle2.gridy = 2;
        // constraintsTitle2.weightx = 1.0;
        // constraintsTitle2.anchor = GridBagConstraints.CENTER;
        // JPanel titlePanel2 = new JPanel();
        // JTextArea title2 = new JTextArea();
        // title2.setText("Next Order Suggestion");
        // title2.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        // title2.setOpaque(false);
        // titlePanel2.setOpaque(false);
        // title2.setEditable(false);
        
        // // Next Order Suggestion Scroll Pane
        // GridBagConstraints constraintsTable2 = new GridBagConstraints();
        // constraintsTable2.gridx = 0;
        // constraintsTable2.gridy = 3;
        // constraintsTable2.weightx = 1.0;
        // constraintsTable2.insets = new Insets(0, 20, 0, 0);
        // constraintsTable2.fill = GridBagConstraints.BOTH;
        // constraintsTable2.anchor = GridBagConstraints.CENTER;
        // List<String[]> tableData2 = requestInventoryTable("SELECT * FROM ingredientsinventory ORDER BY stock / maxstock ASC LIMIT 10;");
        // String[][] rowEntries2 = new String[tableData2.size()][];
        // for(int i = 0; i < tableData2.size(); i++){
        //     rowEntries2[i] = tableData2.get(i);
        // }
        // String[] columnNames2 = {"Ingredient ID", "Name", "Current Stock", "Max Stock", "Units"};
        // JTable table2 = new JTable(rowEntries2, columnNames2);
        // table2.setOpaque(false);
        // table2.setEnabled(false);
        // table2.setRowHeight(Common.HEIGHT / 16);
        // table2.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        // table2.getTableHeader().setFont(new Font("Times New Roman", Font.PLAIN, 16));
        // JScrollPane tableScrollPane2 = new JScrollPane();
        // tableScrollPane2.setViewportView(table2);
        // tableScrollPane2.setPreferredSize(new Dimension(tableScrollPane2.getPreferredSize().width, Common.HEIGHT / 4));

        // // Edit Order and Send Order Buttons
        // GridBagConstraints constraintsOrderButtons = new GridBagConstraints();
        // constraintsOrderButtons.gridx = 1;
        // constraintsOrderButtons.gridy = 3;
        // constraintsOrderButtons.weighty = 1.0;
        // constraintsOrderButtons.insets = new Insets(0, 0, 0, 20);
        // JPanel orderButtonsPanel = new JPanel(new GridBagLayout());

        // GridBagConstraints constraintsEditOrderButton = new GridBagConstraints();
        // constraintsEditOrderButton.gridx = 0;
        // constraintsEditOrderButton.gridy = 0;
        // constraintsEditOrderButton.weighty = 1.0;
        // JButton editOrderButton = new JButton("Edit Order");
        // editOrderButton.setBackground(Color.GRAY);
        // editOrderButton.setPreferredSize(new Dimension(120, 120));
        // editOrderButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {

        //     }
        // });

        // // Add everything together
        // add(mainPanel, constraintsMain);
        // mainPanel.add(bodyPanel, constraintsBody);
        // bodyPanel.add(titlePanel, constraintsTitle);
        // titlePanel.add(title);
        // bodyPanel.add(tableScrollPane, constraintsTable);
        // bodyPanel.add(titlePanel2, constraintsTitle2);
        // titlePanel2.add(title2);
        // bodyPanel.add(tableScrollPane2, constraintsTable2);
        // bodyPanel.add(orderButtonsPanel, constraintsOrderButtons);
        // orderButtonsPanel.add(editOrderButton, constraintsEditOrderButton);
        // orderButtonsPanel.add(placeOrderButton, constraintsPlaceOrderButton);
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

    // public static void main(String[] args) {
    //     JFrame f = new JFrame();
    //     f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //     InventoryPage p = new InventoryPage(conn, pos);
    //     f.getContentPane().add(p);

    //     f.pack();
    //     f.setLocationRelativeTo(null);
    //     f.setVisible(true);
    // }
}
