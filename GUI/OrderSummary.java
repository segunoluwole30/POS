import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderSummary extends JPanel {

    private JPanel middlePanel;
    private MenuPage parentMenu;

    public OrderSummary(MenuPage mp) {

        setLayout(new BorderLayout());
        // setPreferredSize(new Dimension(300, 300));

        Font buttonFont = new Font("Arial", Font.BOLD, 20);
    
        // Top button (cancel order)
        JButton topButton = new JButton("Cancel Order");
        topButton.addActionListener(e -> mp.cancelButton());
        topButton.setFont(buttonFont);
        topButton.setPreferredSize(new Dimension(300, 75));
        topButton.setBackground(Color.DARK_GRAY);
        topButton.setOpaque(true);
        add(topButton, BorderLayout.NORTH);
        loadMiddlePanel();

        // Bottom button (total and pay)
        JButton bottomButton = new JButton("Pay");
        bottomButton.addActionListener(e -> mp.payButton());
        bottomButton.setFont(buttonFont);
        bottomButton.setPreferredSize(new Dimension(300, 75));
        bottomButton.setBackground(Color.DARK_GRAY);
        bottomButton.setOpaque(true);
        add(bottomButton, BorderLayout.SOUTH);
    }

    private void loadMiddlePanel() {
        middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setPreferredSize(new Dimension(300, 800));
        middlePanel.setBackground(Color.DARK_GRAY);

        // addButton("Burger", "$6.99");

        add(middlePanel);
    }

    public void addButton(String itemName, float price) {
        JButton b = new JButton(itemName + " $" + price);
        b.setSize(WIDTH, 150);
        middlePanel.add(b, BorderLayout.CENTER);

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = b.getParent();
                parent.remove(b);
                parent.revalidate();
                parent.repaint();
            }
        });

        // Might not be needed
        middlePanel.revalidate();
        middlePanel.repaint();
    }

}