package org.processmining.plugins.tracetable.ColumnImpl;

import java.time.Instant;
import java.util.Date;

import org.processmining.plugins.tracetable.ColumnType;
import org.processmining.plugins.tracetable.ColumnAbstract.ColumnArrayPrimitive;

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
	public ColumnDiscrete clone() {
		return new ColumnDiscrete(this.values.clone());
	}
	public long get(int i) {
		return this.values[i];
	}
	public void set(int i, long value) {
		this.values[i] = value;
	}

	protected boolean canTypeBoolean() {
		return true;
	}
	protected boolean intoTypeBoolean(int i) {
		return this.values[i] != 0;
	}
	protected boolean canTypeContinuous() {
		return true;
	}
	protected double intoTypeContinuous(int i) {
		return this.values[i];
	}
	protected boolean canTypeDiscrete() {
		return true;
	}
	protected ColumnDiscrete intoTypeDiscrete() {
		return this;
	}
	protected long intoTypeDiscrete(int i) {
		return this.values[i];
	}
	protected boolean canTypeTimestamp() {
		return true;
	}
	protected Date intoTypeTimestamp(int i) {
		return Date.from(Instant.ofEpochMilli(this.values[i]));
	}
}