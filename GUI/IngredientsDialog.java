import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class IngredientsDialog extends JDialog {
    private JTable ingredientsTable;
    private DefaultTableModel tableModel;
    private JButton saveButton, cancelButton, addIngredientButton, removeIngredientButton;
    private JTextField newIngredientNameField, quantityField;
    private Connection conn;
    private int menuItemId;
    private Map<String, Integer> ingredientNameToIdMap = new HashMap<>();

    public IngredientsDialog(Frame owner, Connection conn, int menuItemId) {
        super(owner, "Select Ingredients", true);
        this.conn = conn;
        this.menuItemId = menuItemId;
        initializeUI();
        loadCurrentIngredients();
    }

    private void initializeUI() {
        tableModel = new DefaultTableModel(new Object[] { "Ingredient", "Quantity" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // Make the table cells editable
            }
        };
        ingredientsTable = new JTable(tableModel);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        newIngredientNameField = new JTextField();
        quantityField = new JTextField();
        inputPanel.add(new JLabel("New Ingredient:"));
        inputPanel.add(newIngredientNameField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(ingredientsTable), BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);

        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveIngredients());
        buttonPanel.add(saveButton);

        addIngredientButton = new JButton("Add Ingredient");
        addIngredientButton.addActionListener(e -> addNewIngredient());
        buttonPanel.add(addIngredientButton);

        removeIngredientButton = new JButton("Remove Ingredient");
        removeIngredientButton.addActionListener(e -> removeSelectedIngredients());
        buttonPanel.add(removeIngredientButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 400);
    }

    private void loadCurrentIngredients() {
        String sql = "SELECT i.Name, mi.Quantity FROM MenuItemIngredients mi " +
                "JOIN IngredientsInventory i ON mi.IngredientID = i.IngredientID " +
                "WHERE mi.MenuItemID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuItemId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                float quantity = rs.getFloat("Quantity");
                tableModel.addRow(new Object[] { name, quantity });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading ingredients: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewIngredient() {
        String newIngredient = newIngredientNameField.getText().trim();
        String quantity = quantityField.getText().trim();
        if (!newIngredient.isEmpty() && !quantity.isEmpty()) {
            // Check if ingredient already exists in the table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(newIngredient)) {
                    JOptionPane.showMessageDialog(this,
                            "Ingredient already added. Please update its quantity if needed.", "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            tableModel.addRow(new Object[] { newIngredient, quantity });
            newIngredientNameField.setText("");
            quantityField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Ingredient name and quantity cannot be empty.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedIngredients() {
        int[] selectedRows = ingredientsTable.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            tableModel.removeRow(selectedRows[i]);
        }
    }

    private void saveIngredients() {
        try {
            // Attempt to delete existing associations
            String deleteSql = "DELETE FROM MenuItemIngredients WHERE MenuItemID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, menuItemId);
                pstmt.executeUpdate();
            }

            // Prepare to insert new or existing ingredients
            String insertIngredientSql = "INSERT INTO IngredientsInventory (Name) VALUES (?)";
            String insertMenuItemIngredientSql = "INSERT INTO MenuItemIngredients (MenuItemID, IngredientID, Quantity) VALUES (?, ?, ?)";

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String ingredientName = tableModel.getValueAt(i, 0).toString();
                Float quantity = Float.parseFloat(tableModel.getValueAt(i, 1).toString());

                // Check if the ingredient exists; if not, add to IngredientsInventory
                Integer ingredientId = ingredientNameToIdMap.get(ingredientName);
                if (ingredientId == null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(insertIngredientSql,
                            Statement.RETURN_GENERATED_KEYS)) {
                        pstmt.setString(1, ingredientName);
                        pstmt.executeUpdate();
                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                ingredientId = generatedKeys.getInt(1);
                                // Update map to include new ingredient
                                ingredientNameToIdMap.put(ingredientName, ingredientId);
                            }
                        }
                    }
                }

                // Now, insert the association in MenuItemIngredients
                if (ingredientId != null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(insertMenuItemIngredientSql)) {
                        pstmt.setInt(1, menuItemId);
                        pstmt.setInt(2, ingredientId);
                        pstmt.setFloat(3, quantity);
                        pstmt.executeUpdate();
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Ingredients and quantities saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving ingredients and quantities: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        setVisible(false);
    }

    private void mapNamesToIds() {
        String sql = "SELECT IngredientID, Name FROM IngredientsInventory";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int ingredientId = rs.getInt("IngredientID");
                String name = rs.getString("Name");
                ingredientNameToIdMap.put(name, ingredientId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching ingredient IDs: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}