import java.sql.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import java.util.Properties;

/*
  TODO:
  1) Change credentials for your own team'mainGUI database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel centerPanel
*/

public class GUI extends JFrame implements ActionListener {
  static JFrame mainFrame;

  public static void main(String[] args) {
    // Building the connection
    Connection conn = null;
    Properties props = new Properties();
    // TODO STEP 1 (see line 7)
    try {
      props.load(new FileInputStream("../GUI/database.properties"));
      String database_name = props.getProperty("database_name");
      String database_user = props.getProperty("database_user");
      String database_password = props.getProperty("database_password");
      String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%mainGUI", database_name);
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

    String name = "";
    try {
      // create a statement object
      Statement stmt = conn.createStatement();
      // create a SQL statement
      // TODO Step 2 (see line 8)
      String sqlStatement = "SELECT * FROM MenuItems;";
      // send statement to DBMS
      ResultSet result = stmt.executeQuery(sqlStatement);
      while (result.next()) {
        // TODO you probably need to change the column name tat you are retrieving
        // this command gets the data from the "name" attribute
        name += result.getString("Name") + "\n";
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Error accessing Database.");
    }

    // Set up main JFrame for Menu Items
    mainFrame = new JFrame("Menu Items");
    GUI mainGUI = new GUI();
    
    // Set up close button
    JPanel centerPanel = new JPanel();
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(mainGUI);

    // TODO Step 3 (see line 9)
    JTextArea nameText = new JTextArea(name);

    // TODO Step 4 (see line 10)
    centerPanel.add(nameText);
    centerPanel.add(closeButton);
    mainFrame.add(centerPanel);
    mainFrame.setSize(400, 400);
    mainFrame.setVisible(true);

    // closing the connection
    try {
      conn.close();
      JOptionPane.showMessageDialog(null, "Connection Closed.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Connection NOT Closed.");
    }
  }

  // if button is pressed
  public void actionPerformed(ActionEvent e) {
    String mainGUI = e.getActionCommand();
    if (mainGUI.equals("Close")) {
      mainFrame.dispose();
    }
  }
}
