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
    private Map<String, ArrayList<String>> typeMap;
    

    public MenuPage(Connection con, POS pos) {
        this.conn = con;
        this.pos = pos;
        typeMap = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        setBackground(Common.MAROON);
        setLayout(new BorderLayout());

        loadNavbar();
        add(navbar, BorderLayout.WEST);

        loadMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);

        JPanel orderSummary = new JPanel(new FlowLayout());
        orderSummary.setBackground(Color.MAGENTA);
        orderSummary.setPreferredSize(new Dimension(400, 800));
        add(orderSummary, BorderLayout.EAST);
    }

    private void loadMiddlePanel() {

        middlePanel = new JPanel(new BorderLayout());
        middlePanel.setPreferredSize(new Dimension(900, Common.HEIGHT));
        middlePanel.setBackground(Common.MAROON);

        loadInfoPanel();

        itemPanel = new JPanel(new FlowLayout());
        itemPanel.setBackground(Color.GREEN);
        itemPanel.setPreferredSize(new Dimension(900, 700));

        middlePanel.add(itemPanel);
        add(middlePanel);
    }

    private void setItemPanel(String type) {
        itemPanel.removeAll();
        itemPanel.revalidate();

        String sqlStatement = "SELECT Name, Price FROM MenuItems WHERE Type = " + type + ";";

        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sqlStatement);

            while (result.next()) {


            }

        } catch (SQLException exc) {
            exc.printStackTrace();
            ;
        }

    }

    private void loadInfoPanel() {
        GridBagLayout infoLayout = new GridBagLayout();
        JPanel infoPanel = new JPanel(infoLayout);
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
        navbar.setPreferredSize(new Dimension(300, Common.HEIGHT - 1));
        navbar.setBackground(Color.gray);

        Font buttonFonts = new Font("Arial", Font.BOLD, 30);

        JButton entreeButton = new JButton("Entrees");
        entreeButton.setFont(buttonFonts);

        JButton sidesButton = new JButton("Sides");
        sidesButton.setFont(buttonFonts);

        JButton beverageButton = new JButton("Beverages");
        beverageButton.setFont(buttonFonts);

        JButton dessertButton = new JButton("Desserts");
        dessertButton.setFont(buttonFonts);

        navbar.add(entreeButton);
        navbar.add(sidesButton);
        navbar.add(beverageButton);
        navbar.add(dessertButton);

        add(navbar, BorderLayout.WEST);
    }

    // public static void main(String[] args) {
    // MenuPage p = new MenuPage(null, null);
    // JFrame f = new JFrame();
    // f.setSize(1600, 900);
    // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // f.add(p);
    // f.setVisible(true);
    // }
}
