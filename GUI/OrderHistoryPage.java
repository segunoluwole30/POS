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

    public OrderHistoryPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();
    }

    private void initializeUI() {

        setBackground(Common.DARKCYAN);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
    
        
        String[] columnNames = {"TransactionID", "EmployeeID", "Total", "Date"};
        Object[][] data = {
            {1, 2033, 5.49, "2024-02-27"},
            {2, 2033, 5.49, "2024-02-27"},
            {3, 2033, 5.49, "2024-02-27"},
            {4, 2033, 5.49, "2024-02-27"},
            {5, 2033, 5.49, "2024-02-27"},
            {6, 2033, 5.49, "2024-02-27"},
            {7, 2033, 5.49, "2024-02-27"},
            {8, 2033, 5.49, "2024-02-27"},
            {9, 2033, 5.49, "2024-02-27"},
            {10, 2033, 5.49, "2024-02-27"},
            {11, 2033, 5.49, "2024-02-27"},
            {12, 2033, 5.49, "2024-02-27"},
            {13, 2033, 5.49, "2024-02-27"},
            {14, 2033, 5.49, "2024-02-27"},
            {15, 2033, 5.49, "2024-02-27"},
            {16, 2033, 5.49, "2024-02-27"},
            {17, 2033, 5.49, "2024-02-27"},
            {18, 2033, 5.49, "2024-02-27"},
            {19, 2033, 5.49, "2024-02-27"},
            {1, 2033, 5.49, "2024-02-27"},
            {2, 2033, 5.49, "2024-02-27"},
            {3, 2033, 5.49, "2024-02-27"},
            {4, 2033, 5.49, "2024-02-27"},
            {5, 2033, 5.49, "2024-02-27"},
            {6, 2033, 5.49, "2024-02-27"},
            {7, 2033, 5.49, "2024-02-27"},
            {8, 2033, 5.49, "2024-02-27"},
            {9, 2033, 5.49, "2024-02-27"},
            {10, 2033, 5.49, "2024-02-27"},
            {11, 2033, 5.49, "2024-02-27"},
            {12, 2033, 5.49, "2024-02-27"},
            {13, 2033, 5.49, "2024-02-27"},
            {14, 2033, 5.49, "2024-02-27"},
            {15, 2033, 5.49, "2024-02-27"},
            {16, 2033, 5.49, "2024-02-27"},
            {17, 2033, 5.49, "2024-02-27"},
            {18, 2033, 5.49, "2024-02-27"},
            {19, 2033, 5.49, "2024-02-27"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        JButton refresh = new JButton("Refresh Report");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refresh button clicked");
            }
        });
        /* refresh.addActionListener(e -> refreshOrderHistory());

        refreshOrderHistory(); */

        add(new JScrollPane(table), gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        add(refresh, gbc);
    }

    /* private void refreshOrderHistory() {
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM transactions");

            ResultSetMetaData metaData = result.getMetaData();
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    } */

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
