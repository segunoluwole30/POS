import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class InventoryPage extends JPanel {

    private static Connection conn;
	private static POS pos;
    private JTable table;
    private JButton refresh;
    private JPanel navbar;
    private JPanel mainPanel;

    public InventoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        setupUI();
    }

    private List<String[]> requestInventoryTable(String sqlStatement) {
        List<String[]> tableOutput = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
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

	private void setupUI() {
		// Boilerplate code to setup layout
        setBackground(Common.DARKCYAN);
		setLayout(new BorderLayout());

		navbar = Utils.createHeaderPanel(pos);
		navbar.setPreferredSize(new Dimension(getWidth(), 50));
		add(navbar, BorderLayout.NORTH);
		
  
        //loadHistory();

        //refresh = new JButton("Refresh Report");        
        //refresh.addActionListener(e -> loadHistory());

        // Main Panel contains table and refresh button
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(table));
        mainPanel.setBackground(Common.DARKCYAN);

        // Current Inventory Table
        table = new JTable();
        List<String[]> tableData = requestInventoryTable("SELECT * FROM ingredientsinventory;");
        String[][] rowEntries = new String[tableData.size()][];
        for (int i = 0; i < tableData.size(); i++) {
            rowEntries[i] = tableData.get(i);
        }
        String[] columnNames = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };
        JTable table = new JTable(rowEntries, columnNames);
        table.setOpaque(false);
        table.setEnabled(false);
        table.setRowHeight(Common.HEIGHT / 24);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowSelectionInterval(1, 1);
        JScrollPane tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        tableScrollPane.setPreferredSize(new Dimension(tableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

        mainPanel.add(tableScrollPane, BorderLayout.NORTH);

        // Next Order Suggestion Scroll Pane
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
        table2.setRowHeight(Common.HEIGHT / 24);
        table2.setFont(new Font("Arial", Font.PLAIN, 16));
        table2.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane tableScrollPane2 = new JScrollPane();
        tableScrollPane2.setViewportView(table2);
        tableScrollPane2.setPreferredSize(new Dimension(tableScrollPane2.getPreferredSize().width, Common.HEIGHT / 4));

        mainPanel.add(tableScrollPane2, BorderLayout.CENTER);

		// Edit Order and Send Order Buttons
        JButton editOrderButton = new JButton("Edit Order");
        editOrderButton.setBackground(Color.GRAY);
        editOrderButton.setPreferredSize(new Dimension(120, 120));
        editOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        mainPanel.add(editOrderButton, BorderLayout.SOUTH);
		
        add(mainPanel);
	}

	public void refreshHeader() {
		remove(navbar);
		navbar = Utils.createHeaderPanel(pos);
		add(navbar, BorderLayout.NORTH);
		revalidate();
		repaint();
	}

	// for testing purposes
	// public static void main(String[] args) {
	// XReportPage p = new XReportPage(conn, pos);
	// JFrame f = new JFrame();
	// f.setSize(1600, 900);
	// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// f.add(p);
	// f.setVisible(true);
	// }
}






//     private void initializeUI() {
// Set up body in which the bulk of information will be placed
//         JPanel bodyPanel = new JPanel(new GridBagLayout());
//         bodyPanel.setBackground(Color.LIGHT_GRAY);
//         bodyPanel.setPreferredSize(new Dimension(Common.WIDTH * 7 / 8, Common.HEIGHT * 7 / 9));

//         // Set up panels that will display title, table, and suggested restocking orders

//         // Inventory Report Title Panel

//         JPanel titlePanel = new JPanel();
//         JTextArea title = new JTextArea();
//         title.setText("Inventory Report");
//         title.setFont(new Font("Times New Roman", Font.PLAIN, 28));
//         title.setOpaque(false);
//         titlePanel.setOpaque(false);
//         title.setEditable(false);

//         // Table Scroll Pane
//         List<String[]> tableData = requestInventoryTable("SELECT * FROM ingredientsinventory;");
//         String[][] rowEntries = new String[tableData.size()][];
//         for (int i = 0; i < tableData.size(); i++) {
//             rowEntries[i] = tableData.get(i);
//         }
//         String[] columnNames = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };
//         JTable table = new JTable(rowEntries, columnNames);
//         table.setOpaque(false);
//         table.setEnabled(false);
//         table.setRowHeight(Common.HEIGHT / 16);
//         table.setFont(new Font("Arial", Font.PLAIN, 16));
//         table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
//         table.setRowSelectionInterval(1, 1);
//         JScrollPane tableScrollPane = new JScrollPane();
//         tableScrollPane.setViewportView(table);
//         tableScrollPane.setPreferredSize(new Dimension(tableScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

