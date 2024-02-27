import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.sql.*;
import java.awt.*;

public class InventoryPage extends JPanel {
    
    private Connection con;
    private POS pos;

    public InventoryPage(Connection con, POS pos) {
        this.con = con;
        this.pos = pos;
        initializeUI();
    }

    private void initializeUI() {
        // Set up panel that will hold all page contents
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Common.DARKCYAN);
        mainPanel.setPreferredSize(new Dimension(Common.WIDTH, Common.HEIGHT));

        // Set up panel in which the bulk of information will be placed
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        JPanel bulkPanel = new JPanel(new GridBagLayout());
        bulkPanel.setBackground(Color.LIGHT_GRAY);
        bulkPanel.setPreferredSize(new Dimension(Common.WIDTH * 7 / 8, Common.HEIGHT *7 / 9));
        

        // Set up panels that will display title, table, and suggested restocking orders

        // Inventory Report Title Panel
        GridBagConstraints constraintsTitle = new GridBagConstraints();
        constraintsTitle.gridx = 0;
        constraintsTitle.gridy = 0;
        constraintsTitle.weightx = 1.0;
        constraintsTitle.anchor = GridBagConstraints.PAGE_START;
        JPanel titlePanel = new JPanel();
        JTextArea title = new JTextArea();
        title.setText("Inventory Report");
        title.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        title.setOpaque(false);
        titlePanel.setOpaque(false);
        title.setEditable(false);

        // Table Scroll Pane
        GridBagConstraints constraintsTable = new GridBagConstraints();
        constraintsTable.gridx = 0;
        constraintsTable.gridy = 1;
        constraintsTable.weightx = 1.0;
        constraintsTable.fill = GridBagConstraints.BOTH;
        constraintsTable.anchor = GridBagConstraints.CENTER;
        String[][] rowEntries = {{"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}};
        String[] columnNames = {"Ingredient ID", "Name", "Current Stock", "Max Stock", "Units"};
        JTable table = new JTable(rowEntries, columnNames);
        table.setOpaque(false);
        table.setEnabled(false);
        table.setRowHeight(Common.HEIGHT / 16);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
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
        title2.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        title2.setOpaque(false);
        titlePanel2.setOpaque(false);
        title2.setEditable(false);

        // Next Order Suggestion Scroll Pane
        GridBagConstraints constraintsTable2 = new GridBagConstraints();
        constraintsTable2.gridx = 0;
        constraintsTable2.gridy = 3;
        constraintsTable2.weightx = 1.0;
        constraintsTable2.fill = GridBagConstraints.BOTH;
        constraintsTable2.anchor = GridBagConstraints.CENTER;
        String[][] rowEntries2 = {{"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}, {"1","Beef Patties","100","200","Pieces"}};
        String[] columnNames2 = {"Ingredient ID", "Name", "Current Stock", "Max Stock", "Units"};
        JTable table2 = new JTable(rowEntries2, columnNames2);
        table2.setOpaque(false);
        table2.setEnabled(false);
        table2.setRowHeight(Common.HEIGHT / 16);
        table2.setFont(new Font("Arial", Font.PLAIN, 16));
        table2.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane tableScrollPane2 = new JScrollPane();
        tableScrollPane2.setViewportView(table2);
        tableScrollPane2.setPreferredSize(new Dimension(tableScrollPane2.getPreferredSize().width, Common.HEIGHT / 4));

        add(mainPanel, constraints);
        mainPanel.add(bulkPanel, constraints);
        bulkPanel.add(titlePanel, constraintsTitle);
        bulkPanel.add(tableScrollPane, constraintsTable);
        bulkPanel.add(titlePanel2, constraintsTitle2);
        titlePanel.add(title);
        titlePanel2.add(title2);
        bulkPanel.add(tableScrollPane2, constraintsTable2);
    }

    public static void main(String[] args) {
        InventoryPage p = new InventoryPage(null,null);
        JFrame f = new JFrame();
        f.setSize(1600,900);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(p);
        f.setVisible(true);
        f.setResizable(true);
    }
}
