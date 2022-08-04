package org.processmining.plugins.tracedatatable.ColumnImpl;

import java.io.IOException;
import java.text.ParseException;

import org.processmining.plugins.tracedatatable.Column;

import com.google.gson.Gson;

public abstract class ColumnArrayObject<I extends Object & Comparable<I>> extends Column {
	protected I[] values;

	public ColumnArrayObject(I[] values) {
		this.values = values;
	}

	protected abstract Class<I[]> getValuesClass();

	public int length() {
		return this.values.length;
	}

	public void write(Appendable out, Gson gson) throws IOException {
		gson.toJson(this.values, this.getValuesClass(), out);
	}
	public void read(String s, Gson gson) {
		this.values = gson.fromJson(s, this.getValuesClass());
	}

	public I get(int i) {
		return this.values[i];
	}
	public void set(int i, I v) {
		this.values[i] = v;
	}
	public int compareValues(int i, int j) {
		return this.values[i].compareTo(this.values[j]);
	}
	public void swapValues(int i, int j) {
		I o = this.values[i];
		this.values[i] = this.values[j];
		this.values[j] = o;
	}

	protected abstract I parse(String s) throws NumberFormatException, ParseException;
	protected void parseUnchecked(String[] c, I[] into) throws NumberFormatException, ParseException {
		for (int i = 0; i < c.length; i++)
			java.lang.reflect.Array.set(into, i, parse(c[i]));
	}
	protected void parseUnchecked(ColumnLiteral c) throws ParseException {
		parseUnchecked(c.values, this.values);
	}
	protected void parseUnchecked(ColumnCategoricalLiteral c) throws ParseException {
		I[] map = (I[]) java.lang.reflect.Array.newInstance(this.getValuesClass(), c.values.size());
		parseUnchecked((String[])c.values.toArray(), map);
		for (int i = 0; i < c.indices.length; i++)
			this.values[i] = map[c.indices[i]];
	}
	public void copyValuesTo(Column to, int start_from, int start_to, int count) {
		System.arraycopy(this.values, start_from, ((ColumnArrayObject<I>)to).values, start_to, count);
	}
}