//         // Next Order Suggestion Title Panel
//         JPanel titlePanel2 = new JPanel();
//         JTextArea title2 = new JTextArea();
//         title2.setText("Next Order Suggestion");
//         title2.setFont(new Font("Times New Roman", Font.PLAIN, 28));
//         title2.setOpaque(false);
//         titlePanel2.setOpaque(false);
//         title2.setEditable(false);

//         // Next Order Suggestion Scroll Pane
//         List<String[]> tableData2 = requestInventoryTable(
//                 "SELECT * FROM ingredientsinventory ORDER BY stock / maxstock ASC LIMIT 10;");
//         String[][] rowEntries2 = new String[tableData2.size()][];
//         for (int i = 0; i < tableData2.size(); i++) {
//             rowEntries2[i] = tableData2.get(i);
//         }
//         String[] columnNames2 = { "Ingredient ID", "Name", "Current Stock", "Max Stock", "Units" };
//         JTable table2 = new JTable(rowEntries2, columnNames2);
//         table2.setOpaque(false);
//         table2.setEnabled(false);
//         table2.setRowHeight(Common.HEIGHT / 16);
//         table2.setFont(new Font("Arial", Font.PLAIN, 16));
//         table2.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
//         JScrollPane tableScrollPane2 = new JScrollPane();
//         tableScrollPane2.setViewportView(table2);
//         tableScrollPane2.setPreferredSize(new Dimension(tableScrollPane2.getPreferredSize().width, Common.HEIGHT / 4));

//         // Edit Order and Send Order Buttons
//         JPanel orderButtonsPanel = new JPanel(new GridBagLayout());

//         JButton editOrderButton = new JButton("Edit Order");
//         editOrderButton.setBackground(Color.GRAY);
//         editOrderButton.setPreferredSize(new Dimension(120, 120));
//         editOrderButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {

//             }
//         });

//         JButton placeOrderButton = new JButton("Place Order");
//         placeOrderButton.setBackground(Color.GREEN);
//         placeOrderButton.setPreferredSize(new Dimension(120, 120));
//         placeOrderButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 String restockIDs = "";
//                 for (int i = 0; i < tableData2.size() - 1; i++) {
//                     restockIDs += tableData2.get(i)[0] + ", ";
//                 }
//                 restockIDs += tableData2.get(tableData2.size() - 1);

//                 String restockQuery = "Update ingredientsinventory SET stock = CASE WHEN ingredientid IN (" + restockIDs
//                         + ") THEN maxstock ELSE stock END WHERE ingredientid IN (" + restockIDs + ");";

//                 try {
//                     Statement stmt = con.createStatement();
//                     stmt.executeQuery(restockQuery);
//                 } catch (Exception ee) {
//                     JOptionPane.showMessageDialog(null, "Error accessing Database.");
//                 }
//             }
//         });

//         // Add everything together
//         add(navbar);
//         //add(mainPanel);
//         // mainPanel.add(bodyPanel);
//         // bodyPanel.add(titlePanel);
//         // titlePanel.add(title);
//         // bodyPanel.add(tableScrollPane);
//         // bodyPanel.add(titlePanel2);
//         // titlePanel2.add(title2);
//         // bodyPanel.add(tableScrollPane2);
//         // bodyPanel.add(orderButtonsPanel);
//         // orderButtonsPanel.add(editOrderButton);
//         // orderButtonsPanel.add(placeOrderButton);
//     }

//     public void refreshHeader() {
//         // Remove the old navbar using GridBagConstraints
//         GridBagConstraints gbc = getConstraints(navbar);
//         remove(navbar);

//         // Directly update the class field `navbar` with a new header panel
//         navbar = Utils.createHeaderPanel(pos);

//         // Add the updated navbar to the panel using GridBagConstraints
//         add(navbar);

//         // Revalidate and repaint to ensure UI updates are displayed
//         revalidate();
//         repaint();
//     }

//     // Helper method to get GridBagConstraints of a component
//     private GridBagConstraints getConstraints(Component component) {
//         LayoutManager layout = getLayout();
//         if (layout instanceof GridBagLayout) {
//             GridBagLayout gbl = (GridBagLayout) layout;
//             return gbl.getConstraints(component);
//         } else {
//             return null;
//         }
//     }

//     public static void main(String[] args) {
//         InventoryPage p = new InventoryPage(null, null);
//         JFrame f = new JFrame();
//         f.setSize(1600, 900);
//         f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         f.add(p);
//         f.setVisible(true);
//         f.setResizable(true);
//     }
// }
