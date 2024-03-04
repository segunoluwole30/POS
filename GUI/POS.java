import java.sql.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import java.util.Properties;
import java.awt.*;

public class POS extends JFrame {
    private String employeeID;
    private Connection conn;
    private JPanel cards;
    private CardLayout cardLayout;

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeID() {
        return this.employeeID;
    }

    public POS() {

        inititializeDatabaseConnection();
        setupUI();

    }

    private void inititializeDatabaseConnection() {
        this.conn = null;
        Properties props = new Properties();
        // TODO STEP 1 (see line 7)
        try {
            props.load(new FileInputStream("../GUI/database.properties"));
            String database_name = props.getProperty("database_name");
            String database_user = props.getProperty("database_user");
            String database_password = props.getProperty("database_password");
            String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
            conn = DriverManager.getConnection(database_url, database_user, database_password);
            JOptionPane.showMessageDialog(null, "Opened database successfully");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Properties file not found: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading properties file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    private void setupUI() {
        setTitle("Point of Sale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize CardLayout
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        add(cards);
        showLoginPage();

        setSize(Common.WIDTH, Common.HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showMenuPage() {
        cards.add(new MenuPage(conn, this), "menu");
        cardLayout.show(cards, "menu");
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(conn, this);
        cards.add(loginPage, "login");
        cardLayout.show(cards, "login");
    }

    public void showManagerHomePage() {
        ManagerHomePage managerHomePage = new ManagerHomePage(conn, this);
        cards.add(managerHomePage, "managerHome");
        managerHomePage.refreshHeader();
        cardLayout.show(cards, "managerHome");
    }

    public void showXReportPage() {
        XReportPage xReportPage = new XReportPage(conn, this);
        cards.add(xReportPage, "XReport");
        xReportPage.refreshHeader();
        cardLayout.show(cards, "XReport");
    }

    public void showZReportPage() {
        ZReportPage zReportPage = new ZReportPage(conn, this);
        cards.add(zReportPage, "ZReport");
        zReportPage.refreshHeader();
        cardLayout.show(cards, "ZReport");
    }

    public void showZZReportPage() {
        ZZReportPage zzReportPage = new ZZReportPage(conn, this);
        cards.add(zzReportPage, "ZZReport");
        zzReportPage.refreshHeader();
        cardLayout.show(cards, "ZZReport");
    }

    public void showOrderHistoryPage() {
        OrderHistoryPage orderHistoryPage = new OrderHistoryPage(conn, this);
        cards.add(orderHistoryPage, "OrderHistory");
        orderHistoryPage.refreshHeader(); // Now safe to call refreshHeader
        cardLayout.show(cards, "OrderHistory");
    }

    public void showInventoryPage() {
        InventoryPage inventoryPage = new InventoryPage(conn, this);
        cards.add(inventoryPage, "Inventory");
        inventoryPage.refreshHeader(); // Now safe to call refreshHeader
        cardLayout.show(cards, "Inventory");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new POS();
            }
        });
    }
}
