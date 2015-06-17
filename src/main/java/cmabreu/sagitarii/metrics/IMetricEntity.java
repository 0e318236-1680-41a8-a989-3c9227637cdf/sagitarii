package cmabreu.sagitarii.metrics;

import org.jfree.chart.JFreeChart;

public interface IMetricEntity {
	void calc();
	String getName();
	MetricType getType();
	void setTimeSpent( double time );
	double getTimeSpent();
	JFreeChart getImage();
}
