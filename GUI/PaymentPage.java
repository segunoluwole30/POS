import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.awt.event.*;

public class PaymentPage extends JPanel {

    public PaymentPage() {
        setBackground(Common.MAROON);
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1250, Common.HEIGHT - 50));

        JButton cashButton = new JButton("Cash");
        JButton creditButton = new JButton("Credit Card");
        JButton diningButton = new JButton("Dining Dollars");
        JButton swipeButton = new JButton("Meal Swipe");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(cashButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(creditButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(diningButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(swipeButton, gbc);
    }
}

