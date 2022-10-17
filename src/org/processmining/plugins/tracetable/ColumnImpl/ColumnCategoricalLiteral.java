package org.processmining.plugins.tracetable.ColumnImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.processmining.plugins.tracetable.ColumnType;
import org.processmining.plugins.tracetable.ColumnAbstract.ColumnCategoricalArrayObject;

public class ColumnCategoricalLiteral extends ColumnCategoricalArrayObject<String> {
	public ColumnCategoricalLiteral(int size) {
		super(size);
	}
	public ColumnCategoricalLiteral(String[] values) {
		super(values.length);
		for (int i = 0; i < values.length; i++)
			this.set(i, values[i]);
	}
	public ColumnCategoricalLiteral(ArrayList<String> values, int[] indices) {
		super(values, indices);
	}
	public ColumnType kind() {
		return ColumnType.CategoricalLiteral;
	}
	@SuppressWarnings("unchecked")
	public ColumnCategoricalLiteral clone() {
		return new ColumnCategoricalLiteral((ArrayList<String>)this.values.clone(), this.indices.clone());
	}

	protected ColumnCategoricalLiteral intoTypeCategoricalLiteral() {
		return this;
	}
	protected boolean canTypeLiteral() {
		return true;
	}
	protected String intoTypeLiteral(int i) {
		return this.getMapped(i);
	}
	protected boolean canTypeBoolean() {
		return true;
	}
	protected boolean intoTypeBoolean(int i) {
		return ColumnLiteral.parseBoolean(this.values.get(i));
	}
	protected boolean canTypeContinuous() {
		return ColumnLiteral.canParseContinuous(this.values.get(0));
	}
	protected double intoTypeContinuous(int i) {
		return ColumnLiteral.parseContinuous(this.getMapped(i));
	}
	protected boolean canTypeDiscrete() {
		return ColumnLiteral.canParseDiscrete(this.values.get(0));
	}
	protected long intoTypeDiscrete(int i) {
		return ColumnLiteral.parseDiscrete(this.getMapped(i));
	}
	protected boolean canTypeTimestamp() {
		return ColumnLiteral.canParseTimestamp(this.values.get(0));
	}
	protected Date intoTypeTimestamp(int i) throws ParseException {
		return ColumnLiteral.parseTimestamp(this.getMapped(i));
	}
}