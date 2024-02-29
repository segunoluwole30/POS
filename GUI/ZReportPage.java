import javax.swing.*;
import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZReportPage extends JPanel {
	private Connection conn;
	private POS pos;
	private JPanel centerPanel;
	private JPanel navbar;
	private ChartPanel generatedChart;
	private JPanel chartPanel;
	private Map<String, Color[]> colorSchemes;
	private String year;
	private String month;
	private String day;

	public ZReportPage(Connection conn, POS pos) {
		this.conn = conn;
		this.pos = pos;
		initializeColorSchemes();
		initializeDate();
		setupUI();
	}

	private void generateZChart(String category, int d) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		// Execute query to retrieve data from the database
		try (Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(
			"SELECT MI.MenuItemID, MI.Name AS MenuItemName, COUNT(TE.MenuItemID) AS QuantitySold " +
			"FROM MenuItems MI " +
			"JOIN TransactionEntry TE ON MI.MenuItemID = TE.MenuItemID " +
			"JOIN Transactions T ON TE.TransactionID = T.TransactionID " +
			"WHERE DATE_PART('year', T.Date) = " + year + " " + // Filter by year
			"AND DATE_PART('month', T.Date) = " + month + " " + // Filter by month
			"AND DATE_PART('day', T.Date) = " + day + " " + // Filter by day
			"AND MI.Type = '" + category + "' " +
			"GROUP BY MI.MenuItemID, MI.Name " +
			"ORDER BY QuantitySold DESC")) {
			while (rs.next()) {
				String menuItemName = rs.getString("MenuItemName");
				int quantitySold = rs.getInt("QuantitySold");
				dataset.setValue(menuItemName, quantitySold);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Create the pie chart
		if (generatedChart == null) {
			JFreeChart chart = ChartFactory.createPieChart(
					category + " ZReport",
					dataset, 
					true, 
					true,
					false);

			generatedChart = new ChartPanel(chart);
		} else {
			JFreeChart chart = generatedChart.getChart();
			chart.setTitle(category + " ZReport");
			PiePlot plot = (PiePlot) chart.getPlot();

			Color[] colors = colorSchemes.get("purp");
        if (colors != null) {
            for (int i = 0; i < dataset.getItemCount(); i++) {
                plot.setSectionPaint(dataset.getKey(i), colors[i % colors.length]);
            }
        }

			plot.setDataset(dataset);
			plot.setSimpleLabels(true);
			plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}"));

			chartPanel.removeAll();
			chartPanel.add(generatedChart, BorderLayout.CENTER);

			revalidate();
			repaint();
		}
	}

	private void initializeDate(){
    // Create an array of JLabels and JTextFields for the date and hour inputs
    JLabel dateLabel = new JLabel("Enter the date (YYYY-MM-DD):");
    JTextField dateField = new JTextField();

    // Create an array of JComponents to pass to JOptionPane
    JComponent[] inputs = new JComponent[] {
        dateLabel, dateField
    };

    int result = JOptionPane.showConfirmDialog(this, inputs, "Enter Date and Hour", JOptionPane.OK_CANCEL_OPTION);

    // Check if the user clicked OK
    if (result == JOptionPane.OK_OPTION) {
        // Get the date and hour inputs from the text fields
        String dateInput = dateField.getText();

        // Validate and process the inputs
        if (!dateInput.isEmpty()) {
            try {
								String[] parts = dateInput.split("-");

								// Extract year, month, and day from the parts array
								year = parts[0];
								month = parts[1];
								day = parts[2];
								
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid hour input. Please enter a valid integer between 0 and 23.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if the hour input is invalid
            }
        } else {
            JOptionPane.showMessageDialog(this, "Both date and hour inputs are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if either input is empty
        }
    } else {
        return; // Exit the method if the user clicked Cancel
    }
	}

	private void setupUI() {
		// Boilerplate code to setup layout
		setLayout(new BorderLayout());

		navbar = Utils.createHeaderPanel(pos);
		navbar.setPreferredSize(new Dimension(getWidth(), 50));
		add(navbar, BorderLayout.NORTH);
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 130, 100, 50));

		// Creating three buttons vertically aligned on the left side
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
		JButton button1 = new JButton("Entree ZReport");
		JButton button2 = new JButton("Drink ZReport");
		JButton button3 = new JButton("Dessert ZReport");
		button1.addActionListener(new ButtonListener("Entree", 8));
		button2.addActionListener(new ButtonListener("Drink", 12));
		button3.addActionListener(new ButtonListener("Dessert", 18));
		buttonPanel.add(button1);
		buttonPanel.add(button2);
		buttonPanel.add(button3);

		chartPanel = new JPanel(new BorderLayout());
		chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 100));

		centerPanel.add(chartPanel, BorderLayout.CENTER);
		centerPanel.add(buttonPanel, BorderLayout.WEST);

		// Adding navbar and center panel to the main panel
		add(navbar, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);

		revalidate();
		repaint();
	}

	private class ButtonListener implements ActionListener {
		private String category;
		private int hour;

		public ButtonListener(String category, int hour) {
			this.category = category;
			this.hour = hour;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			generateZChart(category, hour);
		}
	}

	private void initializeColorSchemes() {
        colorSchemes = new HashMap<>();

        // Define some preset color schemes
        Color[] scheme1 = {Color.RED, Color.GREEN, Color.BLUE};
        Color[] scheme2 = {Color.ORANGE, Color.YELLOW, Color.CYAN};
				Color[] gradientBlueScheme = {new Color(0, 0, 255), new Color(0, 128, 255), new Color(0, 191, 255)};
				Color[] smoothColorScheme = {
					new Color(0, 255, 255),     // Cyan
					new Color(0, 204, 255),
					new Color(0, 153, 255),
					new Color(0, 102, 255),
					new Color(0, 51, 255),
					new Color(51, 0, 255),
					new Color(102, 0, 255),
					new Color(153, 0, 255),
					new Color(204, 0, 255),
					new Color(255, 0, 255)      // Magenta
			};

        colorSchemes.put("Scheme 1", scheme1);
        colorSchemes.put("Scheme 2", scheme2);
				colorSchemes.put("gradientBlue", gradientBlueScheme);
				colorSchemes.put("purp", smoothColorScheme);

    }

	public void refreshHeader() {
		remove(navbar);
		navbar = Utils.createHeaderPanel(pos);
		add(navbar, BorderLayout.NORTH);
		revalidate();
		repaint();
	}

}
