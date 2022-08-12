package org.processmining.plugins.tracetable.ColumnImpl;

import java.text.ParseException;

import org.processmining.plugins.tracetable.ColumnType;
import org.processmining.plugins.tracetable.ColumnAbstract.ColumnArrayPrimitive;

public class ColumnContinuous extends ColumnArrayPrimitive<double[], Double> {
	public ColumnContinuous(int size) {
		this(new double[size]);
	}
	public ColumnContinuous(double[] values) {
		super(values);
	}
	protected Class<double[]> getValuesClass() {
		return double[].class;
	}
	public ColumnType kind() {
		return ColumnType.Continuous;
	}
	protected Double parse(String s) throws NumberFormatException, ParseException {
		return Double.parseDouble(s);
	}
	public ColumnContinuous clone() {
		return new ColumnContinuous(this.values.clone());
	}
	public double get(int i) {
		return this.values[i];
	}
	public void set(int i, double value) {
		this.values[i] = value;
	}
}