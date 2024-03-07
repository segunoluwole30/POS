import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The {@code OrderSummary} class represents the panel used to display the order
 * summary
 * in a user interface. It allows users to manage their orders by adding,
 * removing,
 * and finalizing items in the order. This panel is part of a larger application
 * concerning
 * menu navigation and order processing.
 * 
 * @author Daniel Rios
 * @author Abhishek Bhattacharyya
 * 
 */

public class OrderSummary extends JPanel {

    private JPanel middlePanel;
    private MenuPage parentPage;
    private JButton topButton;

    /**
     * Constructs a new {@code OrderSummary} panel.
     * 
     * @param mp The parent {@code MenuPage} that this panel is a part of, used for
     *           callback methods.
     */
    public OrderSummary(MenuPage mp) {
        setLayout(new BorderLayout());
        parentPage = mp;

        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        // Initializes the top button with functionality for logging out or canceling
        // the order.
        topButton = new JButton("Logout");
        topButton.addActionListener(e -> parentPage.cancelButton());
        topButton.setFont(buttonFont);
        topButton.setPreferredSize(new Dimension(300, 75));
        topButton.setBackground(Color.RED);
        topButton.setOpaque(true);
        add(topButton, BorderLayout.NORTH);
        loadMiddlePanel(); // Sets up the middle panel of this order summary panel.

        // Initializes the bottom button with functionality for finalizing the payment.
        JButton bottomButton = new JButton("Pay");
        bottomButton.addActionListener(e -> parentPage.payButton());
        bottomButton.setFont(buttonFont);
        bottomButton.setPreferredSize(new Dimension(300, 75));
        bottomButton.setBackground(Color.GREEN);
        bottomButton.setOpaque(true);
        add(bottomButton, BorderLayout.SOUTH);
    }

    /**
     * Initializes and sets up the middle panel of the order summary, where items
     * will be listed.
     */
    private void loadMiddlePanel() {
        middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setPreferredSize(new Dimension(300, 800));
        middlePanel.setBackground(Color.DARK_GRAY);
        add(middlePanel);
    }

    /**
     * Adds a button representing an item in the order to the middle panel.
     * Each item button, when clicked, removes the associated item from the order.
     * 
     * @param itemName The name of the item to be added.
     * @param price    The price of the item to be added.
     */
    public void addButton(String itemName, double price) {
        JButton b = new JButton(itemName + " $" + parentPage.round(price));
        b.setSize(WIDTH, 150); // Sets a fixed width for consistency; HEIGHT is ignored.
        middlePanel.add(b, BorderLayout.CENTER); // Adds the button to the middle panel.
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentPage.removeTransactionEntree(itemName, price);
                middlePanel.remove(b);
                middlePanel.revalidate(); // Refreshes the middle panel to update the view.
                middlePanel.repaint(); // Repaints the middle panel to reflect the updated view.
            }
        });

        // Revalidate and repaint might be needed after modification to update UI.
        middlePanel.revalidate();
        middlePanel.repaint();
    }

    /**
     * Updates the text of the top button, used for changing its function or label
     * dynamically.
     * 
     * @param label The new label to set for the top button.
     */
    public void refreshTopButton(String label) {
        topButton.setText(label);
    }
}
