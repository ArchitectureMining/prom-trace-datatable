package org.processmining.plugins.tracetable.ColumnImpl;

import org.processmining.plugins.tracetable.Column;
import org.processmining.plugins.tracetable.ColumnType;
import org.processmining.plugins.tracetable.ColumnAbstract.ColumnArrayPrimitive;

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
	public Column clone() {
		return new ColumnBoolean(this.values.clone());
	}

	public boolean get(int i) {
		return this.values[i];
	}
	public void set(int i, boolean value) {
		this.values[i] = value;
	}

	@Override
	protected boolean canTypeBoolean() {
		return true;
	}
	@Override
	protected ColumnBoolean intoTypeBoolean() {
		return this;
	}
	@Override
	protected boolean intoTypeBoolean(int i) {
		return this.values[i];
	}

	@Override
	protected boolean canTypeContinuous() {
		return true;
	}
	@Override
	protected double intoTypeContinuous(int i) {
		return this.values[i] ? 1 : 0;
	}

	@Override
	protected boolean canTypeDiscrete() {
		return true;
	}
	@Override
	protected long intoTypeDiscrete(int i) {
		return this.values[i] ? 1 : 0;
	}
}