import javax.swing.*;
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
import java.sql.SQLException;
import java.sql.Statement;
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
	private Map<String, Color[]> colorSchemes;

	public XReportPage(Connection conn, POS pos) {
		this.conn = conn;
		this.pos = pos;
		initializeColorSchemes();
		generateXChart("Entree", 10);
		setupUI();
	}

	private void generateXChart(String category, int hour) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		// Execute query to retrieve data from the database
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT MI.MenuItemID, MI.Name AS MenuItemName, COUNT(TE.MenuItemID) AS QuantitySold " +
								"FROM MenuItems MI " +
								"JOIN TransactionEntry TE ON MI.MenuItemID = TE.MenuItemID " +
								"JOIN Transactions T ON TE.TransactionID = T.TransactionID " +
								"WHERE DATE_PART('hour', T.Date) = " + hour + " " +
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
			// Handle any SQL exceptions
		}

		// Create the pie chart
		if (generatedChart == null) {
			JFreeChart chart = ChartFactory.createPieChart(
					category + " XReport", // chart title
					dataset, // data
					true, // include legend
					true,
					false);

			generatedChart = new ChartPanel(chart);
		} else {
			// Otherwise, update the dataset associated with the existing chart
			JFreeChart chart = generatedChart.getChart();
			chart.setTitle(category + " XReport");
			PiePlot plot = (PiePlot) chart.getPlot();

			Color[] colors = colorSchemes.get("gradientBlue");
        if (colors != null) {
            for (int i = 0; i < dataset.getItemCount(); i++) {
                plot.setSectionPaint(dataset.getKey(i), colors[i % colors.length]);
            }
        }

			plot.setDataset(dataset);
			plot.setSimpleLabels(true);
			plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}"));

			// Invalidate the chart panel to trigger repaint
			generatedChart.invalidate();
			generatedChart.repaint();
		}
	}

	private void setupUI() {
		// Setting layout for the panel
		setLayout(new BorderLayout());

		// Creating the top navbar
		navbar = Utils.createHeaderPanel(pos);
		navbar.setPreferredSize(new Dimension(getWidth(), 50));
		add(navbar, BorderLayout.NORTH);

		// Creating the centered panel
		centerPanel = new JPanel(new BorderLayout());

		// Creating three buttons vertically aligned on the left side
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
		JButton button1 = new JButton("Entree XReport");
		JButton button2 = new JButton("Drink XReport");
		JButton button3 = new JButton("Dessert XReport");
		button1.addActionListener(new ButtonListener("Entree", 8));
		button2.addActionListener(new ButtonListener("Drink", 12));
		button3.addActionListener(new ButtonListener("Dessert", 18));
		buttonPanel.add(button1);
		buttonPanel.add(button2);
		buttonPanel.add(button3);

		centerPanel.add(generatedChart, BorderLayout.CENTER);
		centerPanel.add(buttonPanel, BorderLayout.WEST);

		// Adding navbar and center panel to the main panel
		add(navbar, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
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
			generateXChart(category, hour);
		}
	}

	private void initializeColorSchemes() {
        colorSchemes = new HashMap<>();

        // Define some preset color schemes
        Color[] scheme1 = {Color.RED, Color.GREEN, Color.BLUE};
        Color[] scheme2 = {Color.ORANGE, Color.YELLOW, Color.CYAN};
				Color[] gradientBlueScheme = {new Color(0, 0, 255), new Color(0, 128, 255), new Color(0, 191, 255)};
				Color[] smoothColorScheme = {
					new Color(180, 160, 255), // Light purple
					new Color(153, 128, 255),
					new Color(126, 96, 255),
					new Color(99, 64, 255),
					new Color(72, 32, 255),
					new Color(45, 0, 255),
					new Color(36, 0, 214),
					new Color(27, 0, 172),
					new Color(18, 0, 130),
					new Color(9, 0, 88), // Dark blue
			};
			// Add color schemes to the map
			colorSchemes.put("Scheme 1", scheme1);
			colorSchemes.put("Scheme 2", scheme2);
			colorSchemes.put("gradientBlue", gradientBlueScheme);
			colorSchemes.put("purp", smoothColorScheme);
			// Add more color schemes as needed
  }

	public void refreshHeader() {
		// Remove the old header
		remove(navbar);
		// Directly update the class field `navbar` with a new header panel
		navbar = Utils.createHeaderPanel(pos);
		// Add the updated navbar to the panel
		add(navbar, BorderLayout.NORTH);
		// Revalidate and repaint to ensure UI updates are displayed
		revalidate();
		repaint();
	}

	// for testing purposes
	// public static void main(String[] args) {
	// XReportPage p = new XReportPage(conn, pos);
	// JFrame f = new JFrame();
	// f.setSize(1600, 900);
	// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// f.add(p);
	// f.setVisible(true);
	// }
}
