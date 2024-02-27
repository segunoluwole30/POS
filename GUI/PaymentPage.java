import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.awt.event.*;

public class PaymentPage extends JPanel {

    private Connection conn;
    private POS pos;
    private JPanel navbar;
    private JPanel middlePanel;
    private JPanel itemPanel;
    private Map<String, ArrayList<String>> typeMap;
    

    public PaymentPage(Connection con, POS pos) {
        this.conn = con;
        this.pos = pos;
        typeMap = new HashMap<>();

        String sqlStatement = "SELECT * FROM MenuItems;";

        if(this.conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(sqlStatement);

                while (result.next()) {
                    String itemType = result.getString(4);
                    String itemName = result.getString(2);

                    typeMap.putIfAbsent(itemType, new ArrayList<String>());
                    typeMap.get(itemType).add(itemName);
                }

            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        }

        initializeUI();
    }

    private void initializeUI() {
        setBackground(Common.MAROON);
        setLayout(new BorderLayout());

        //loadNavbar();
        //add(navbar, BorderLayout.WEST);

        loadMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        JPanel orderSummary = new OrderSummary();
        add(orderSummary.Contents)
        /*
        orderSummary.setBackground(Color.MAGENTA);
        orderSummary.setPreferredSize(new Dimension(400, 800));
        add(orderSummary, BorderLayout.EAST);
        */
    }

    private void loadMiddlePanel() {

        middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBackground(Common.MAROON);
        middlePanel.setPreferredSize(new Dimension(900, Common.HEIGHT));

        //loadInfoPanel();

        itemPanel = new JPanel(new FlowLayout());
        itemPanel.setBackground(Common.MAROON);
        itemPanel.setPreferredSize(new Dimension(900, 700));
        setItemPanel("Entree");

        middlePanel.add(itemPanel);
        add(middlePanel);
    }

    private void setItemPanel(String type) {
        itemPanel.removeAll();
        itemPanel.revalidate();
        itemPanel.repaint();

        ArrayList<String> items = new ArrayList<>();
        items.add("Cash");
        items.add("Credit Card");
        items.add("Dining Dollars");
        items.add("Retail Swipe");

        for (int i = 0; i < items.size(); i++) {
            JButton b = new JButton(items.get(i));
            b.setFont(new Font("Arial", Font.BOLD, 15));
            b.setPreferredSize(new Dimension(300, 300));
            itemPanel.add(b);
        }

    }
    public static void main(String[] args) {
    PaymentPage p = new PaymentPage(null, null);
    JFrame f = new JFrame();
    f.setSize(1600, 900);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.add(p);
    f.setVisible(true);
    }
}

