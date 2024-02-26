import javax.swing.*;
import java.sql.*;
import java.awt.*;

public class MenuPage extends JPanel {

    private Connection con;
    private POS pos;

    public MenuPage(Connection con, POS pos) {
        this.con = con;
        this.pos = pos;
        initializeUI();
    }

    private void initializeUI() {
        setBackground(Common.MAROON);
        
        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        // Create menu navbar
        JPanel navbar = new JPanel();
        navbar.setLayout(new GridLayout(4,1));
        navbar.setPreferredSize(new Dimension(300, Common.HEIGHT - 1));
        navbar.setBackground(Color.gray);


        JButton entreeButton = new JButton("Entrees");
        JButton sidesButton = new JButton("Sides");
        JButton beverageButton = new JButton("Beverages");
        JButton dessertButton = new JButton("Desserts");

        navbar.add(entreeButton);
        navbar.add(sidesButton);
        navbar.add(beverageButton);
        navbar.add(dessertButton);


        add(navbar, BorderLayout.WEST);
    }

    


    public static void main(String[] args) {
        MenuPage p = new MenuPage(null,null);
        JFrame f = new JFrame();
        f.setSize(1600,900);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(p);
        f.setVisible(true);
    }
}
