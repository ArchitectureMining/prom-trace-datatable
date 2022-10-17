package org.processmining.plugins.tracetable.ColumnImpl;

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
	public ColumnContinuous clone() {
		return new ColumnContinuous(this.values.clone());
	}
	public double get(int i) {
		return this.values[i];
	}
	public void set(int i, double value) {
		this.values[i] = value;
	}

	protected boolean canTypeBoolean() {
		return true;
	}
	protected boolean intoTypeBoolean(int i) {
		return this.values[i] != 0;
	}
	protected boolean canTypeContinuous(int i) {
		return true;
	}
	protected ColumnContinuous intoTypeContinuous() {
		return this;
	}
	protected double intoTypeContinuous(int i) {
		return this.values[i];
	}
	protected boolean canTypeDiscrete() {
		return true;
	}
	protected long intoTypeDiscrete(int i) {
		return (long) this.values[i];
	}
}