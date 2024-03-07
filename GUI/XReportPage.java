import javax.swing.*;
import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class displays the X Report page, which holds a collection of
 * different reports for a user-defined hour within a specified date. These
 * different reports
 * include pie charts, sales, product usage, excess, and best product combos.
 * 
 * @author David Tenase
 */
public class XReportPage extends JPanel {
	private Connection conn;
	private POS pos;

	private JPanel centerPanel;
	private JPanel navbar;
	private JPanel chartPanel;
	private JTable generatedTable;
	private ChartPanel generatedChart;

	private Map<String, Color[]> colorSchemes;

	private String year;
	private String month;
	private String day;
	private int hour;

	private Timestamp startTimestamp;
	private Timestamp endTimestamp;

	private boolean goBack = false;

	/**
	 * Constructor for XReportPage class.
	 * 
	 * @param conn The connection to the database.
	 * @param pos  The POS system instance.
	 */
	public XReportPage(Connection conn, POS pos) {
		this.conn = conn;
		this.pos = pos;
		initializeColorSchemes();
		initializeDate();
		if (goBack) {
			pos.showManagerHomePage();
		} else {
			setupUI();
		}
	}

	/**
	 * Generates a pie chart based on the given category.
	 * 
	 * @param category , The category of items for which the report is generated.
	 */
	private void generateXChart(String category) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT MI.MenuItemID, MI.Name AS MenuItemName, COUNT(TE.MenuItemID) AS QuantitySold " +
								"FROM MenuItems MI " +
								"JOIN TransactionEntry TE ON MI.MenuItemID = TE.MenuItemID " +
								"JOIN Transactions T ON TE.TransactionID = T.TransactionID " +
								"WHERE DATE_PART('year', T.Date) = " + year + " " +
								"AND DATE_PART('month', T.Date) = " + month + " " +
								"AND DATE_PART('day', T.Date) = " + day + " " +
								"AND DATE_PART('hour', T.Date) = " + hour + " " +
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

