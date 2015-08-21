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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class NodeLoadMonitorEntity implements IMetricEntity {
	private String name;
	private double time;
	private List<Double> cpuLoad;
	private List<Double> ramLoad;
	private List<Double> tasksLoad;
	private final int HISTOGRAM_PERIOD = 1000;
	private MetricType type;
	private double valueCpu;
	private double valueRam;
	private double valueTasks;
	
	public void set( double valueCpu, double valueRam, double valueTasks ) {
		this.valueCpu = valueCpu;
		this.valueRam = valueRam;
		this.valueTasks = valueTasks;
		addHistogram( valueCpu, valueRam, valueTasks );
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

	private void addHistogram( double valueCpu, double valueRam, double valueTasks ) {
		for ( int x = 0; x < HISTOGRAM_PERIOD-1; x++ ) {
			double val = cpuLoad.get(x+1);
			cpuLoad.set( x, val );
		}
		cpuLoad.set(HISTOGRAM_PERIOD-1, valueCpu);
		
		for ( int x = 0; x < HISTOGRAM_PERIOD-1; x++ ) {
			double val = ramLoad.get(x+1);
			ramLoad.set( x, val );
		}
		ramLoad.set(HISTOGRAM_PERIOD-1, valueRam);

		for ( int x = 0; x < HISTOGRAM_PERIOD-1; x++ ) {
			double val = tasksLoad.get(x+1);
			tasksLoad.set( x, val );
		}
		tasksLoad.set(HISTOGRAM_PERIOD-1, valueTasks);
	}
	
	
	public NodeLoadMonitorEntity( String name, MetricType type ) {
		this.name = name;
		this.type = type;
		cpuLoad = new ArrayList<Double>();
		ramLoad = new ArrayList<Double>();
		tasksLoad = new ArrayList<Double>();
		for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
			cpuLoad.add( 0.0 );
			ramLoad.add( 0.0 );
			tasksLoad.add( 0.0 );
		}
	}
	
	@Override
	public JFreeChart getImage() {
        final XYSeries seriesCpu = new XYSeries("CPU");
        final XYSeries seriesRam = new XYSeries("RAM");
        final XYSeries seriesTasks = new XYSeries("Tasks");
        
        final Minute minute = new Minute();

        for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
        	seriesCpu.add(x, cpuLoad.get(x) );
		}
        for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
        	seriesRam.add(x, ramLoad.get(x) );
		}
        for ( int x = 0; x < HISTOGRAM_PERIOD; x++ ) {
        	seriesTasks.add(x, tasksLoad.get(x) );
		}
        
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesCpu);
        dataset.addSeries(seriesRam);
        dataset.addSeries(seriesTasks);
        
        
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
        
        plot.getRenderer().setSeriesPaint( 0, new Color( 249, 1, 1 ) );
        plot.getRenderer().setSeriesStroke( 0, new BasicStroke( 1 ) );

        plot.getRenderer().setSeriesPaint( 1, new Color( 2, 102, 200 ) );
        plot.getRenderer().setSeriesStroke( 1, new BasicStroke( 1 ) );

        plot.getRenderer().setSeriesPaint( 2, new Color( 0, 147, 59 ) );
        plot.getRenderer().setSeriesStroke( 2, new BasicStroke( 1 ) );
        
        
        ValueAxis domain = plot.getDomainAxis();
        domain.setVisible(false);
        
        return retChart;
    	
	}
	
}
