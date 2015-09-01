package br.cefetrj.sagitarii.metrics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class NodeVMMonitorEntity implements IMetricEntity {
	private String name;
	private double time;
	private List<Double> totalRamLoad;
	private final int HISTOGRAM_PERIOD = 1000;
	private MetricType type;
	
	public void set( double valueRamLoad ) {
		addHistogram( valueRamLoad );
	}
	
	@Override
	public void calc() {
		// do nothing;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public MetricType getType() {
		return type;
	}
	
	@Override
	public void setTimeSpent( double time ) {
		this.time = time;
	}
	
	@Override
	public double getTimeSpent() {
		return time;
	}

	private void addHistogram( double totalRam ) {
		for ( int x = 0; x < HISTOGRAM_PERIOD-1; x++ ) {
			double val = totalRamLoad.get(x+1);
			totalRamLoad.set( x, val );
		}
		totalRamLoad.set(HISTOGRAM_PERIOD-1, totalRam);
	}
	
	
	public NodeVMMonitorEntity( String name, MetricType type ) {
		this.name = name;
		this.type = type;
		totalRamLoad = new ArrayList<Double>();
		for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
			totalRamLoad.add( 0.0 );
		}
	}
	
	@Override
	public JFreeChart getImage() {
        final XYSeries seriesTotalRam = new XYSeries("Total RAM");
        for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
        	seriesTotalRam.add(x, totalRamLoad.get(x) );
		}
        
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesTotalRam);
        
        final JFreeChart retChart = ChartFactory.createTimeSeriesChart(
            "", "", "",  dataset,  true,  false, false
        );
       	
        
        StandardChartTheme chartTheme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();
        
        final Font oldSmallFont = chartTheme.getSmallFont();
        final Font smallFont = new Font("Consolas", oldSmallFont.getStyle(), 8);

        chartTheme.setExtraLargeFont(smallFont);
        chartTheme.setLargeFont(smallFont);
        chartTheme.setRegularFont(smallFont);
        chartTheme.setSmallFont(smallFont);
        chartTheme.apply(retChart);        

        retChart.setAntiAlias(true);
        retChart.setTextAntiAlias(true);
        
        
        XYPlot plot = (XYPlot) retChart.getPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( new Color(  220, 220, 220) ); 
        plot.setRangeGridlinePaint( new Color(  220, 220, 220) );
        
        plot.getRenderer().setSeriesPaint( 0, new Color( 178, 178, 178 ) );
        plot.getRenderer().setSeriesStroke( 0, new BasicStroke( 1 ) );
        
        ValueAxis domain = plot.getDomainAxis();
        domain.setVisible(false);
        
        return retChart;
    	
	}
	
}
