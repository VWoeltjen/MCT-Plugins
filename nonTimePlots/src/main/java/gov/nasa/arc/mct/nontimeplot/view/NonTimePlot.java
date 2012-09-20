package gov.nasa.arc.mct.nontimeplot.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SpringLayout;

import plotter.xy.LinearXYAxis;
import plotter.xy.ScatterXYPlotLine;
import plotter.xy.SimpleXYDataset;
import plotter.xy.XYDimension;
import plotter.xy.XYPlot;
import plotter.xy.XYPlotContents;

public class NonTimePlot extends XYPlot {
	private static final long serialVersionUID = 7711105789250549245L;
	
	private static final int PLOT_MARGIN = 48;
	
	private XYPlotContents contents = new XYPlotContents();
	private Map<String, SimpleXYDataset> dataSet = new HashMap<String, SimpleXYDataset>();
		
	public NonTimePlot() {
		setXBounds(-1.5, 1.5);
		setYBounds(-1.5, 1.5);
		
		setBackground(Color.DARK_GRAY);
		contents.setBackground(Color.BLACK);
		
		add (getXAxis());
		add (getYAxis());

		add (contents);		
		
		setupLayout();
	}
	
	public void setXBounds(double minimum, double maximum) {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setStart(minimum);
		axis.setEnd(maximum);
		setXAxis(axis);
		colorize(axis);
	}

	public void setYBounds(double minimum, double maximum) {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
		axis.setStart(minimum);
		axis.setEnd(maximum);
		setYAxis(axis);
		colorize(axis);
	}
	
	public void addPoint (String key, double x, double y) {
		if (dataSet.containsKey(key)) dataSet.get(key).add(x, y);		
	}
	
	public void addDataset (String key, Color c) {
		ScatterXYPlotLine plotLine = new ScatterXYPlotLine(getXAxis(), getYAxis());
		SimpleXYDataset data = new SimpleXYDataset(plotLine);
		dataSet.put(key, data);
		contents.add(plotLine);
		plotLine.setForeground(c);
	}
	
	private void colorize(JComponent comp) {
		comp.setBackground(Color.DARK_GRAY);
		comp.setForeground(Color.GRAY);
	}

	private void setupLayout() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		layout.putConstraint(SpringLayout.WEST, getXAxis(), PLOT_MARGIN, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, getXAxis(), 0, SpringLayout.EAST, this);
		
		layout.putConstraint(SpringLayout.SOUTH, getYAxis(), -PLOT_MARGIN, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, getYAxis(), 0, SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.SOUTH, getXAxis(), 0, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.NORTH, getXAxis(), 0, SpringLayout.SOUTH, getYAxis());
		
		layout.putConstraint(SpringLayout.WEST, getYAxis(), 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, getYAxis(), 0, SpringLayout.WEST, getXAxis());
		
		layout.putConstraint(SpringLayout.SOUTH, contents, 0, SpringLayout.NORTH, getXAxis());
		layout.putConstraint(SpringLayout.NORTH, contents, 0, SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.WEST, contents, 0, SpringLayout.EAST, getYAxis());
		layout.putConstraint(SpringLayout.EAST, contents, 0, SpringLayout.EAST, this);		
	}
}
