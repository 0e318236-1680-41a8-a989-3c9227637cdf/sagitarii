package br.cefetrj.sagitarii.metrics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class MetricHitEntity implements IMetricEntity {
	private double totalHits;
	private double hitsPerSecond;
	private String name;
	private double time;
	private List<Double> histogram;
	private final int HISTOGRAM_PERIOD = 60;
	private MetricType type;
	
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
	
	@Override
	public void calc() {
		calcHitsPerSecond();
	}
	
	private void calcHitsPerSecond() {
		hitsPerSecond = totalHits;
		totalHits = 0;
		addHistogram( hitsPerSecond );
	}
	
	public double getHitsPerSecond() {
		return hitsPerSecond;
	}
	
	public void hit() {
		totalHits++;
	}
	
	public MetricHitEntity( String name, MetricType type ) {
		this.name = name;
		this.type = type;
		totalHits = 0;
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
            name, "", "",  dataset,  false,  false, false
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

	@Override
	public void saveImage(String path) throws Exception {
		/*
		JFreeChart chart = getImage();
		
		BufferedImage objBufferedImage = chart.createBufferedImage(210, 110);
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		        try {
		            ImageIO.write(objBufferedImage, "png", bas);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }

		byte[] byteArray = bas.toByteArray();		
		bas.close();
		
		InputStream in = new ByteArrayInputStream( byteArray );
		BufferedImage image = ImageIO.read(in);
		File outputfile = new File( path + name + ".png" );
		ImageIO.write(image, "png", outputfile);		
		
		in.close();
		
		*/
		
		/*
		
		BufferedImage image = chart.createBufferedImage( 210, 110);
        File outputfile = new File( path + name + ".png");
        ImageIO.write( image, "png", outputfile );
        */		
	}
	
}
