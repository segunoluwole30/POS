import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class OrderHistoryPage extends JPanel {

    private Connection conn;
    private POS pos;
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
                "ORDER BY t.Date DESC " + 
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
                String price = String.valueOf("$" + result.getFloat("total"));
                String items = result.getString("menuitems");
                model.addRow(new Object[] { date, orderID, price, items }); // adds row to GUI table
            }

            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}