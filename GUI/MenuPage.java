import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * This class defines the Menu Page framework, which acts as the home page
 * for the POS. MenuPage includes the basics of a POS homepage, from buttons
 * to access every item on the menu, to a header navigation bar that displays
 * the current user and the current total price of the order, to a panel that 
 * holds a receipt of the current menu items in a transaction, to a manager mode
 * button that is only accessible to users with the manager tag. 
 * 
 * @author Danny Rios Socorro
 */
public class MenuPage extends JPanel {
    private Connection conn;
    private POS pos;
    private JPanel navbar;
    private JPanel middlePanel;
    private JPanel itemPanel;
    private JPanel payPanel;
    private JLabel orderTotal;
    private OrderSummary orderSummary;
    private Map<String, ArrayList<String>> typeMap;
    private Map<String, Double> priceMap;
    private Map<String, Integer> idMap;
    private ArrayList<String> transactionItems;
    private String currentEmployeeName;
    private String currentRole;
    private String paymentType;
    private int transactionID;
    private double transactionTotal;
    private boolean currentlyPaying;

    /**
     * This is the constructor for the MenuPage object. It creates a MenuPage object
     * that displays the menu, split into different category tabs, a nav bar with different
     * order and user data along with a manager access button, and a order tracking panel. 
     * 
     * The con argument must already be an established SQL database connection, and the 
     * pos argument must be an previously established POS object. 
     * 
     * @param con , a SQL connection object that represents the connection to the database
     * @param pos , the POS object that acts as the main driver for the program
     */
    public MenuPage(Connection con, POS pos) {
        this.conn = con;
        this.pos = pos;
        typeMap = new HashMap<>();
        priceMap = new HashMap<>();
        idMap = new HashMap<>();
        transactionItems = new ArrayList<String>();
        currentlyPaying = false;
        paymentType = "";

        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM MenuItems;");

            while (result.next()) {
                String itemType = result.getString(4);
                String itemName = result.getString(2);
                double price = result.getDouble(3);
                int id = result.getInt(1);

                typeMap.putIfAbsent(itemType, new ArrayList<String>());
                typeMap.get(itemType).add(itemName);

                priceMap.put(itemName, price);
                idMap.put(itemName, id);
            }

            result = stmt.executeQuery("SELECT transactionid FROM transactions ORDER BY transactionid DESC LIMIT 1;");
            result.next();
            transactionID = result.getInt(1) + 1;

            result = stmt.executeQuery("SELECT name, role FROM employees WHERE employeeid = " + pos.getEmployeeID());
            result.next();
            currentEmployeeName = result.getString(1);
            currentRole = result.getString(2);

        } catch (SQLException exc) {
            exc.printStackTrace();
        }

