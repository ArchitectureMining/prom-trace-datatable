package org.processmining.plugins.tracedatatable.ColumnImpl;

import java.io.IOException;
import java.text.ParseException;

import org.processmining.plugins.tracedatatable.Column;

import com.google.gson.Gson;

public abstract class ColumnArrayPrimitive<A, I extends Comparable<I>> extends Column {
	protected A values;

	public ColumnArrayPrimitive(A values) {
		this.values = values;
	}

	protected abstract Class<A> getValuesClass();

	public int length() {
		return java.lang.reflect.Array.getLength(this.values);
	}

	public void write(Appendable out, Gson gson) throws IOException {
		gson.toJson(this.values, this.getValuesClass(), out);
	}
	public void read(String s, Gson gson) {
		this.values = gson.fromJson(s, this.getValuesClass());
	}

	private static Object get(Object a, int i) {
		return java.lang.reflect.Array.get(a, i);
	}
	private void set(Object a, int i, Object v) {
		java.lang.reflect.Array.set(a, i, v);
	}
	public int compareValues(int i, int j) {
		I oi = (I) get(this.values, i);
		I oj = (I) get(this.values, j);
		return oi.compareTo(oj);
	}
	public void swapValues(int i, int j) {
		Object oi = get(this.values, i);
		this.set(this.values, i, this.get(this.values, j));
		this.set(this.values, j, oi);
	}

	protected abstract I parse(String s) throws NumberFormatException, ParseException;
	protected void parseUnchecked(String[] c, A into) throws NumberFormatException, ParseException {
		for (int i = 0; i < c.length; i++)
			java.lang.reflect.Array.set(into, i, parse(c[i]));
	}
	protected void parseUnchecked(ColumnLiteral c) throws ParseException {
		parseUnchecked(c.values, this.values);
	}
	protected void parseUnchecked(ColumnCategoricalLiteral c) throws ParseException {
		A map = (A) java.lang.reflect.Array.newInstance(this.getValuesClass(), c.values.size());
		parseUnchecked((String[])c.values.toArray(), map);
		for (int i = 0; i < c.indices.length; i++)
			set(this.values, i, get(map, c.indices[i]));
	}
	public void copyValuesTo(Column to, int start_from, int start_to, int count) {
		System.arraycopy(this.values, start_from, ((ColumnArrayPrimitive<A, I>)to).values, start_to, count);
	}
}