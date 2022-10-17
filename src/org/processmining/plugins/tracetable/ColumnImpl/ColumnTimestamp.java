package org.processmining.plugins.tracetable.ColumnImpl;

import java.util.Date;

import org.processmining.plugins.tracetable.ColumnType;
import org.processmining.plugins.tracetable.ColumnAbstract.ColumnArrayObject;

public class ColumnTimestamp extends ColumnArrayObject<Date> {
	public ColumnTimestamp(int size) {
		this(new Date[size]);
	}
	public ColumnTimestamp(Date[] values) {
		super(values);
	}
	protected Class<Date[]> getValuesClass() {
		return Date[].class;
	}
	public ColumnType kind() {
		return ColumnType.Timestamp;
	}
	public ColumnTimestamp clone() {
		return new ColumnTimestamp(this.values.clone());
	}

	protected boolean canTypeDiscrete() {
		return true;
	}
	protected long intoTypeDiscrete(int i) {
		return this.values[i].getTime();
	}
	protected boolean canTypeTimestamp() {
		return true;
	}
	protected ColumnTimestamp intoTypeTimestamp() {
		return this;
	}
	protected Date intoTypeTimestamp(int i) {
		return this.values[i];
	}
}