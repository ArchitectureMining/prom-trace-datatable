package org.processmining.plugins.tracedatatable.ColumnImpl;

import java.text.ParseException;
import java.util.ArrayList;

import org.processmining.plugins.tracedatatable.ColumnType;

public class ColumnCategoricalLiteral extends ColumnCategoricalArray<String> {
	public ColumnCategoricalLiteral(int size) {
		super(size);
	}
	public ColumnCategoricalLiteral(ArrayList<String> values, int[] indices) {
		super(values, indices);
	}
	public ColumnType kind() {
		return ColumnType.CategoricalLiteral;
	}
	protected String parseUnchecked(String c) throws ParseException {
		return c;
	}
	public ColumnCategoricalLiteral clone() {
		return new ColumnCategoricalLiteral((ArrayList<String>)this.values.clone(), this.indices.clone());
	}
}