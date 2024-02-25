import java.sql.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import java.util.Properties;

public class POS extends JFrame {

    private Connection conn;

    public POS() {

        inititializeDatabaseConnection();
        displayLoginPage();

    }

    private void inititializeDatabaseConnection() {
        Connection conn = null;
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

    private void displayLoginPage() {
        LoginPage loginPage = new LoginPage(conn);

        // add loginPage to JFrame
        add(loginPage);

        // Set JFrame properties
        setTitle("Point of Sale");
        setSize(500, 600); // Set your preferred size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the JFrame
        setVisible(true); // Make the JFrame visible
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new POS();
            }
        });
    }

}
