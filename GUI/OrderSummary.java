import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderSummary extends JPanel {

    int HEIGHT = Common.HEIGHT-50;
    int WIDTH = 300;

    private void addButton(JPanel canvas, String itemName, String price) {
        JButton b = new JButton(itemName + " " + price);
        b.setSize(WIDTH, 150);
        canvas.add(b, BorderLayout.CENTER);

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
        canvas.revalidate();
        canvas.repaint();
    }

    private void loadMiddlePanel(JPanel canvas) {
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setPreferredSize(new Dimension(300, 800));
        middlePanel.setBackground(Common.MAROON);

        addButton(middlePanel, "Burger", "$6.99");
        addButton(middlePanel, "Fries", "$2.99");
        addButton(middlePanel, "Double Cheeseburger", "$10.99");
        addButton(middlePanel, "Bacon Burger", "$8.99");
        addButton(middlePanel, "Large Soda", "$1.99");
        addButton(middlePanel, "Chicken Tender Combo", "$6.99");
        canvas.add(middlePanel);
    }

    public OrderSummary() {
        JPanel OrderSummary = new JPanel();
        OrderSummary.setLayout(new BorderLayout());
        OrderSummary.setPreferredSize(new Dimension(300, HEIGHT));

        // Top button (cancel order)
        JButton topButton = new JButton("Cancel Order");
        OrderSummary.add(topButton, BorderLayout.NORTH);
        topButton.setBackground(Color.RED);

        // Bottom button (total and pay)
        JButton bottomButton = new JButton("Total & Pay");
        OrderSummary.add(bottomButton, BorderLayout.SOUTH);
        bottomButton.setBackground(Color.GREEN);

        loadMiddlePanel(OrderSummary);
        add(OrderSummary);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        OrderSummary os = new OrderSummary();
        f.setBackground(Common.MAROON);
        f.setLayout(new BorderLayout());
        f.setSize(400,Common.HEIGHT);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(os);
        f.setVisible(true);
    }
}