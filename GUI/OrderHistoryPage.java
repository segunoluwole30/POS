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
        // JPanel homePanel = new JPanel(new GridBagLayout());
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
        gbc.gridy = 1; // Increase the gridy to move the table below the navbar
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(new JScrollPane(table), gbc);

        refresh = new JButton("Refresh Report");        
        refresh.addActionListener(e -> loadHistory());
        
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        add(refresh, gbc);
        
        loadHistory();
    }

    private void loadHistory() {
        
        String sql = "SELECT date,transactionid,total FROM transactions";
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(
                "SELECT date,transactionid,total FROM transactions "+
                "LIMIT 20");

            // Create a table model and populate it with data from the result set
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Date", "Order ID", "Price"});

            while (result.next()) {
                String date = result.getString("date");
                int orderID = result.getInt("transactionid");
                float price = result.getFloat("total");
                System.out.println("Adding row from database");
                model.addRow(new Object[]{date, orderID, price});
            }

            // Set the table model to the JTable
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

    /* public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OrderHistoryPage p = new OrderHistoryPage(conn, pos);
        f.getContentPane().add(p);

        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    } */
}
