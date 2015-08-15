package br.cefetrj.sagitarii.metrics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import br.cefetrj.sagitarii.core.types.ExperimentStatus;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.Workflow;

public class ExperimentTimes {
	
	@SuppressWarnings("serial")
	class CustomRenderer extends BarRenderer { 
		public Paint getItemPaint(final int row, final int column) { 
			return new Color(2, 102, 200); 
		} 
	}	
	
	
	public JFreeChart getImage( Workflow workflow ) {

		DefaultCategoryDataset ds = new DefaultCategoryDataset();

        for ( Experiment ex : workflow.getExperiments() ) {
        	if ( ex.getStatus() != ExperimentStatus.STOPPED ) {
        		ds.addValue( ex.getElapsedMillis(), ex.getElapsedTime(), ex.getTagExec() );
        	}
        }
        
        JFreeChart retChart = ChartFactory.createBarChart(
                "",         				// chart title
                "Experiments",              // domain axis label
                "Time",                  	// range axis label
                ds,                   		// data
                PlotOrientation.VERTICAL,
                false,                     	// include legend
                false,                    	// tooltips?
                false                     	// URLs?
        );      
        
        StandardChartTheme chartTheme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();

        Font oldExtraLargeFont = chartTheme.getExtraLargeFont();
        Font oldLargeFont = chartTheme.getLargeFont();
        Font oldRegularFont = chartTheme.getRegularFont();
        Font oldSmallFont = chartTheme.getSmallFont();

        Font extraLargeFont = new Font("Consolas", oldExtraLargeFont.getStyle(), oldExtraLargeFont.getSize());
        Font largeFont = new Font("Consolas", oldLargeFont.getStyle(), oldLargeFont.getSize());
        Font regularFont = new Font("Consolas", oldRegularFont.getStyle(), oldRegularFont.getSize());
        Font smallFont = new Font("Consolas", oldSmallFont.getStyle(), oldSmallFont.getSize());

        chartTheme.setExtraLargeFont(extraLargeFont);
        chartTheme.setLargeFont(largeFont);
        chartTheme.setRegularFont(regularFont);
        chartTheme.setSmallFont(smallFont);

        chartTheme.apply(retChart);        

        retChart.setAntiAlias(true);
        retChart.setTextAntiAlias(true);
        
        CategoryPlot plot = retChart.getCategoryPlot();
        
        BarRenderer renderer =  new CustomRenderer(); //(BarRenderer) plot.getRenderer();
        plot.setRenderer( renderer );
        
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.5);
        renderer.setGradientPaintTransformer(null);
        renderer.setBarPainter(new StandardBarPainter());
        
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2); 
        CategoryItemLabelGenerator generator =
            new StandardCategoryItemLabelGenerator("{0}", format, format);
        renderer.setBaseItemLabelGenerator(generator);
        renderer.setBaseItemLabelsVisible(true);  
        renderer.setMaximumBarWidth(.10);
        
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( new Color(  220, 220, 220) ); 
        plot.setRangeGridlinePaint( new Color(  220, 220, 220) );

        
        ValueAxis range = plot.getRangeAxis();
        range.setVisible(false);
        
        
        return retChart;
    	
	}
	
}
