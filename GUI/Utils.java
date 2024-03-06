import java.util.Date;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utility class providing helper methods for common operations within the POS
 * application,
 * such as fetching the current date and time, and creating standardized header
 * panels for UI consistency.
 * This class is designed to be used statically and does not require
 * instantiation, hence the absence of
 * an explicit constructor.
 * 
 * @author Segun Oluwole
 */
public class Utils {

    /**
     * Returns the current date in "yyyy-MM-dd" format.
     *
     * @return A string representing the current date.
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * Returns the current time in "HH:mm:ss" format.
     *
     * @return A string representing the current time.
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * Creates and returns a standardized header panel used across various pages of
     * the POS application.
     * The header includes a back button, the manager's ID, the current date and
     * time, and a log out button.
     *
     * @param pos The POS object, providing context and functionality for the
     *            actions performed by the header buttons.
     * @return A JPanel object configured as the header panel.
     */
    public static JPanel createHeaderPanel(final POS pos) {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(95, 95, 80));
        headerPanel.setPreferredSize(new Dimension(100, 75));

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
                pos.showManagerHomePage();
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

}
