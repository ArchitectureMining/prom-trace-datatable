package org.processmining.plugins.tracedatatable;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnCategoricalLiteral;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnLiteral;

import com.google.gson.Gson;

public abstract class Column {

	public abstract ColumnType kind();
	public abstract int length();

	public void write(Appendable out) throws IOException {
		this.write(out, new Gson());
	}
	public void read(String s) {
		this.read(s, new Gson());
	}
	public abstract void write(Appendable out, Gson gson) throws IOException;
	public abstract void read(String s, Gson gson);

	public abstract int compareValues(int i, int j);
	public abstract void swapValues(int i, int j);
	public abstract void copyValuesTo(Column to, int start_from, int start_to, int count);

	public abstract Column clone();

	public void parse(ColumnLiteral c) throws NumberFormatException, ParseException {
		if (this.length() != c.length())
			throw new IllegalArgumentException("To-be parsed column needs to be of the same length as the column being parsed into");
		this.parseUnchecked(c);
	}
	public void parse(ColumnCategoricalLiteral c) throws NumberFormatException, ParseException {
		if (this.length() != c.length())
			throw new IllegalArgumentException("To-be parsed column needs to be of the same length as the column being parsed into");
		this.parseUnchecked(c);
	}
	protected abstract void parseUnchecked(ColumnLiteral c) throws ParseException, NumberFormatException;
	protected abstract void parseUnchecked(ColumnCategoricalLiteral c) throws ParseException, NumberFormatException;


	public static void sort(Map<String, Column> columns, String[] order) {
		Column[] column_order = new Column[order.length];
		for (int i = 0; i < order.length; i++)
			column_order[i] = columns.get(order[i]);
		sort(columns.values(), column_order);
	}
	public static void sort(Collection<Column> columns, Column[] order) {
		if (columns == null || columns.size() == 0)
			return;
		if (order == null || order.length == 0)
			return;
		// Create a list of indices
		int[] indices = new int[order[0].length()];
		for (int i = 0; i < indices.length; i++)
			indices[i] = i;

		// Sort the indices based on by
		heapSort(order, indices);

		// Transform indices into a mapping with only n swaps from the indices list
		// it's better to create this first in order to reduce the number of swaps required later
		for (int i = 0; i < indices.length; i++) {
			int k = indices[i];
			while(i != k && k < i)
				k = indices[k];
			indices[i] = k;
		}

		// Sort the actual columns
		for (Column c : columns) {
			for (int i = 0; i < indices.length; i++) {
				c.swapValues(i, indices[i]);
			}
		}
	}
	private static void heapSort(Column[] columns, int[] indices) {
		// We rock our own in-place sorting algorithm as that's much more efficient
		// Possible improvement is to add bottom-up heapsort
		for (int start = (indices.length / 2) - 1; start >= 0; start--)
			heapify(columns, indices, start, indices.length - 1);

		for (int end = indices.length - 1; end > 0; end--) {
			swapValues(indices, end, 0);
			heapify(columns, indices, 0, end - 1);
		}
	}
	private static void heapify(Column[] columns, int[] indices, int start, int end) {
		int root = start;
		int child = root * 2 + 1;

		while (child <= end) {
			int swap = root;

			if (compareValues(columns, indices[swap], indices[child]) < 0)
				swap = child;

			if ((child + 1 <= end) && compareValues(columns, indices[swap], indices[child + 1]) < 0)
				swap = child + 1;

			if (swap == root)
				return;

			swapValues(indices, root, swap);
			root = swap;
			child = root * 2 + 1;
		}
	}
	private static void swapValues(int[] indices, int i, int j) {
		int t = indices[i];
		indices[i] = indices[j];
		indices[j] = t;
	}
	private static int compareValues(Column[] columns, int a, int b) {
		int value = 0;
		for (int c = 0; c < columns.length; c++) {
			value = columns[c].compareValues(a, b);
			if (value != 0)
				break;
		}
		return value;
	}
}
