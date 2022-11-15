package org.processmining.plugins.tracetable;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.processmining.plugins.tracetable.ColumnImpl.ColumnBoolean;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnCategoricalLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnContinuous;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnDiscrete;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnTimestamp;

import com.google.gson.Gson;

public abstract class Column {

	public abstract ColumnType kind();
	public abstract int length();
	
	public abstract Object getObject(int index);
	public abstract void setObject(int index, Object value);

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

	public HashSet<ColumnType> getIntoTypes() {
		HashSet<ColumnType> s = new HashSet<ColumnType>();
		if (this.canTypeBoolean())
			s.add(ColumnType.Boolean);
		if (this.canTypeContinuous())
			s.add(ColumnType.Continuous);
		if (this.canTypeDiscrete())
			s.add(ColumnType.Discrete);
		if (this.canTypeLiteral()) {
			s.add(ColumnType.Literal);
			s.add(ColumnType.CategoricalLiteral);
		}
		if (this.canTypeTimestamp())
			s.add(ColumnType.Timestamp);
		return s;
	}

	public Column intoType(ColumnType c) throws NumberFormatException, ParseException {
		switch (c) {
		case Boolean:
			return this.intoTypeBoolean();
		case Continuous:
			return this.intoTypeContinuous();
		case Discrete:
			return this.intoTypeDiscrete();
		case Literal:
			return this.intoTypeLiteral();
		case CategoricalLiteral:
			return this.intoTypeCategoricalLiteral();
		case Timestamp:
			return this.intoTypeTimestamp();
		default:
			intoTypeError(c);
			return null;
		}
	}
	protected ColumnBoolean intoTypeBoolean() throws NumberFormatException {
		if (!this.canTypeBoolean()) {
			intoTypeError(ColumnType.Boolean);
			return null;
		}
		ColumnBoolean c = new ColumnBoolean(this.length());
		for (int i = 0; i < c.length(); i++)
			c.set(i, this.intoTypeBoolean(i));
		return c;
	}
	protected ColumnContinuous intoTypeContinuous() throws NumberFormatException {
		if (!this.canTypeContinuous()) {
			intoTypeError(ColumnType.Continuous);
			return null;
		}
		ColumnContinuous c = new ColumnContinuous(this.length());
		for (int i = 0; i < c.length(); i++)
			c.set(i, this.intoTypeContinuous(i));
		return c;
	}
	protected ColumnDiscrete intoTypeDiscrete() throws NumberFormatException {
		if (!this.canTypeDiscrete()) {
			intoTypeError(ColumnType.Discrete);
			return null;
		}
		ColumnDiscrete c = new ColumnDiscrete(this.length());
		for (int i = 0; i < c.length(); i++)
			c.set(i, this.intoTypeDiscrete(i));
		return c;
	}
	protected ColumnLiteral intoTypeLiteral() {
		if (!this.canTypeLiteral()) {
			intoTypeError(ColumnType.Literal);
			return null;
		}
		ColumnLiteral c = new ColumnLiteral(this.length());
		for (int i = 0; i < c.length(); i++)
			c.set(i, this.intoTypeLiteral(i));
		return c;
	}
	protected ColumnCategoricalLiteral intoTypeCategoricalLiteral() {
		if (!this.canTypeLiteral()) {
			intoTypeError(ColumnType.CategoricalLiteral);
			return null;
		}
		ColumnCategoricalLiteral c = new ColumnCategoricalLiteral(this.length());
		for (int i = 0; i < c.length(); i++)
			c.set(i, this.intoTypeLiteral(i));
		return c;
	}
	protected ColumnTimestamp intoTypeTimestamp() throws ParseException {
		if (!this.canTypeTimestamp()) {
			intoTypeError(ColumnType.Timestamp);
			return null;
		}
		ColumnTimestamp c = new ColumnTimestamp(this.length());
		for (int i = 0; i < c.length(); i++)
			c.set(i, this.intoTypeTimestamp(i));
		return c;
	}

	protected boolean intoTypeBoolean(int i) {
		intoTypeError(ColumnType.Boolean);
		return false;
	}
	protected double intoTypeContinuous(int i) throws NumberFormatException {
		intoTypeError(ColumnType.Continuous);
		return 0;
	}
	protected long intoTypeDiscrete(int i) throws NumberFormatException {
		intoTypeError(ColumnType.Discrete);
		return 0;
	}
	protected String intoTypeLiteral(int i) {
		intoTypeError(ColumnType.Literal);
		return null;
	}
	@SuppressWarnings("unused")
	protected Date intoTypeTimestamp(int i) throws ParseException {
		intoTypeError(ColumnType.Timestamp);
		return null;
	}

	protected boolean canTypeBoolean() {
		return false;
	}
	protected boolean canTypeContinuous() {
		return false;
	}
	protected boolean canTypeDiscrete() {
		return false;
	}
	protected boolean canTypeLiteral() {
		return false;
	}
	protected boolean canTypeTimestamp() {
		return false;
	}

	protected void intoTypeError(ColumnType c) {
		throw new IllegalArgumentException(String.format("Cannot parse column with kind %s into kind %s", c, this.kind()));
	}

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
	private static void heapSort(Column[] order, int[] indices) {
		// We rock our own in-place sorting algorithm as that's much more efficient
		// Possible improvement is to add bottom-up heapsort
		for (int start = (indices.length / 2) - 1; start >= 0; start--)
			heapify(order, indices, start, indices.length - 1);

		for (int end = indices.length - 1; end > 0; end--) {
			swapValues(indices, end, 0);
			heapify(order, indices, 0, end - 1);
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
