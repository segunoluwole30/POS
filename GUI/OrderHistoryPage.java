import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OrderHistoryPage extends JPanel {

    private static Connection conn;
    private static POS pos;
    private JTable table;
    private JButton refresh;
    private JPanel navbar;

    public OrderHistoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();
    }

    private void initializeUI() {

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
        gbc.insets = new Insets(10, 10, 10, 10);
        add(navbar, gbc);

        String[] columnNames = { "TransactionID", "EmployeeID", "Total", "Date" };
        Object[][] data = {
                { 1, 2033, 5.49, "2024-02-27" },
                { 2, 2033, 5.49, "2024-02-27" },
                { 3, 2033, 5.49, "2024-02-27" },
                { 4, 2033, 5.49, "2024-02-27" },
                { 5, 2033, 5.49, "2024-02-27" },
                { 6, 2033, 5.49, "2024-02-27" },
                { 7, 2033, 5.49, "2024-02-27" },
                { 8, 2033, 5.49, "2024-02-27" },
                { 9, 2033, 5.49, "2024-02-27" },
                { 10, 2033, 5.49, "2024-02-27" },
                { 11, 2033, 5.49, "2024-02-27" },
                { 12, 2033, 5.49, "2024-02-27" },
                { 13, 2033, 5.49, "2024-02-27" },
                { 14, 2033, 5.49, "2024-02-27" },
                { 15, 2033, 5.49, "2024-02-27" },
                { 16, 2033, 5.49, "2024-02-27" },
                { 17, 2033, 5.49, "2024-02-27" },
                { 18, 2033, 5.49, "2024-02-27" },
                { 19, 2033, 5.49, "2024-02-27" },
                { 1, 2033, 5.49, "2024-02-27" },
                { 2, 2033, 5.49, "2024-02-27" },
                { 3, 2033, 5.49, "2024-02-27" },
                { 4, 2033, 5.49, "2024-02-27" },
                { 5, 2033, 5.49, "2024-02-27" },
                { 6, 2033, 5.49, "2024-02-27" },
                { 7, 2033, 5.49, "2024-02-27" },
                { 8, 2033, 5.49, "2024-02-27" },
                { 9, 2033, 5.49, "2024-02-27" },
                { 10, 2033, 5.49, "2024-02-27" },
                { 11, 2033, 5.49, "2024-02-27" },
                { 12, 2033, 5.49, "2024-02-27" },
                { 13, 2033, 5.49, "2024-02-27" },
                { 14, 2033, 5.49, "2024-02-27" },
                { 15, 2033, 5.49, "2024-02-27" },
                { 16, 2033, 5.49, "2024-02-27" },
                { 17, 2033, 5.49, "2024-02-27" },
                { 18, 2033, 5.49, "2024-02-27" },
                { 19, 2033, 5.49, "2024-02-27" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1; // Increase the gridy to move the table below the navbar
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton refresh = new JButton("Refresh Report");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refresh button clicked");
            }
        });
        /*
         * refresh.addActionListener(e -> refreshOrderHistory());
         * 
         * refreshOrderHistory();
         */

        add(new JScrollPane(table), gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        add(refresh, gbc);
    }

    /*
     * private void refreshOrderHistory() {
     * try {
     * Statement statement = conn.createStatement();
     * ResultSet result = statement.executeQuery("SELECT * FROM transactions");
     * 
     * ResultSetMetaData metaData = result.getMetaData();
     * } catch (SQLException ex){
     * ex.printStackTrace();
     * }
     * }
     */

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
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OrderHistoryPage p = new OrderHistoryPage(conn, pos);
        f.getContentPane().add(p);

        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
