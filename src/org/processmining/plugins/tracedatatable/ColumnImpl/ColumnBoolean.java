package org.processmining.plugins.tracedatatable.ColumnImpl;

import java.text.ParseException;

import org.processmining.plugins.tracedatatable.Column;
import org.processmining.plugins.tracedatatable.ColumnType;
import org.processmining.plugins.tracedatatable.ColumnAbstract.ColumnArrayPrimitive;

public class ColumnBoolean extends ColumnArrayPrimitive<boolean[], Boolean> {
	public ColumnBoolean(int size) {
		this(new boolean[size]);
	}
	public ColumnBoolean(boolean[] values) {
		super(values);
	}
	protected Class<boolean[]> getValuesClass() {
		return boolean[].class;
	}
	public ColumnType kind() {
		return ColumnType.Boolean;
	}
	protected Boolean parse(String s) throws NumberFormatException, ParseException {
		return Boolean.parseBoolean(s);
	}
	public Column clone() {
		return new ColumnBoolean(this.values.clone());
	}

	public boolean get(int i) {
		return this.values[i];
	}
	public void set(int i, boolean value) {
		this.values[i] = value;
	}
}