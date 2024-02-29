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

public class ZZReportPage extends JPanel {
	private Connection conn;
	private POS pos;
	private JPanel centerPanel;
	private JPanel navbar;
	private ChartPanel generatedChart;
	private JPanel chartPanel;
	private Map<String, Color[]> colorSchemes;
	
	private String start_year;
	private String start_month;
	private String start_day;

	private String end_year;
	private String end_month;
	private String end_day;

	public ZZReportPage(Connection conn, POS pos) {
		this.conn = conn;
		this.pos = pos;
		initializeColorSchemes();
		initializeDate();
		setupUI();
	}

	private void generateZZChart(String category, int hour) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		// Execute query to retrieve data from the database
		try (Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT MI.MenuItemID, MI.Name AS MenuItemName, COUNT(TE.MenuItemID) AS QuantitySold " +
					"FROM MenuItems MI " +
					"JOIN TransactionEntry TE ON MI.MenuItemID = TE.MenuItemID " +
					"JOIN Transactions T ON TE.TransactionID = T.TransactionID " +
					"WHERE T.Date >= '" + start_year + "-" + start_month + "-" + start_day + "' " + // Start date condition
					"AND T.Date <= '" + end_year + "-" + end_month + "-" + end_day + "' " + // End date condition
					"AND MI.Type = '" + category + "' " +
					"GROUP BY MI.MenuItemID, MI.Name " +
					"ORDER BY QuantitySold DESC")) {
			// Process the query results
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
					category + " ZZReport",
					dataset,
					true,
					true,
					false);

			generatedChart = new ChartPanel(chart);
		} else {
			// Otherwise, update the dataset associated with the existing chart
			JFreeChart chart = generatedChart.getChart();
			chart.setTitle(category + " ZZReport");
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
    JLabel start_dateLabel = new JLabel("Enter the start date (YYYY-MM-DD):");
    JTextField start_dateField = new JTextField();
		JLabel end_dateLabel = new JLabel("Enter the end date (YYYY-MM-DD):");
    JTextField end_dateField = new JTextField();

    // Create an array of JComponents to pass to JOptionPane
    JComponent[] inputs = new JComponent[] {
        start_dateLabel, start_dateField,
				end_dateLabel, end_dateField
    };

    int result = JOptionPane.showConfirmDialog(this, inputs, "Enter Date and Hour", JOptionPane.OK_CANCEL_OPTION);

    // Check if the user clicked OK
    if (result == JOptionPane.OK_OPTION) {
        // Get the date and hour inputs from the text fields
        String s_dateInput = start_dateField.getText();
				String e_dateInput = end_dateField.getText();

        // Validate and process the inputs
        if (!s_dateInput.isEmpty() || !e_dateInput.isEmpty()) {
            try {
								String[] parts = s_dateInput.split("-");
								String[] e_parts = e_dateInput.split("-");

								// Extract year, month, and day from the parts array
								start_year = parts[0];
								start_month = parts[1];
								start_day = parts[2];

								end_year = e_parts[0];
								end_month = e_parts[1];
								end_day = e_parts[2];
								
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid hour input. Please enter a valid integer between 0 and 23.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Both date and hour inputs are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    } else {
        return;
    }
	}

	private void setupUI() {
		// Boilerplate code to setup layout
		setLayout(new BorderLayout());

		// Creating the top navbar
		navbar = Utils.createHeaderPanel(pos);
		navbar.setPreferredSize(new Dimension(getWidth(), 50));
		add(navbar, BorderLayout.NORTH);
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 130, 100, 50));

		// Creating three buttons vertically aligned on the left side
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
		JButton button1 = new JButton("Entree ZZReport");
		JButton button2 = new JButton("Drink ZZReport");
		JButton button3 = new JButton("Dessert ZZReport");
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
			generateZZChart(category, hour);
		}
	}

	private void initializeColorSchemes() {
		colorSchemes = new HashMap<>();

			// Define some preset color schemes
			Color[] scheme1 = {Color.RED, Color.GREEN, Color.BLUE};
			Color[] scheme2 = {Color.ORANGE, Color.YELLOW, Color.CYAN};
			Color[] gradientBlueScheme = {new Color(0, 0, 255), new Color(0, 128, 255), new Color(0, 191, 255)};
			Color[] smoothColorScheme = {
				new Color(204, 255, 204), // Lightest green
				new Color(153, 255, 153),
				new Color(102, 255, 102),
				new Color(51, 204, 51),
				new Color(0, 153, 0),
				new Color(0, 102, 0),
				new Color(0, 51, 0),
				new Color(0, 51, 0),      // Darkest green
				new Color(0, 51, 0),
				new Color(0, 51, 0)
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
