package org.processmining.plugins.tracetable.ColumnAbstract;

public abstract class ColumnArrayObject<I extends Object & Comparable<I>> extends ColumnArrayPrimitive<I[], I> {
	public ColumnArrayObject(I[] values) {
		super(values);
	}

	public I get(int i) {
		return this.values[i];
	}
	public void set(int i, I v) {
		this.values[i] = v;
	}

	@Override
	protected boolean canTypeBoolean() {
		return true;
	}
	@Override
	protected boolean intoTypeBoolean(int i) {
		return this.values[i] != null;
	}
}
