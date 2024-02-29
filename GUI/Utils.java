import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import java.sql.*;
//import java.util.concurrent.Flow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// TODO: Fix Comments esp. lines 66 - 95

public class Utils {

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

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

}