		if (generatedChart == null) {
			JFreeChart chart = ChartFactory.createPieChart(
					category + " XReport",
					dataset,
					true,
					true,
					false);

			generatedChart = new ChartPanel(chart);
		} else {
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

	/**
	 * Generates a sales report based on the given criteria.
	 * 
	 * @param none
	 */
	private void generateSalesReport() {
		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(
					"SELECT MI.MenuItemID, MI.Name AS MenuItemName, COUNT(TE.MenuItemID) AS QuantitySold " +
							"FROM MenuItems MI " +
							"JOIN TransactionEntry TE ON MI.MenuItemID = TE.MenuItemID " +
							"JOIN Transactions T ON TE.TransactionID = T.TransactionID " +
							"WHERE DATE_PART('year', T.Date) = " + year + " " +
							"AND DATE_PART('month', T.Date) = " + month + " " +
							"AND DATE_PART('day', T.Date) = " + day + " " +
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
			salesReportScrollPane
					.setPreferredSize(new Dimension(salesReportScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

			chartPanel.removeAll();
			chartPanel.add(salesReportScrollPane, BorderLayout.CENTER);

			revalidate();
			repaint();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a product usage report based on the given criteria.
	 * 
	 * @param none
	 */
	private void generateProductUsageReport() {
		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(
					"SELECT ii.Name AS InventoryItem, SUM(mii.Quantity) AS UsedQuantity " +
							"FROM Transactions t " +
							"JOIN TransactionEntry te ON t.TransactionID = te.TransactionID " +
							"JOIN MenuItems mi ON te.MenuItemID = mi.MenuItemID " +
							"JOIN MenuItemIngredients mii ON mi.MenuItemID = mii.MenuItemID " +
							"JOIN IngredientsInventory ii ON mii.IngredientID = ii.IngredientID " +
							"WHERE DATE_PART('year', T.Date) = " + year + " " +
							"AND DATE_PART('month', T.Date) = " + month + " " +
							"AND DATE_PART('day', T.Date) = " + day + " " +
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
			salesReportScrollPane
					.setPreferredSize(new Dimension(salesReportScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

			chartPanel.removeAll();
			chartPanel.add(salesReportScrollPane, BorderLayout.CENTER);

			revalidate();
			repaint();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a report on best product combinations based on the given criteria.
	 * 
	 * @param none
	 */
	private void generateBestPairsReport() {

		try {
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(
					"SELECT mi1.Name AS MenuItem1, mi2.Name AS MenuItem2, COUNT(*) AS Frequency " +
							"FROM TransactionEntry te1 " +
							"JOIN TransactionEntry te2 ON te1.TransactionID = te2.TransactionID AND te1.MenuItemID < te2.MenuItemID "
							+
							"JOIN MenuItems mi1 ON te1.MenuItemID = mi1.MenuItemID " +
							"JOIN MenuItems mi2 ON te2.MenuItemID = mi2.MenuItemID " +
							"JOIN Transactions t ON te1.TransactionID = t.TransactionID " +
							"WHERE DATE_PART('year', t.Date) = " + year + " " +
							"AND DATE_PART('month', t.Date) = " + month + " " +
							"AND DATE_PART('day', t.Date) = " + day + " " +
							"AND DATE_PART('hour', t.Date) = " + hour + " " +
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
			salesReportScrollPane
					.setPreferredSize(new Dimension(salesReportScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

			chartPanel.removeAll();
			chartPanel.add(salesReportScrollPane, BorderLayout.CENTER);

			revalidate();
			repaint();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a report on excess inventory based on the given criteria.
	 * 
	 * @param none
	 */
	private void generateExcessReport() {
		try {
			String query = "" +
					"WITH StockChanges AS (" +
					"    SELECT" +
					"        ii.IngredientID," +
					"        ii.Name," +
					"        ii.MaxStock," +
					"        ii.Stock AS StartingStock," +
					"        COALESCE(SUM(mii.Quantity), 0) AS SoldQuantity," +
					"        ii.MaxStock - COALESCE(SUM(mii.Quantity), 0) AS EndingStock" +
					"    FROM" +
					"        IngredientsInventory ii" +
					"    LEFT JOIN" +
					"        MenuItemIngredients mii ON ii.IngredientID = mii.IngredientID" +
					"    LEFT JOIN" +
					"        TransactionEntry te ON mii.MenuItemID = te.MenuItemID" +
					"        AND te.TransactionID IN (" +
					"            SELECT TransactionID" +
					"            FROM Transactions" +
					"            WHERE Date BETWEEN ? AND ?" +
					"        )" +
					"    GROUP BY" +
					"        ii.IngredientID, ii.Name, ii.MaxStock, ii.Stock" +
					")," +
					"InventoryChanges AS (" +
					"    SELECT" +
					"        *," +
					"        (SoldQuantity) / MaxStock AS StockChangePercentage" +
					"    FROM" +
					"        StockChanges" +
					")" +
					"SELECT" +
					"    *" +
					"FROM" +
					"    InventoryChanges" +
					" WHERE" +
					"    StockChangePercentage < 0.1;";

			PreparedStatement statement = conn.prepareStatement(query);
			statement.setTimestamp(1, startTimestamp);
			statement.setTimestamp(2, endTimestamp);
			ResultSet result = statement.executeQuery();

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
			salesReportScrollPane
					.setPreferredSize(new Dimension(salesReportScrollPane.getPreferredSize().width, Common.HEIGHT / 4));

			chartPanel.removeAll();
			chartPanel.add(salesReportScrollPane, BorderLayout.CENTER);

			revalidate();
			repaint();

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle SQL exception here, such as displaying an error message
		}
	}

	/**
	 * Initializes the date and hour for generating reports.
	 * 
	 * @param none
	 */
	private void initializeDate() {
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

		if (result == JOptionPane.OK_OPTION) {
			String dateInput = dateField.getText();
			String hourInput = hourField.getText();

			String startTime = hourInput + ":00:00";
			String endTime = hourInput + ":59:59";

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime startDateTime = LocalDateTime.parse(dateInput + " " + startTime, formatter);
			LocalDateTime endDateTime = LocalDateTime.parse(dateInput + " " + endTime, formatter);
			startTimestamp = Timestamp.valueOf(startDateTime);
			endTimestamp = Timestamp.valueOf(endDateTime);

			if (!dateInput.isEmpty() && !hourInput.isEmpty()) {
				try {
					hour = Integer.parseInt(hourInput);
					String[] parts = dateInput.split("-");

					year = parts[0];
					month = parts[1];
					day = parts[2];

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this,
							"Invalid hour input. Please enter a valid integer between 8 and 20.", "Error",
							JOptionPane.ERROR_MESSAGE);
					goBack = true;
				}
			} else {
				JOptionPane.showMessageDialog(this, "Both date and hour inputs are required.", "Error",
						JOptionPane.ERROR_MESSAGE);
				goBack = true;
			}
		} else if (result == JOptionPane.CANCEL_OPTION) {
			goBack = true;
		} else {
			return;
		}
	}

	/**
	 * Sets up the UI components for the report page.
	 * 
	 * @param none
	 */
	private void setupUI() {
		// Boilerplate code to setup layout
		setLayout(new BorderLayout());
		navbar = Utils.createHeaderPanel(pos);
		navbar.setPreferredSize(new Dimension(getWidth(), 50));
		add(navbar, BorderLayout.NORTH);
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 130, 100, 50));
		centerPanel.setBackground(Common.DARKCYAN);

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

		centerPanel.add(chartPanel, BorderLayout.CENTER);
		centerPanel.add(buttonPanel, BorderLayout.WEST);

		// Adding navbar and center panel to the main panel
		add(navbar, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
	}

	/**
	 * ActionListener implementation for handling button clicks.
	 * 
	 * @param none
	 */
	private class ButtonListener implements ActionListener {
		private String category;
		private String action;

		/**
		 * Constructor for ButtonListener.
		 * 
		 * @param category The category associated with the button
		 * @param action   The action associated with the button
		 */
		public ButtonListener(String category, String action) {
			this.category = category;
			this.action = action;
		}

		/**
		 * Action performed when a button is clicked.
		 * 
		 * @param e The ActionEvent object
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (action == "pie_chart") {
				generateXChart(category);
			} else if (action == "sales_report") {
				// Given a time window, display the sales by item from the order history.
				generateSalesReport();
			} else if (action == "product_usage") {
				// Given a time window, display a chart (table, graph, diagram) that depicts the
				// amount of inventory used during that time period.
				generateProductUsageReport();
			} else if (action == "excess_report") {
				// Given a timestamp, display the list of items that only sold less than 10% of
				// their inventory between the timestamp and the current time, assuming no
				// restocks have happened during the window.
				generateExcessReport();
			} else if (action == "sells_together") {
				// Given a time window, display a list of pairs of menu items that sell together
				// often, popular or not, sorted by most frequent.
				generateBestPairsReport();
			}
		}
	}

	/**
	 * Initializes color schemes for the charts.
	 * 
	 * @param none
	 */
	private void initializeColorSchemes() {
		colorSchemes = new HashMap<>();

		// Define some preset color schemes
		Color[] scheme1 = { Color.RED, Color.GREEN, Color.BLUE };
		Color[] scheme2 = { Color.ORANGE, Color.YELLOW, Color.CYAN };
		Color[] gradientBlueScheme = { new Color(0, 0, 255), new Color(0, 128, 255), new Color(0, 191, 255) };
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

	/**
	 * Refreshes the header panel of the report page.
	 * 
	 * @param none
	 */
	public void refreshHeader() {
		remove(navbar);
		navbar = Utils.createHeaderPanel(pos);
		add(navbar, BorderLayout.NORTH);
		revalidate();
		repaint();
	}
}
