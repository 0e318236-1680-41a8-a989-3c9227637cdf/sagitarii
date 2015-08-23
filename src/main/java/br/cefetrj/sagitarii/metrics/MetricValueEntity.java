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
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class MetricValueEntity implements IMetricEntity {
	private String name;
	private double time;
	private List<Double> histogram;
	private final int HISTOGRAM_PERIOD = 60;
	private MetricType type;
	private double value;
	
	public void set( double value ) {
		this.value = value;
		addHistogram( value );
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

	private void addHistogram( double value ) {
		for ( int x = 0; x < HISTOGRAM_PERIOD-1; x++ ) {
			double val = histogram.get(x+1);
			histogram.set( x, val );
		}
		histogram.set(HISTOGRAM_PERIOD-1, value);
	}
	
	
	public MetricValueEntity( String name, MetricType type ) {
		this.name = name;
		this.type = type;
		histogram = new ArrayList<Double>();
		for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
			histogram.add( 0.0 );
		}
	}
	
	@Override
	public JFreeChart getImage() {
        final TimeSeries series = new TimeSeries("");
        final Minute minute = new Minute();

        for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
	        series.add(new Second(x+1, minute), histogram.get(x) );
		}
        
        final TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        final JFreeChart retChart = ChartFactory.createTimeSeriesChart(
            "", "", "",  dataset,  false,  false, false
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
        
        plot.getRenderer().setSeriesPaint( 0, new Color( 200, 2, 3) );
        plot.getRenderer().setSeriesStroke( 0, new BasicStroke( 1 ) );
        
        ValueAxis domain = plot.getDomainAxis();
        domain.setVisible(false);
        
        return retChart;
    	
	}
	
}
