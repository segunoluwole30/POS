import java.sql.*;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryPage extends JPanel {

    private Connection con;
    private POS pos;
    private JPanel navbar;

    public InventoryPage(Connection con, POS pos) {
        this.con = con;
        this.pos = pos;
        initializeUI();
    }

    private List<String[]> requestInventoryTable(String sqlStatement) {
        List<String[]> tableOutput = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                String[] str = { String.valueOf(result.getInt("ingredientid")),
                        result.getString("name"),
                        String.valueOf(result.getInt("stock")),
                        String.valueOf(result.getInt("maxstock")),
                        result.getString("units") };
                tableOutput.add(str);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database.");
        }

        return tableOutput;
    }

    private void initializeUI() {
        // Set up panel that will hold all page contents
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Common.DARKCYAN);
        mainPanel.setPreferredSize(new Dimension(Common.WIDTH, Common.HEIGHT));
        navbar = Utils.createHeaderPanel(pos);
        navbar.setPreferredSize(new Dimension(getWidth(), 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set up body in which the bulk of information will be placed
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.LIGHT_GRAY);
        bodyPanel.setPreferredSize(new Dimension(Common.WIDTH * 7 / 8, Common.HEIGHT * 7 / 9));

        // Set up panels that will display title, table, and suggested restocking orders

        // Inventory Report Title Panel
        GridBagConstraints constraintsTitle = new GridBagConstraints();
        constraintsTitle.gridx = 0;
        constraintsTitle.gridy = 1;
        constraintsTitle.weightx = 1.0;
        constraintsTitle.anchor = GridBagConstraints.PAGE_START;
        JPanel titlePanel = new JPanel();
        JTextArea title = new JTextArea();
        title.setText("Inventory Report");
        title.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        title.setOpaque(false);
        titlePanel.setOpaque(false);
        title.setEditable(false);

        // Table Scroll Pane
        GridBagConstraints constraintsTable = new GridBagConstraints();
        constraintsTable.gridx = 0;
        constraintsTable.gridy = 1;
        constraintsTable.insets = new Insets(0, 20, 50, 20);
        constraintsTable.weightx = 1.0;
        constraintsTable.fill = GridBagConstraints.BOTH;
        constraintsTable.anchor = GridBagConstraints.CENTER;
        List<String[]> tableData = requestInventoryTable("SELECT * FROM ingredientsinventory;");
        String[][] rowEntries = new String[tableData.size()][];
        for (int i = 0; i < tableData.size(); i++) {
            rowEntries[i] = tableData.get(i);
        }
        String[] columnNames = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };
        JTable table = new JTable(rowEntries, columnNames);
        table.setOpaque(false);
        table.setEnabled(false);
        table.setRowHeight(Common.HEIGHT / 16);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowSelectionInterval(1, 1);
        JScrollPane tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        tableScrollPane.setPreferredSize(new Dimension(tableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

        // Next Order Suggestion Title Panel
        GridBagConstraints constraintsTitle2 = new GridBagConstraints();
        constraintsTitle2.gridx = 0;
        constraintsTitle2.gridy = 2;
        constraintsTitle2.weightx = 1.0;
        constraintsTitle2.anchor = GridBagConstraints.CENTER;
        JPanel titlePanel2 = new JPanel();
        JTextArea title2 = new JTextArea();
        title2.setText("Next Order Suggestion");
        title2.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        title2.setOpaque(false);
        titlePanel2.setOpaque(false);
        title2.setEditable(false);

        // Next Order Suggestion Scroll Pane
        GridBagConstraints constraintsTable2 = new GridBagConstraints();
        constraintsTable2.gridx = 0;
        constraintsTable2.gridy = 3;
        constraintsTable2.weightx = 1.0;
        constraintsTable2.insets = new Insets(0, 20, 0, 0);
        constraintsTable2.fill = GridBagConstraints.BOTH;
        constraintsTable2.anchor = GridBagConstraints.CENTER;
        List<String[]> tableData2 = requestInventoryTable(
                "SELECT * FROM ingredientsinventory ORDER BY stock / maxstock ASC LIMIT 10;");
        String[][] rowEntries2 = new String[tableData2.size()][];
        for (int i = 0; i < tableData2.size(); i++) {
            rowEntries2[i] = tableData2.get(i);
        }
        String[] columnNames2 = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };
        JTable table2 = new JTable(rowEntries2, columnNames2);
        table2.setOpaque(false);
        table2.setEnabled(false);
        table2.setRowHeight(Common.HEIGHT / 16);
        table2.setFont(new Font("Arial", Font.PLAIN, 16));
        table2.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane tableScrollPane2 = new JScrollPane();
        tableScrollPane2.setViewportView(table2);
        tableScrollPane2.setPreferredSize(new Dimension(tableScrollPane2.getPreferredSize().width, Common.HEIGHT / 4));

        // Edit Order and Send Order Buttons
        GridBagConstraints constraintsOrderButtons = new GridBagConstraints();
        constraintsOrderButtons.gridx = 1;
        constraintsOrderButtons.gridy = 3;
        constraintsOrderButtons.weighty = 1.0;
        constraintsOrderButtons.insets = new Insets(0, 0, 0, 20);
        JPanel orderButtonsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraintsEditOrderButton = new GridBagConstraints();
        constraintsEditOrderButton.gridx = 0;
        constraintsEditOrderButton.gridy = 0;
        constraintsEditOrderButton.weighty = 1.0;
        JButton editOrderButton = new JButton("Edit Order");
        editOrderButton.setBackground(Color.GRAY);
        editOrderButton.setPreferredSize(new Dimension(120, 120));
        editOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        GridBagConstraints constraintsPlaceOrderButton = new GridBagConstraints();
        constraintsPlaceOrderButton.gridx = 0;
        constraintsPlaceOrderButton.gridy = 1;
        constraintsPlaceOrderButton.weighty = 1.0;
        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBackground(Color.GREEN);
        placeOrderButton.setPreferredSize(new Dimension(120, 120));
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String restockIDs = "";
                for (int i = 0; i < tableData2.size() - 1; i++) {
                    restockIDs += tableData2.get(i)[0] + ", ";
                }
                restockIDs += tableData2.get(tableData2.size() - 1);

                String restockQuery = "Update ingredientsinventory SET stock = CASE WHEN ingredientid IN (" + restockIDs
                        + ") THEN maxstock ELSE stock END WHERE ingredientid IN (" + restockIDs + ");";

                try {
                    Statement stmt = con.createStatement();
                    stmt.executeQuery(restockQuery);
                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(null, "Error accessing Database.");
                }
            }
        });

        // Add everything together
        add(mainPanel, constraints);
        mainPanel.add(bodyPanel, constraints);
        bodyPanel.add(titlePanel, constraintsTitle);
        titlePanel.add(title);
        bodyPanel.add(tableScrollPane, constraintsTable);
        bodyPanel.add(titlePanel2, constraintsTitle2);
        titlePanel2.add(title2);
        bodyPanel.add(tableScrollPane2, constraintsTable2);
        bodyPanel.add(orderButtonsPanel, constraintsOrderButtons);
        orderButtonsPanel.add(editOrderButton, constraintsEditOrderButton);
        orderButtonsPanel.add(placeOrderButton, constraintsPlaceOrderButton);
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

    public static void main(String[] args) {
        InventoryPage p = new InventoryPage(null, null);
        JFrame f = new JFrame();
        f.setSize(1600, 900);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(p);
        f.setVisible(true);
        f.setResizable(true);
    }
}
