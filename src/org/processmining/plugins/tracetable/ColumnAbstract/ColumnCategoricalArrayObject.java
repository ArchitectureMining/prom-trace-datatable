package org.processmining.plugins.tracetable.ColumnAbstract;

import java.io.IOException;
import java.util.ArrayList;

import org.processmining.plugins.tracetable.Column;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnCategoricalLiteral;

import com.google.gson.Gson;

public abstract class ColumnCategoricalArrayObject<I extends Comparable<I>> extends Column {
	protected ArrayList<I> values;
	protected int[] indices;

	public ColumnCategoricalArrayObject(int size) {
		this(new ArrayList<I>(), new int[size]);
	}
	public ColumnCategoricalArrayObject(ArrayList<I> values, int[] indices) {
		this.values = values;
		this.indices = indices;
	}

	public int length() {
		return this.indices.length;
	}
	public void read(String s, Gson gson) {
		@SuppressWarnings("unchecked")
		ColumnCategoricalArrayObject<I> o = gson.fromJson(s, this.getClass());
		this.values = o.values;
		this.indices = o.indices;
	}
	public void write(Appendable out, Gson gson) throws IOException {
		gson.toJson(this, this.getClass(), out);
	}

	public int compareValues(int i, int j) {
		return this.getMapped(i).compareTo(this.getMapped(j));
	}
	public void swapValues(int i, int j) {
		int t = this.indices[i];
		this.indices[i] = this.indices[j];
		this.indices[j] = t;
	}
	public void copyValuesTo(Column to, int start_from, int start_to, int count) {
		@SuppressWarnings("unchecked")
		ColumnCategoricalArrayObject<I> c = (ColumnCategoricalArrayObject<I>) to;
		int[] map = new int[this.values.size()];
		for (int i = 0; i < map.length; i++)
			map[i] = c.getOrAddIndex(this.values.get(i));
		for (int i = 0; i < count; i++)
			c.indices[start_to + i] = map[this.indices[start_from + i]];
	}

	@Override
	protected boolean canTypeLiteral() {
		return true;
	}
	@Override
	protected String intoTypeLiteral(int i) {
		return this.getMapped(i).toString();
	}
	@Override
	protected ColumnCategoricalLiteral intoTypeCategoricalLiteral() {
		ColumnCategoricalLiteral c = new ColumnCategoricalLiteral(this.length());
		for (I i : this.values)
			c.values.add(i.toString());
		c.indices = this.indices;
		return c;
	}

	public int get(int i) {
		return this.indices[i];
	}
	public I getMapped(int i) {
		return this.map(this.get(i));
	}
	public I map(int i) {
		return this.values.get(i);
	}
	protected int getOrAddIndex(I v) {
		int index = this.values.indexOf(v);
		if (index < 0) {
			index = this.values.size();
			this.values.add(v);
		}
		return index;
	}
	public void set(int i, I v) {
		this.set(i, this.getOrAddIndex(v));
	}
	public void set(int i, int v) {
		this.indices[i] = v;
	}
}