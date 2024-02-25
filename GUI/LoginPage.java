import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import javax.swing.border.*;

public class LoginPage extends JPanel {
    private JPasswordField idField;
    private JButton loginButton;
    private Connection conn;

    public LoginPage(Connection conn) {
        this.conn = conn;
        initializeUI();
    }

    private void initializeUI() {

        setBackground(new Color(128, 0, 0));

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
        buttonPanel.add(loginButton);
        add(buttonPanel, gbc);

    }
}