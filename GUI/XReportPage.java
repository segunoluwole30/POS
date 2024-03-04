import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class XReportPage extends JPanel {
	private static Connection conn;
	private static POS pos;
	private JPanel centerPanel;
	private JPanel navbar;
	private ChartPanel generatedChart;
	private JTable generatedTable;
	private JPanel chartPanel;
	private Map<String, Color[]> colorSchemes;
	private String year;
	private String month;
	private String day;
	private int hour;

	private boolean goBack = false;

	public XReportPage(Connection conn, POS pos) {
		this.conn = conn;
		this.pos = pos;
		initializeColorSchemes();
		initializeDate();
		if(goBack){
			pos.showManagerHomePage();
		}
		else{
			setupUI();
		}
	}

	private void generateXChart(String category) {
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
				"AND DATE_PART('hour', T.Date) = " + hour + " " +
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
					category + " XReport", 
					dataset, 
					true, 
					true,
					false);

			generatedChart = new ChartPanel(chart);
		} else {
			// Otherwise, update the dataset associated with the existing chart
			JFreeChart chart = generatedChart.getChart();
			chart.setTitle(category + " XReport");
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

	private void generateSalesReport() {
		
		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(
				"SELECT MI.MenuItemID, MI.Name AS MenuItemName, COUNT(TE.MenuItemID) AS QuantitySold " +
				"FROM MenuItems MI " +
				"JOIN TransactionEntry TE ON MI.MenuItemID = TE.MenuItemID " +
				"JOIN Transactions T ON TE.TransactionID = T.TransactionID " +
				"WHERE DATE_PART('year', T.Date) = " + year + " " + // Filter by year
				"AND DATE_PART('month', T.Date) = " + month + " " + // Filter by month
				"AND DATE_PART('day', T.Date) = " + day + " " + // Filter by day
				"AND DATE_PART('hour', T.Date) = " + hour + " " +
				"GROUP BY MI.MenuItemID, MI.Name " +
				"ORDER BY QuantitySold DESC");
	
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
	
			ArrayList<String[]> rows = new ArrayList<>();
	
			while (result.next()) {
					String[] row = new String[columnCount];
					for (int i = 1; i <= columnCount; i++) {
							row[i - 1] = result.getString(i);
					}
					rows.add(row);
			}
	
			// Convert the list of rows to a 2D array
			String[][] data = new String[rows.size()][];
			for (int i = 0; i < rows.size(); i++) {
					data[i] = rows.get(i);
			}
	
			String[] columnEntries = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
					columnEntries[i] = metaData.getColumnName(i + 1);
			}
	
			generatedTable = new JTable(data, columnEntries);
	
			JScrollPane salesReportScrollPane = new JScrollPane();
			salesReportScrollPane.setViewportView(generatedTable);
			salesReportScrollPane.setPreferredSize(new Dimension(salesReportScrollPane.getPreferredSize().width, Common.HEIGHT / 4));
	
			chartPanel.removeAll();
			chartPanel.add(salesReportScrollPane, BorderLayout.CENTER);
	
			revalidate();
			repaint();
	
	} catch (SQLException e) {
			e.printStackTrace();
			// Handle SQL exception here, such as displaying an error message
	}
	}

	private void generateProductUsageReport() {
		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery("SELECT ii.Name AS InventoryItem, SUM(mii.Quantity) AS UsedQuantity " +
																									"FROM Transactions t " +
																									"JOIN TransactionEntry te ON t.TransactionID = te.TransactionID " +
																									"JOIN MenuItems mi ON te.MenuItemID = mi.MenuItemID " +
																									"JOIN MenuItemIngredients mii ON mi.MenuItemID = mii.MenuItemID " +
																									"JOIN IngredientsInventory ii ON mii.IngredientID = ii.IngredientID " +
																									"WHERE DATE_PART('year', T.Date) = " + year + " " + // Filter by year
																									"AND DATE_PART('month', T.Date) = " + month + " " + // Filter by month
																									"AND DATE_PART('day', T.Date) = " + day + " " + // Filter by day
																									"AND DATE_PART('hour', T.Date) = " + hour + " " +
																									"GROUP BY ii.Name " +
                                                	"ORDER BY UsedQuantity DESC;");
	
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
	
			ArrayList<String[]> rows = new ArrayList<>();
	
			while (result.next()) {
					String[] row = new String[columnCount];
					for (int i = 1; i <= columnCount; i++) {
							row[i - 1] = result.getString(i);
					}
					rows.add(row);
			}
	
			// Convert the list of rows to a 2D array
			String[][] data = new String[rows.size()][];
			for (int i = 0; i < rows.size(); i++) {
					data[i] = rows.get(i);
			}
	
			String[] columnEntries = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
					columnEntries[i] = metaData.getColumnName(i + 1);
			}
	
			generatedTable = new JTable(data, columnEntries);
	
			JScrollPane salesReportScrollPane = new JScrollPane();
			salesReportScrollPane.setViewportView(generatedTable);
			salesReportScrollPane.setPreferredSize(new Dimension(salesReportScrollPane.getPreferredSize().width, Common.HEIGHT / 4));
	
			chartPanel.removeAll();
			chartPanel.add(salesReportScrollPane, BorderLayout.CENTER);
	
			revalidate();
			repaint();
	
	} catch (SQLException e) {
			e.printStackTrace();
			// Handle SQL exception here, such as displaying an error message
	}
	}

	private void generateBestPairsReport() {
		
		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(
        "SELECT mi1.Name AS MenuItem1, mi2.Name AS MenuItem2, COUNT(*) AS Frequency " +
        "FROM TransactionEntry te1 " +
        "JOIN TransactionEntry te2 ON te1.TransactionID = te2.TransactionID AND te1.MenuItemID < te2.MenuItemID " +
        "JOIN MenuItems mi1 ON te1.MenuItemID = mi1.MenuItemID " +
        "JOIN MenuItems mi2 ON te2.MenuItemID = mi2.MenuItemID " +
        "JOIN Transactions t ON te1.TransactionID = t.TransactionID " +
        "WHERE DATE_PART('year', t.Date) = " + year + " " + // Filter by year
        "AND DATE_PART('month', t.Date) = " + month + " " + // Filter by month
        "AND DATE_PART('day', t.Date) = " + day + " " + // Filter by day
        "AND DATE_PART('hour', t.Date) = " + hour + " " + // Filter by hour
        "GROUP BY mi1.Name, mi2.Name " +
        "ORDER BY Frequency DESC");
	
			ResultSetMetaData metaData = result.getMetaData();
			int columnCount = metaData.getColumnCount();
	
			ArrayList<String[]> rows = new ArrayList<>();
	
			while (result.next()) {
					String[] row = new String[columnCount];
					for (int i = 1; i <= columnCount; i++) {
							row[i - 1] = result.getString(i);
					}
					rows.add(row);
			}
	
			// Convert the list of rows to a 2D array
			String[][] data = new String[rows.size()][];
			for (int i = 0; i < rows.size(); i++) {
					data[i] = rows.get(i);
			}
	
			String[] columnEntries = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
					columnEntries[i] = metaData.getColumnName(i + 1);
			}
	
			generatedTable = new JTable(data, columnEntries);
	
			JScrollPane salesReportScrollPane = new JScrollPane();
			salesReportScrollPane.setViewportView(generatedTable);
			salesReportScrollPane.setPreferredSize(new Dimension(salesReportScrollPane.getPreferredSize().width, Common.HEIGHT / 4));
	
			chartPanel.removeAll();
			chartPanel.add(salesReportScrollPane, BorderLayout.CENTER);
	
			revalidate();
			repaint();
	
	} catch (SQLException e) {
			e.printStackTrace();
			// Handle SQL exception here, such as displaying an error message
	}
	}

	private void initializeDate() {
		// Create an array of JLabels and JTextFields for the date and hour inputs
		JLabel dateLabel = new JLabel("Enter the date (YYYY-MM-DD):");
		JTextField dateField = new JTextField();
		JLabel hourLabel = new JLabel("Enter the hour (8-20):");
		JTextField hourField = new JTextField();

		// Create an array of JComponents to pass to JOptionPane
		JComponent[] inputs = new JComponent[] {
				dateLabel, dateField,
				hourLabel, hourField
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Enter Date and Hour", JOptionPane.OK_CANCEL_OPTION);

		// Check if the user clicked OK
		if (result == JOptionPane.OK_OPTION) {
				// Get the date and hour inputs from the text fields
				String dateInput = dateField.getText();
				String hourInput = hourField.getText();

				// Validate and process the inputs
				if (!dateInput.isEmpty() && !hourInput.isEmpty()) {
						try {
								// Convert hourInput to an integer
								hour = Integer.parseInt(hourInput);

								String[] parts = dateInput.split("-");

								// Extract year, month, and day from the parts array
								year = parts[0];
								month = parts[1];
								day = parts[2];
								
								// Convert year, month, and day strings to integers
								// year = Integer.parseInt(yearStr);
								// month = Integer.parseInt(monthStr);
								// day = Integer.parseInt(dayStr);
								// Use the date and hour inputs in your code
								// Execute the query to retrieve data from the database...
						} catch (NumberFormatException ex) {
								JOptionPane.showMessageDialog(this, "Invalid hour input. Please enter a valid integer between 8 and 20.", "Error", JOptionPane.ERROR_MESSAGE);
								return; // Exit the method if the hour input is invalid
						}
				} else {
						JOptionPane.showMessageDialog(this, "Both date and hour inputs are required.", "Error", JOptionPane.ERROR_MESSAGE);
						return; // Exit the method if either input is empty
				}
		} 
		else if(result == JOptionPane.CANCEL_OPTION){
			goBack = true;
		}
		else {
        return;
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
		JButton button1 = new JButton("Entree XReport");
		JButton button2 = new JButton("Drink XReport");
		JButton button3 = new JButton("Dessert XReport");
		JButton button4 = new JButton("Sales Report");
		JButton button5 = new JButton("Product Usage");
		JButton button6 = new JButton("Excess Report");
		JButton button7 = new JButton("Best Product Combos");
		JButton button8 = new JButton("Cool Button 1");
		JButton button9 = new JButton("Cool Button 2");

		button1.addActionListener(new ButtonListener("Entree", "pie_chart"));
		button2.addActionListener(new ButtonListener("Drink", "pie_chart"));
		button3.addActionListener(new ButtonListener("Dessert", "pie_chart"));
		button4.addActionListener(new ButtonListener("n/a", "sales_report"));
		button5.addActionListener(new ButtonListener("n/a", "product_usage"));
		button6.addActionListener(new ButtonListener("n/a", "excess_report"));
		button7.addActionListener(new ButtonListener("n/a", "sells_together"));

		buttonPanel.add(button1);
		buttonPanel.add(button2);
		buttonPanel.add(button3);
		buttonPanel.add(button4);
		buttonPanel.add(button5);
		buttonPanel.add(button6);
		buttonPanel.add(button7);
		buttonPanel.add(button8);
		buttonPanel.add(button9);

		chartPanel = new JPanel(new BorderLayout());
		chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 100));
		//chartPanel.add(generatedChart);

		centerPanel.add(chartPanel, BorderLayout.CENTER);
		centerPanel.add(buttonPanel, BorderLayout.WEST);

		// Adding navbar and center panel to the main panel
		add(navbar, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
	}

	private class ButtonListener implements ActionListener {
		private String category;
		private String action;

		public ButtonListener(String category, String action) {
			this.category = category;
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (action == "pie_chart"){
				generateXChart(category);
			}
			else if (action == "sales_report"){
				//Given a time window, display the sales by item from the order history.
				generateSalesReport();
			}
			else if (action == "product_usage"){
				//Given a time window, display a chart (table, graph, diagram) that depicts the amount of inventory used during that time period.
				generateProductUsageReport();
			}
			else if (action == "excess_report"){
				//Given a timestamp, display the list of items that only sold less than 10% of their inventory between the timestamp and the current time, assuming no restocks have happened during the window.
				//generateExcessReport()
			}
			else if (action == "sells_together"){
				//Given a time window, display a list of pairs of menu items that sell together often, popular or not, sorted by most frequent.
				generateBestPairsReport();
			}
		}
	}

	private void initializeColorSchemes() {
        colorSchemes = new HashMap<>();

        // Define some preset color schemes
        Color[] scheme1 = {Color.RED, Color.GREEN, Color.BLUE};
        Color[] scheme2 = {Color.ORANGE, Color.YELLOW, Color.CYAN};
				Color[] gradientBlueScheme = {new Color(0, 0, 255), new Color(0, 128, 255), new Color(0, 191, 255)};
				Color[] smoothColorScheme = {
					new Color(180, 160, 255), 
					new Color(153, 128, 255),
					new Color(126, 96, 255),
					new Color(99, 64, 255),
					new Color(72, 32, 255),
					new Color(45, 0, 255),
					new Color(36, 0, 214),
					new Color(27, 0, 172),
					new Color(18, 0, 130),
					new Color(9, 0, 88), 
			};
			// Add color schemes to the map
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
