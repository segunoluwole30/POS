import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LoginPage extends JPanel {
    private JPasswordField idField;
    private JButton loginButton;
    private Connection conn;
    private POS pos;

    public LoginPage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();

    }

    private void initializeUI() {

        setBackground(new Color(78, 18, 26));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 10, 0); // Add spacing below the idField

        // Create a panel to hold the label and password field
        JPanel idPanel = new JPanel();
        idPanel.setOpaque(false);
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS)); // Set layout to vertical

        // Add "ID:" label
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 20));
        idLabel.setForeground(Color.WHITE);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        idLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100)); // Add empty border on the right to left align
                                                                          // with idField
        idPanel.add(idLabel);
        idPanel.add(Box.createVerticalStrut(5)); // Add some vertical space

        // Add idField
        idField = new JPasswordField(20);
        idField.setPreferredSize(new Dimension(200, 40));
        idPanel.add(idField);

        add(idPanel, gbc);
        gbc.gridy++; // Move to the next row
        gbc.insets = new Insets(0, 0, 0, 0); // Reset insets for the login button

        // Create a panel to hold the login button and center it horizontally
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform database check here
                String enteredID = new String(idField.getPassword());
                String sqlStatement = "SELECT * FROM Employees where EmployeeID = '" + enteredID + "';";
                // If the ID is found, proceed to the menu page
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet result = stmt.executeQuery(sqlStatement);
                    if (result.next()) {
                        // Employee ID found in the database
                        // You can perform further actions here
                        System.out.println("Employee ID found");
                        pos.showMenuPage();
                    } else {
                        // Employee ID not found in the database
                        // You can handle this case accordingly
                        JOptionPane.showMessageDialog(null, "Invalid Employee ID", "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Employee ID not found");
                    }
                } catch (SQLException exc) {
                    // Handle any potential exceptions
                    exc.printStackTrace();
                }
            }
        });
        buttonPanel.add(loginButton);
        add(buttonPanel, gbc);

    }
}