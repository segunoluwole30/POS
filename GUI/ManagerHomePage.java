import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.sql.*;
//import java.util.concurrent.Flow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents the manager's home page in a Point of Sale (POS) system. This page
 * provides the manager with various options to navigate through the POS system,
 * including viewing reports, managing menu items, and logging out.
 * 
 * @author Segun Oluwole
 */
public class ManagerHomePage extends JPanel {
    private Connection conn;
    private POS pos;
    private JPanel headerPanel;

    /**
     * Constructs a new ManagerHomePage with a specified database connection and a
     * reference to the main POS system.
     * 
     * @param conn the SQL Connection object for database operations
     * @param pos  the POS system instance
     */
    public ManagerHomePage(Connection conn, POS pos) {
        this.conn = conn;
        this.pos = pos;
        initializeUI();
    }

    /**
     * Initializes the user interface of the manager's home page, setting up the
     * layout, background color, header, and navigation buttons.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Common.DARKCYAN);
        displayManagerHeader();
        displayButtons();
    }

    /**
     * Displays the header panel at the top of the manager's home page. The header
     * includes navigation and utility buttons.
     */
    private void displayManagerHeader() {
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Creates and returns a header panel for the manager's home page. The panel
     * includes a back button, manager ID display, current date and time, and a log
     * out button.
     * 
     * @return the created header panel
     */
    private JPanel createHeaderPanel() {

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(95, 95, 80));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 75));

        // Left side panel for back button and manager ID
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));

        // Back button
        JButton backButton = new JButton("Back");
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

        JLabel headerText = new JLabel("Manager ID: " + pos.getEmployeeID());
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

    /**
     * Adds and lays out the main navigation buttons on the manager's home page.
     * These buttons allow the manager to navigate to different sections of the POS
     * system, such as order history, inventory, menu item management, and report
     * generation.
     */
    private void displayButtons() {
        JPanel buttonPanel = new JPanel(new GridBagLayout()); // Main panel with 2 columns
        buttonPanel.setBackground(Common.DARKCYAN);

        Font buttonFont = new Font("Arial", Font.BOLD, 30);

        JButton orderHistoryButton = new JButton("View Order History Report");
        orderHistoryButton.setFont(buttonFont);
        orderHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pos.showOrderHistoryPage();
            }
        });

        JButton inventoryReportButton = new JButton("View Inventory Report");
        inventoryReportButton.setFont(buttonFont);
        inventoryReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pos.showInventoryPage();
            }
        });

        JButton menuItemsButton = new JButton("Edit Menu Items");
        menuItemsButton.setFont(buttonFont);
        menuItemsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewMenuItemsDialog dialog = new ViewMenuItemsDialog(
                        (Frame) SwingUtilities.getWindowAncestor(ManagerHomePage.this), conn);
                dialog.setLocationRelativeTo(ManagerHomePage.this);
                dialog.refreshTableData();
                dialog.setVisible(true);
            }
        });

        JButton xReportButton = new JButton("Generate X Report");
        xReportButton.setFont(buttonFont);
        xReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pos.showXReportPage();
            }
        });

        JButton zReportButton = new JButton("Generate Z Report");
        zReportButton.setFont(buttonFont);
        zReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pos.showZReportPage();
            }
        });
        JButton zzReportButton = new JButton("Generate ZZ Report");
        zzReportButton.setFont(buttonFont);
        zzReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pos.showZZReportPage();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20); // Padding between components
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(orderHistoryButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(inventoryReportButton, gbc);

        gbc.gridy = 2;
        buttonPanel.add(menuItemsButton, gbc);

        gbc.gridx = 1;
        buttonPanel.add(zzReportButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(zReportButton, gbc);

        gbc.gridy = 0;
        buttonPanel.add(xReportButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Refreshes the header to update any dynamic content, such as the current time
     * or manager ID. This method is useful for ensuring that the displayed
     * information remains accurate over time.
     */
    public void refreshHeader() {
        // Remove the old header
        remove(headerPanel);
        // Create and add a new header panel using the updated employeeID
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        // Revalidate and repaint to ensure UI updates are displayed
        revalidate();
        repaint();
    }
}
