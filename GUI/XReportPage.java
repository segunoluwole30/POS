import javax.swing.*;
import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class XReportPage extends JPanel {
	private static Connection conn;
	private static POS pos;
	private JPanel centerPanel;
	private ChartPanel generatedChart;

	public XReportPage(Connection conn, POS pos) {
		this.conn = conn;
		this.pos = pos;
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
			plot.setDataset(dataset);

			// Invalidate the chart panel to trigger repaint
			generatedChart.invalidate();
			generatedChart.repaint();
		}
	}

	private void setupUI() {
		// Setting layout for the panel
		setLayout(new BorderLayout());

		// Creating the top navbar
		JPanel navbar = new JPanel();
		navbar.setBackground(Color.RED);
		navbar.setPreferredSize(new Dimension(getWidth(), 50));

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

	// for testing purposes
	public static void main(String[] args) {
		XReportPage p = new XReportPage(conn, pos);
		JFrame f = new JFrame();
		f.setSize(1600, 900);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(p);
		f.setVisible(true);
	}
}
