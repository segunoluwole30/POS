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

public class OrderHistoryPage extends JPanel {

    private static Connection conn;
    private static POS pos;
    private JTable table;
    private JButton refresh;
    private JPanel navbar;
    private JPanel centerPanel;

    public OrderHistoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        setupUI();
    }

    private void setupUI() {
        // Boilerplate code to setup layout
        setBackground(Common.DARKCYAN);
        setLayout(new BorderLayout());

        navbar = Utils.createHeaderPanel(pos);
        navbar.setPreferredSize(new Dimension(getWidth(), 50));
        add(navbar, BorderLayout.NORTH);

        table = new JTable();
        loadHistory();

        refresh = new JButton("Refresh Report");
        refresh.addActionListener(e -> loadHistory());

        // Center Panel contains table and refresh button
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(table));
        centerPanel.add(refresh, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshHeader() {
        remove(navbar);
        navbar = Utils.createHeaderPanel(pos);
        add(navbar, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private void loadHistory() {
        String sql = "SELECT t.Date, t.TransactionID, t.Total, string_agg(mi.Name, ', ') AS MenuItems " +
                "FROM transactions t " +
                "JOIN transactionentry te ON t.TransactionID = te.TransactionID " +
                "JOIN MenuItems mi ON te.MenuItemID = mi.MenuItemID " +
                "GROUP BY t.TransactionID " +
                "LIMIT 200";

        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[] { "Date", "Order ID", "Price", "Items" });

            while (result.next()) {
                // converting query lines into values for GUI table
                String date = result.getString("date");
                int orderID = result.getInt("transactionid");
                float price = result.getFloat("total");
                String items = result.getString("menuitems");
                model.addRow(new Object[] { date, orderID, price, items }); // adds row to GUI table
            }

            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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