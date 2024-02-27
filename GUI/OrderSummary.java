import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderSummary extends JPanel {

    int HEIGHT = Common.HEIGHT-50;
    int WIDTH = 300;
    
    public class ItemEntry extends JPanel {
        private JButton ItemButton;
    
        public ItemEntry(String itemName, double price) {
            setLayout(new GridLayout());
            setSize(new Dimension(WIDTH, 50)); // Ensure it doesn't expand vertically
            setBackground(Color.WHITE); // Set background or any color you like
    
            ItemButton = new JButton(itemName + "  " + price);
            ItemButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeFromParent();
                }
            });

            add(ItemButton, BorderLayout.WEST);

        }
    
        private void removeFromParent() {
            Container parent = this.getParent();
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }

    private void addItemEntry(JPanel canvas, String itemName, double price) {
        ItemEntry itemEntry = new ItemEntry(itemName, price);
        canvas.add(itemEntry, BorderLayout.CENTER);
        canvas.revalidate();
        canvas.repaint();
    }

    private void loadMiddlePanel() {
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setPreferredSize(new Dimension(300, 800));
        middlePanel.setBackground(Common.MAROON);

        // You can call addItemEntry whenever you want to add new items
        addItemEntry(middlePanel, "Burger", 19.99);
        addItemEntry(middlePanel, "Example Item 2", 29.99);
        addItemEntry(middlePanel, "Example Item 2", 29.99);
        addItemEntry(middlePanel, "Example Item 2", 29.99);
        addItemEntry(middlePanel, "Example Item 2", 29.99);

        add(middlePanel, BorderLayout.CENTER);
    }

    public OrderSummary() {
        JPanel menuSummary = new JPanel();
        menuSummary.setLayout(new BorderLayout());
        menuSummary.setPreferredSize(new Dimension(300, HEIGHT));

        // Top button (cancel order)
        JButton topButton = new JButton("Cancel Order");
        menuSummary.add(topButton, BorderLayout.NORTH);
        topButton.setBackground(Color.RED);

        // Bottom button (total and pay)
        JButton bottomButton = new JButton("Total & Pay");
        menuSummary.add(bottomButton, BorderLayout.SOUTH);
        bottomButton.setBackground(Color.GREEN);

        loadMiddlePanel();
        add(menuSummary);
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