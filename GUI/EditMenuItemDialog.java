import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class EditMenuItemDialog extends JDialog {
    private JTextField nameField, priceField, typeField;
    private JButton saveButton, cancelButton;
    private int menuItemId;
    private POS pos;
    private Connection conn;

    public EditMenuItemDialog(Frame owner, boolean modal, Connection conn, POS pos, int menuItemId, String name,
            float price, String type) {
        super(owner, "Edit Menu Item", modal);
        this.menuItemId = menuItemId;

        setLayout(new GridLayout(4, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField(name);
        add(nameField);

        add(new JLabel("Price:"));
        priceField = new JTextField(Float.toString(price));
        add(priceField);

        add(new JLabel("Type:"));
        typeField = new JTextField(type);
        add(typeField);

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveItem());
        add(saveButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        add(cancelButton);

        pack();
    }

    private void saveItem() {
        // Example update query (adjust according to your database schema)
        String updateQuery = "UPDATE MenuItems SET Name = ?, Price = ?, Type = ? WHERE MenuItemID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, nameField.getText());
            pstmt.setFloat(2, Float.parseFloat(priceField.getText()));
            pstmt.setString(3, typeField.getText());
            pstmt.setInt(4, menuItemId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Item updated successfully.");
            setVisible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating item: " + ex.getMessage());
        }
    }
}
