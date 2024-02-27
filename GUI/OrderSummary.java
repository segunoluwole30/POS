import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderSummary extends JPanel {

    int HEIGHT = Common.HEIGHT-50;
    int WIDTH = 300;
    
    private JPanel loadMiddlePanel() {
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setPreferredSize(new Dimension(300, HEIGHT-100));
        middlePanel.setBackground(Common.MAROON);

        // You can call addItemEntry whenever you want to add new items
        JButton item1 = new JButton("Burger   $7.99");
        middlePanel.add(item1, BorderLayout.CENTER);
        JButton item2 = new JButton("Chicken Sandwich   $5.99");
        middlePanel.add(item2, BorderLayout.CENTER);
        JButton item3 = new JButton("Bacon Double   $7.99");
        middlePanel.add(item3, BorderLayout.CENTER);      

        return(middlePanel);
    }

    public OrderSummary() {
        JPanel menuSummary = new JPanel();
        menuSummary.setLayout(new BorderLayout());
        menuSummary.setPreferredSize(new Dimension(300, HEIGHT));

        add(loadMiddlePanel(), BorderLayout.CENTER);

        // Top button (cancel order)
        JButton topButton = new JButton("Cancel Order");
        menuSummary.add(topButton, BorderLayout.NORTH);
        topButton.setBackground(Color.RED);

        // Bottom button (total and pay)
        JButton bottomButton = new JButton("Total & Pay");
        menuSummary.add(bottomButton, BorderLayout.SOUTH);
        bottomButton.setBackground(Color.GREEN);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        OrderSummary os = new OrderSummary();
        f.setSize(400,Common.HEIGHT);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(os);
        f.setVisible(true);
    }
}