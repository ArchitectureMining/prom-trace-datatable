package org.processmining.plugins.tracetable.ColumnImpl;

import org.processmining.plugins.tracetable.ColumnType;
import org.processmining.plugins.tracetable.ColumnAbstract.ColumnArrayObject;

public class ColumnLiteral extends ColumnArrayObject<String> {
	public ColumnLiteral(int size) {
		this(new String[size]);
	}
	public ColumnLiteral(String[] values) {
		super(values);
	}
	protected Class<String[]> getValuesClass() {
		return String[].class;
	}
	public ColumnType kind() {
		return ColumnType.Literal;
	}
	protected String parse(String s) {
		return s;
	}
	protected void parseUnchecked(ColumnLiteral c) {
		this.values = c.values;
	}
	protected void parseUnchecked(ColumnCategoricalLiteral c) {
		for (int i = 0; i < this.values.length; i++)
			this.values[i] = c.getMapped(i);
	}
	public ColumnLiteral clone() {
		return new ColumnLiteral(this.values.clone());
	}
}