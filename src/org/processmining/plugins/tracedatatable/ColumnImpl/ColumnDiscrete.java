package org.processmining.plugins.tracedatatable.ColumnImpl;

import org.processmining.plugins.tracedatatable.ColumnType;

public class ColumnDiscrete extends ColumnArrayPrimitive<long[], Long> {
	public ColumnDiscrete(int size) {
		this(new long[size]);
	}
	public ColumnDiscrete(long[] values) {
		super(values);
	}
	protected Class<long[]> getValuesClass() {
		return long[].class;
	}
	public ColumnType kind() {
		return ColumnType.Discrete;
	}
	protected Long parse(String s) {
		return Long.parseLong(s);
	}
	public ColumnDiscrete clone() {
		return new ColumnDiscrete(this.values.clone());
	}
	public long get(int i) {
		return this.values[i];
	}
	public void set(int i, long value) {
		this.values[i] = value;
	}
}