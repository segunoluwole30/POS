import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import java.sql.*;
//import java.util.concurrent.Flow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerHomePage extends JPanel {
    // private Connection conn;
    private POS pos;

    public ManagerHomePage(POS pos) {
        // this.conn = conn;
        this.pos = pos;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Common.DARKCYAN);
        displayManagerHeader();
        displayButtons();
    }

    private void displayManagerHeader() {
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createHeaderPanel() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(95, 95, 80));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 75));

        // Left side panel for back button and manager ID
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.RED);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform action to log out
                pos.showMenuPage();
            }
        });
        leftPanel.add(backButton);

        // Add space between back button and manager ID
        leftPanel.add(Box.createHorizontalStrut(10));

        JLabel headerText = new JLabel("Manager ID: XXXX-XXXX");
        headerText.setForeground(Color.WHITE);
        headerText.setFont(headerText.getFont().deriveFont(24f));
        leftPanel.add(headerText);

        // Right side panel for date, time, and log out button
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));

        // Add date label
        JLabel dateLabel = new JLabel(Utils.getCurrentDate()); // Implement getCurrentDate() to return the current date
        dateLabel.setForeground(Color.WHITE);
        rightPanel.add(dateLabel);

        // Add space between the date and time
        rightPanel.add(Box.createHorizontalStrut(5));

        // Add time label
        JLabel timeLabel = new JLabel(Utils.getCurrentTime()); // Implement getCurrentTime() to return the current time
        timeLabel.setForeground(Color.WHITE);
        rightPanel.add(timeLabel);

        // Add space between the time and log out button
        rightPanel.add(Box.createHorizontalStrut(10));

        // Right side login button
        JButton loginButton = new JButton("Log Out");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.RED);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform action to log out
                pos.showLoginPage();
            }
        });

        rightPanel.add(loginButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        JLabel centerText = new JLabel("MANAGER MODE");
        centerText.setForeground(Color.WHITE);
        centerText.setFont(centerText.getFont().deriveFont(24f));
        centerPanel.add(centerText);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        headerPanel.add(centerPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private void displayButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2)); // Main panel with 2 columns

        JPanel leftPanel = new JPanel(new GridLayout(3, 1)); // Left panel with 2 rows
        leftPanel.setBackground(Color.LIGHT_GRAY); // Background color for visualization

        JButton orderHistoryButton = new JButton("View Order History Report");
        JButton inventoryReportButton = new JButton("View Inventory Report");
        JButton menuItemsButton = new JButton("Edit Menu Items");

        leftPanel.add(orderHistoryButton);
        leftPanel.add(inventoryReportButton);
        leftPanel.add(menuItemsButton);

        JPanel rightPanel = new JPanel(new GridLayout(3, 1)); // Right panel with 3 rows
        rightPanel.setBackground(Color.GRAY); // Background color for visualization

        JButton xReportButton = new JButton("Generate X Report");
        JButton zReportButton = new JButton("Generate Z Report");
        JButton zzReportButton = new JButton("Generate ZZ Report");

        rightPanel.add(xReportButton);
        rightPanel.add(zReportButton);
        rightPanel.add(zzReportButton);

        buttonPanel.add(leftPanel); // Add left panel to the main panel
        buttonPanel.add(rightPanel); // Add right panel to the main panel

        add(buttonPanel, BorderLayout.CENTER);
    }

    // for testing purposes
    // public static void main(String[] args) {
    // ManagerHomePage p = new ManagerHomePage(null);
    // JFrame f = new JFrame();
    // f.setSize(1600, 900);
    // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // f.add(p);
    // f.setVisible(true);
    // }

}