        initializeUI();
    }

    /**
     * The root of the Java Swing UI implementation for Menu Page
     * 
     * @param none
     */
    private void initializeUI() {
        setBackground(Common.MAROON);
        setLayout(new BorderLayout());

        loadNavbar();
        loadMiddlePanel();

        orderSummary = new OrderSummary(this);
        add(orderSummary, BorderLayout.EAST);
    }

    /**
     * Creates the Java Swing elements for the middle panel
     * 
     * @param none
     */
    private void loadMiddlePanel() {
        middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Common.MAROON);
        middlePanel.setPreferredSize(new Dimension(900, Common.HEIGHT));

        loadInfoPanel();

        itemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        itemPanel.setBackground(Common.MAROON);
        itemPanel.setPreferredSize(new Dimension(900, 700));
        setItemPanel("Entree");

        middlePanel.add(new JScrollPane(itemPanel));
        add(middlePanel);
    }

    /**
     * Creates the Java Swing elements for the nav bar
     * at the top of the page
     * 
     * @param none
     */
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
                System.out.println(currentRole);
                if (currentRole.equals("manager")) {
                    pos.showManagerHomePage();
                }
                else {
                    JOptionPane.showMessageDialog(null, "Cannot Access Manager Mode", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel employeeName = new JLabel("Current User: " + currentEmployeeName, SwingConstants.CENTER);
        employeeName.setFont(labelFont);
        employeeName.setOpaque(true);
        employeeName.setBackground(Color.WHITE);

        JLabel orderNumber = new JLabel("ORDER #" + transactionID, SwingConstants.CENTER);
        orderNumber.setFont(labelFont);
        orderNumber.setOpaque(true);
        orderNumber.setBackground(Color.WHITE);

        orderTotal = new JLabel("Order Total: $" + String.format("%.2f", transactionTotal), SwingConstants.CENTER);
        orderTotal.setFont(labelFont);
        orderTotal.setOpaque(true);
        orderTotal.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
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

    /**
     * Creates the UI elements for the side bar that accesses the 
     * different categories of menu items and adds this side bar
     * to the Menu Page panel
     * 
     * @param none
     */
    private void loadNavbar() {
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

    /**
     * Creates the UI elements for the payment panel that allows
     * the user to choose a payment option along with displaying the 
     * current order, which is all added on top of the previous Menu Page
     * 
     * @param none
     */
    private void loadPayPanel() {
        payPanel = new JPanel();

        payPanel.setBackground(Common.MAROON);
        payPanel.setLayout(new GridBagLayout());
        payPanel.setPreferredSize(new Dimension(1250, Common.HEIGHT - 50));

        JButton cashButton = new JButton("Cash");
        cashButton.addActionListener(e -> setPaymentType("Cash"));
        JButton creditButton = new JButton("Credit Card");
        creditButton.addActionListener(e -> setPaymentType("Credit Card"));
        JButton diningButton = new JButton("Dining Dollars");
        diningButton.addActionListener(e -> setPaymentType("Dining Dollars"));
        JButton swipeButton = new JButton("Meal Swipe");
        swipeButton.addActionListener(e -> setPaymentType("Meal Swipe"));

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

    /**
     * Creates the UI elements for the main item panel, which
     * holds all the buttons for eveyr menu item, and adds it to
     * the Menu Page
     * 
     * @param type , a string that determines the category of
     *               the menu item
     */
    private void setItemPanel(String type) {
        itemPanel.removeAll();
        itemPanel.revalidate();
        itemPanel.repaint();

        ArrayList<String> items = typeMap.get(type);

        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            double itemPrice = priceMap.get(itemName);
            JButton b = new JButton(itemName);
            b.addActionListener(e -> addToSummary(itemName, itemPrice));

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

    /**
     * Enters a new transaction into the database, queries
     * the database to determine what ingredients need to be 
     * updated in inventory, updates the stock in inventory, 
     * and returns to the home Menu Page
     * 
     * @param none
     * @throws SQLException , if an error occurs adding a transaction
     */
    private void finalizeOrder() {
        try {
            Statement stmt = conn.createStatement();
            String finalOrderTotal = round(transactionTotal) + "";
            String timeStamp = "'" + Utils.getCurrentDate() + " " + Utils.getCurrentTime() + "'";
            String statement = "INSERT INTO transactions VALUES (" + transactionID + ", " + Integer.parseInt(pos.getEmployeeID()) + ", " + Float.parseFloat(finalOrderTotal) + ", " + timeStamp + ");";
            stmt.executeUpdate(statement);

            for (int i = 0; i < transactionItems.size(); i++) {
                int id = idMap.get(transactionItems.get(i));
                stmt.executeUpdate("INSERT INTO transactionentry VALUES (" + id + "," + transactionID + ");");

                statement = "SELECT ingredientid, quantity FROM menuitemingredients WHERE menuitemid = " + id; 
                ResultSet result = stmt.executeQuery(statement);
                while (result.next()) {
                    int ingredientId = result.getInt(1);
                    String quantity = result.getString(2);
                    Statement s = conn.createStatement();
                    statement = "UPDATE ingredientsinventory SET stock = stock - " + quantity + " WHERE ingredientid = " + ingredientId;
                    s.executeUpdate(statement);
                }
            }

        } catch (SQLException exc) {
            exc.printStackTrace();
        }

        pos.showMenuPage();
    }

    /**
     * Adds the price of the menu item to the transaction total
     * and displays the item as a button for optional deletion
     * 
     * @param item , a string that holds the name of the menu item
     * @param price , a double that holds the price of the menu item
     */
    private void addToSummary(String item, double price) {
        transactionTotal += price;
        transactionTotal = round(transactionTotal);
        orderTotal.setText("Order Total: $" + String.format("%.2f", transactionTotal));
        orderSummary.addButton(item, price);
        transactionItems.add(item);
    }

    /**
     * Removes an item from the transaction by subtracting
     * the price from the order total and the item from the list
     * of items in the order
     * 
     * @param item , a string that holds the name of the menu item
     * @param price , a double that holds the price of the menu item
     */
    public void removeTransactionEntree(String item, double price) {
        transactionTotal -= price;
        transactionTotal = round(transactionTotal);
        orderTotal.setText("Order Total: $" + String.format("%.2f", transactionTotal));
        transactionItems.remove(item);
    }

    /**
     * Sets the payment type, selected by the user, for 
     * the order
     * 
     * @param type , a string that holds the name of the payment type
     */
    private void setPaymentType(String type) {
        paymentType = type;
    }

    /**
     * Implements the payment button, which either finalizes
     * the order or leads to the payment option panel
     * 
     * @param none
     */
    public void payButton() {
        if (currentlyPaying) {
            if (paymentType.equals("")) {
                JOptionPane.showMessageDialog(null, "Select a Payent Type", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if (transactionItems.size() == 0) {
                JOptionPane.showMessageDialog(null, "Order Must Have Items", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                finalizeOrder();
            }
        }
        else {
            remove(middlePanel);
            remove(navbar);
            revalidate();
            loadPayPanel();
            orderSummary.refreshTopButton("Cancel Order");
            repaint();
            currentlyPaying = true;
        }
    }

    /**
     * Implements the cancel button, which either cancels
     * the current order and returns to the home Menu Page, or
     * logs the current user out of the POS
     * 
     * @param none
     */
    public void cancelButton() {
        if (currentlyPaying) {
            remove(payPanel);
            revalidate();
            loadNavbar();
            loadMiddlePanel();
            orderSummary.refreshTopButton("Logout");
            repaint();    
            currentlyPaying = false;
            paymentType = "";
        } 
        else {
            pos.showLoginPage();      
        }
    }

    /**
     * Rounds a number to prevent very long decimals
     * in key parts of this implementation
     * 
     * @param num , a double that holds the number being rounded
     * @return , the rounded number with all unnecessary decimal 
     *           digits removed
     */
    public double round(double num) {
        return Math.round(num * 100) / 100.0;
    }
}