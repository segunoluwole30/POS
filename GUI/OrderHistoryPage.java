import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderHistoryPage extends JPanel {
    private JTable table;
    private JButton refresh;
    private JPanel navbar;
    private Connection conn;
    private POS pos;

    public OrderHistoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();
    }

    private void initializeUI() {
        setBackground(Common.DARKCYAN);
        // homePanel.setPreferredSize(new Dimension(Common.WIDTH, Common.HEIGHT));
        // Creating the top navbar
        navbar = Utils.createHeaderPanel(pos);
        navbar.setPreferredSize(new Dimension(getWidth(), 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(navbar, gbc);

        table = new JTable();
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1; // Increase the gridy to move the table below the navbar, still buggy
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(new JScrollPane(table), gbc);

        refresh = new JButton("Refresh Report");        
        refresh.addActionListener(e -> loadHistory());
        
        gbc.gridy++; // attempts to add button below table and navbar, still buggy
        gbc.fill = GridBagConstraints.NONE;
        add(refresh, gbc);
        
        loadHistory();
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
            model.setColumnIdentifiers(new String[]{"Date", "Order ID", "Price", "Items"});

            while (result.next()) {
                // converting query lines into values for GUI table
                String date = result.getString("date"); 
                int orderID = result.getInt("transactionid"); 
                float price = result.getFloat("total"); 
                String items = result.getString("menuitems");
                model.addRow(new Object[]{date, orderID, price, items}); // adds row to GUI table
            }

            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
