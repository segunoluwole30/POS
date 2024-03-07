import java.sql.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import java.util.Properties;
import java.awt.*;

/**
 * The main frame for the Point of Sale (POS) application. This class manages
 * the
 * application's GUI and database connection, facilitating navigation between
 * different pages of the application such as login, menu, manager home,
 * reports,
 * order history, and inventory management.
 * 
 * @author Segun Oluwole
 * @author Daniel Rios
 */
public class POS extends JFrame {
    private String employeeID;
    private Connection conn;
    private JPanel cards;
    private CardLayout cardLayout;

    /**
     * Sets the employee ID for the current user.
     * 
     * @param employeeID The employee ID to set.
     */
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    /**
     * Gets the employee ID of the current user.
     * 
     * @return The employee ID.
     */
    public String getEmployeeID() {
        return this.employeeID;
    }

    /**
     * Constructs a POS object, initializing the database connection and setting up
     * the user interface.
     */
    public POS() {

        inititializeDatabaseConnection();
        setupUI();

    }

    /**
     * Initializes the connection to the database using properties specified in a
     * configuration file. Alerts the user upon successful connection or exits the
     * application on error.
     */
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

    /**
     * Sets up the user interface of the application, including initializing the
     * CardLayout for managing different screens and displaying the login page.
     */
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

    /**
     * Displays the menu page within the application window.
     */
    public void showMenuPage() {
        cards.add(new MenuPage(conn, this), "menu");
        cardLayout.show(cards, "menu");
    }

    /**
     * Displays the login page within the application window.
     */
    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(conn, this);
        cards.add(loginPage, "login");
        cardLayout.show(cards, "login");
    }

    /**
     * Displays the manager's home page within the application window.
     */
    public void showManagerHomePage() {
        ManagerHomePage managerHomePage = new ManagerHomePage(conn, this);
        cards.add(managerHomePage, "managerHome");
        managerHomePage.refreshHeader();
        cardLayout.show(cards, "managerHome");
    }

    /**
     * Displays the X Report page within the application window.
     */
    public void showXReportPage() {
        XReportPage xReportPage = new XReportPage(conn, this);
        cards.add(xReportPage, "XReport");
        xReportPage.refreshHeader();
        cardLayout.show(cards, "XReport");
    }

    /**
     * Displays the Z Report page within the application window.
     */
    public void showZReportPage() {
        ZReportPage zReportPage = new ZReportPage(conn, this);
        cards.add(zReportPage, "ZReport");
        zReportPage.refreshHeader();
        cardLayout.show(cards, "ZReport");
    }

    /**
     * Displays the ZZ Report page within the application window.
     */
    public void showZZReportPage() {
        ZZReportPage zzReportPage = new ZZReportPage(conn, this);
        cards.add(zzReportPage, "ZZReport");
        zzReportPage.refreshHeader();
        cardLayout.show(cards, "ZZReport");
    }

    /**
     * Displays the Order History page within the application window.
     */
    public void showOrderHistoryPage() {
        OrderHistoryPage orderHistoryPage = new OrderHistoryPage(conn, this);
        cards.add(orderHistoryPage, "OrderHistory");
        orderHistoryPage.refreshHeader();
        cardLayout.show(cards, "OrderHistory");
    }

    /**
     * Displays the Inventory Page within the application window.
     */
    public void showInventoryPage() {
        InventoryPage inventoryPage = new InventoryPage(conn, this);
        cards.add(inventoryPage, "Inventory");
        inventoryPage.refreshHeader();
        cardLayout.show(cards, "Inventory");
    }

    /**
     * The entry point of the application. Initializes the POS system within the
     * event-dispatching thread.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new POS();
            }
        });
    }
}
