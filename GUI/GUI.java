import java.sql.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import java.util.Properties;

/*
  TODO:
  1) Change credentials for your own team's database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel p
*/

public class GUI extends JFrame implements ActionListener {
  static JFrame f;

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
    // create a new frame
    f = new JFrame("Menu Items");

    // create a object
    GUI s = new GUI();

    // create a panel
    JPanel p = new JPanel();

    JButton b = new JButton("Close");

    // add actionlistener to button
    b.addActionListener(s);

    // TODO Step 3 (see line 9)
    JTextArea t = new JTextArea(name);

    // TODO Step 4 (see line 10)
    p.add(t);

    // add button to panel
    p.add(b);

    // add panel to frame
    f.add(p);

    // set the size of frame
    f.setSize(400, 400);

    f.setVisible(true);

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
    String s = e.getActionCommand();
    if (s.equals("Close")) {
      f.dispose();
    }
  }
}
