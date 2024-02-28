import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.awt.event.*;

public class MenuPage extends JPanel {

    private Connection conn;
    private POS pos;
    private JPanel navbar;
    private JPanel middlePanel;
    private JPanel itemPanel;
    private JPanel payPanel;
    private OrderSummary orderSummary;
    private Map<String, ArrayList<String>> typeMap;
    private Map<String, Float> priceMap;
    private Map<String, Integer> idMap;
    private boolean currentlyOrdering;

    public MenuPage(Connection con, POS pos) {
        this.conn = con;
        this.pos = pos;
        typeMap = new HashMap<>();
        priceMap = new HashMap<>();
        idMap = new HashMap<>();
        currentlyOrdering = true;

        String sqlStatement = "SELECT * FROM MenuItems;";

        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sqlStatement);

            while (result.next()) {
                String itemType = result.getString(4);
                String itemName = result.getString(2);
                float price = result.getFloat(3);
                int id = result.getInt(1);

                typeMap.putIfAbsent(itemType, new ArrayList<String>());
                typeMap.get(itemType).add(itemName);

                priceMap.put(itemName, price);
                idMap.put(itemName, id);
            }

        } catch (SQLException exc) {
            exc.printStackTrace();
        }

        initializeUI();
    }

    private void initializeUI() {
        setBackground(Common.MAROON);
        setLayout(new BorderLayout());

        loadNavbar();

        loadMiddlePanel();

        orderSummary = new OrderSummary(this);
        // orderSummary.setPreferredSize(new Dimension(400, 800));
        add(orderSummary, BorderLayout.EAST);
    }

    private void loadMiddlePanel() {

        middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Common.MAROON);
        middlePanel.setPreferredSize(new Dimension(900, Common.HEIGHT));

        loadInfoPanel();

        itemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        itemPanel.setBackground(Common.MAROON);
        itemPanel.setPreferredSize(new Dimension(900, 700));
        setItemPanel("Entree");

        middlePanel.add(itemPanel);
        add(middlePanel);
    }

    private void loadInfoPanel() {
        GridBagLayout infoLayout = new GridBagLayout();
        JPanel infoPanel = new JPanel(infoLayout);
        infoPanel.setBackground(Common.MAROON);
        infoPanel.setPreferredSize(new Dimension(900, 250));

        Font labelFont = new Font("Arial", Font.BOLD, 20);

        JButton managerButton = new JButton("Manager Mode");
        managerButton.setFont(labelFont);
        managerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pos.showManagerHomePage();
            }
        });

        JLabel employeeName = new JLabel("EMPLOYEE NAME", SwingConstants.CENTER);
        employeeName.setFont(labelFont);
        employeeName.setOpaque(true);
        employeeName.setBackground(Color.WHITE);

        JLabel orderNumber = new JLabel("ORDER NUMBER", SwingConstants.CENTER);
        orderNumber.setFont(labelFont);
        orderNumber.setOpaque(true);
        orderNumber.setBackground(Color.WHITE);

        JLabel orderTotal = new JLabel("ORDER TOTAL", SwingConstants.CENTER);
        orderTotal.setFont(labelFont);
        orderTotal.setOpaque(true);
        orderTotal.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between components
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(managerButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        infoPanel.add(employeeName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(orderNumber, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        infoPanel.add(orderTotal, gbc);

        middlePanel.add(infoPanel, BorderLayout.NORTH);
    }

    private void loadNavbar() {
        // Create menu navbar
        navbar = new JPanel();
        navbar.setLayout(new GridLayout(4, 1));
        navbar.setBackground(Color.gray);
        navbar.setPreferredSize(new Dimension(300, Common.HEIGHT));

        Font buttonFonts = new Font("Arial", Font.BOLD, 30);

        JButton entreeButton = new JButton("Entrees");
        entreeButton.setFont(buttonFonts);
        entreeButton.addActionListener(e -> setItemPanel("Entree"));

        JButton sidesButton = new JButton("Sides");
        sidesButton.setFont(buttonFonts);
        sidesButton.addActionListener(e -> setItemPanel("Side"));

        JButton beverageButton = new JButton("Beverages");
        beverageButton.setFont(buttonFonts);
        beverageButton.addActionListener(e -> setItemPanel("Drink"));

        JButton dessertButton = new JButton("Desserts");
        dessertButton.setFont(buttonFonts);
        dessertButton.addActionListener(e -> setItemPanel("Dessert"));

        navbar.add(entreeButton);
        navbar.add(sidesButton);
        navbar.add(beverageButton);
        navbar.add(dessertButton);

        add(navbar, BorderLayout.WEST);
    }

    private void loadPayPanel() {
        payPanel = new JPanel();

        payPanel.setBackground(Common.MAROON);
        payPanel.setLayout(new GridBagLayout());
        payPanel.setPreferredSize(new Dimension(1250, Common.HEIGHT - 50));

        JButton cashButton = new JButton("Cash");
        JButton creditButton = new JButton("Credit Card");
        JButton diningButton = new JButton("Dining Dollars");
        JButton swipeButton = new JButton("Meal Swipe");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 0); // Padding between components
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        payPanel.add(cashButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        payPanel.add(creditButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        payPanel.add(diningButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        payPanel.add(swipeButton, gbc);

        add(payPanel, BorderLayout.WEST);
    }

    private void setItemPanel(String type) {
        itemPanel.removeAll();
        itemPanel.revalidate();
        itemPanel.repaint();

        ArrayList<String> items = typeMap.get(type);

        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            JButton b = new JButton(itemName);
            b.addActionListener(e -> addToSummary(itemName, priceMap.get(itemName)));

            if (type == "Entree") {
                b.setFont(new Font("Arial", Font.BOLD, 15));
                b.setPreferredSize(new Dimension(250, 100));
            }
            else {
                b.setFont(new Font("Arial", Font.BOLD, 15));
                b.setPreferredSize(new Dimension(325, 125));
            }

            itemPanel.add(b);
        }

    }

    private void addToSummary(String item, float price) {
        orderSummary.addButton(item, price);
    }

    public void payButton() {
        // if (currentlyOrdering)

        remove(middlePanel);
        remove(navbar);
        revalidate();
        loadPayPanel();
        repaint();
    }

    public void cancelButton() {
        // if (currentlyOrdering)

        remove(payPanel);
        revalidate();
        loadNavbar();
        loadMiddlePanel();
        repaint();
    }

}