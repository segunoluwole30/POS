public class ItemEntry extends JPanel {
    private JButton removeButton;
    private JLabel priceLabel;

    public ItemEntry(String itemName, double price) {
        setLayout(new GridLayout());
        setSize(new Dimension(WIDTH, 50)); // Ensure it doesn't expand vertically
        setBackground(Color.WHITE); // Set background or any color you like

        removeButton = new JButton(itemName + "  " + price);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFromParent();
            }
        });

        //priceLabel = new JLabel(String.format("$%.2f", price), SwingConstants.RIGHT);
        
        // Adjust the layout
        add(removeButton, BorderLayout.WEST);
        //add(priceLabel, BorderLayout.EAST);
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