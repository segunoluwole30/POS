import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The class that displays the Order History Report page. This class manages
 * the report that lists the last 500 transactions sorted by date/time and implements
 * a button that refreshs the report to account for new transactions. 
 * 
 *
 * @author Alby Joseph
 */
public class OrderHistoryPage extends JPanel {
    private Connection conn;
    private POS pos;
    private JTable table;
    private JButton refresh;
    private JPanel navbar;
    private JPanel centerPanel;

    /**
     * This is the constructor for the OrderHistoryPage object. It creates an
     * OrderHistoryPage object that displays a list of all past orders in the POS.
     * The conn argument must already be an established SQL database connection and
     * the pos argument must be an established POS object. 
     * 
     * @param conn , a SQL connection object that represents the connection
     *               to the database
     * @param pos  , the POS object that acts as the main object and foreground of the program
     */
    public OrderHistoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        setupUI();
    }

    /**
     * Assembles all the Java Swing components and implements them into the Order History Page
     * 
     * @param none
     */
    private void setupUI() {
        // Boilerplate code to setup layout
        setLayout(new BorderLayout());

        navbar = Utils.createHeaderPanel(pos);
        navbar.setPreferredSize(new Dimension(getWidth(), 50));
        add(navbar, BorderLayout.NORTH);

        table = new JTable();
        loadHistory();
        table.getColumnModel().getColumn(0).setMinWidth(150);
        table.getColumnModel().getColumn(0).setMaxWidth(250);
        table.getColumnModel().getColumn(1).setMaxWidth(60);
        table.getColumnModel().getColumn(2).setMaxWidth(60);

        refresh = new JButton("Refresh Report");
        refresh.setFont(new Font("Arial", Font.BOLD, 20));
        refresh.addActionListener(e -> loadHistory());

        // Center Panel contains table and refresh button
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Common.DARKCYAN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(40, 80, 20, 80);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JScrollPane(table), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 80, 20, 80);
        centerPanel.add(refresh, gbc);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Reloads the navigation bar at the top of the page to update the time element
     * 
     * @param none
     */
    public void refreshHeader() {
        remove(navbar);
        navbar = Utils.createHeaderPanel(pos);
        add(navbar, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    /**
     * Uses the global conn variable to execute a SQL statement that lists the last 500
     * transactions along with every associated menu item. The transactions are then channeled
     * into a JTable.
     * 
     * @param none
     */
    private void loadHistory() {
        String sql = "SELECT t.Date, t.TransactionID, t.Total, string_agg(mi.Name, ', ') AS MenuItems " +
                "FROM transactions t " +
                "JOIN transactionentry te ON t.TransactionID = te.TransactionID " +
                "JOIN MenuItems mi ON te.MenuItemID = mi.MenuItemID " +
                "GROUP BY t.TransactionID " +
                "ORDER BY t.Date DESC " + 
                "LIMIT 500";

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