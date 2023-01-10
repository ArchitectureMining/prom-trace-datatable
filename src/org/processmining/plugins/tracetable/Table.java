package org.processmining.plugins.tracetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.processmining.plugins.tracetable.ColumnImpl.ColumnBoolean;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnCategoricalLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnContinuous;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnDiscrete;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnTimestamp;

import com.google.gson.Gson;

public class Table {
	private final int Length;
	private final HashMap<String, Column> Columns;

	public Table(int size) {
		this.Length = size;
		this.Columns = new HashMap<String, Column>();
	}
	public Table(int size, Map<String, ColumnType> columns) {
		this(size);
		for (Entry<String, ColumnType> e : columns.entrySet()) {
			this.add(e.getKey(), e.getValue());
		}
	}

	// Partial Map<String, ColumnType> interface implementation
	public int length() {
		return this.Length;
	}
	public int size() {
		return this.Columns.size();
	}
	public boolean isEmpty() {
		return this.Columns.isEmpty();
	}
	public boolean containsKey(String key) {
		return this.Columns.containsKey(key);
	}
	public Column get(String key) {
		return this.Columns.get(key);
	}
	public ColumnBoolean getBoolean(String key) {
		return (ColumnBoolean)this.get(key);
	}
	public ColumnContinuous getContinuous(String key) {
		return (ColumnContinuous)this.get(key);
	}
	public ColumnDiscrete getDiscrete(String key) {
		return (ColumnDiscrete)this.get(key);
	}
	public ColumnLiteral getLiteral(String key) {
		return (ColumnLiteral)this.get(key);
	}
	public ColumnCategoricalLiteral getCategoricalLiteral(String key) {
		return (ColumnCategoricalLiteral)this.get(key);
	}
	public ColumnTimestamp getTimestamp(String key) {
		return (ColumnTimestamp)this.get(key);
	}
	public HashMap<String, ColumnType> typeMap() {
		HashMap<String, ColumnType> map = new HashMap<String, ColumnType>(this.Columns.size());
		for (Entry<String, Column> e : this.Columns.entrySet())
			map.put(e.getKey(), e.getValue().kind());
		return map;
	}
	public Column add(String key, ColumnType value) {
		Column column = value.create(this.Length);
		this.Columns.put(key, column);
		return column;
	}
	public Column put(String key, Column value) {
		if (value.length() != this.Length)
			throw new IllegalArgumentException("Column needs to have the same size as table.");
		return this.Columns.put(key, value);
	}
	public Column remove(String key) {
		return this.Columns.remove(key);
	}
	public boolean removeAll(Collection<String> keys) {
		boolean changed = false;
		for (String key : keys) {
			changed |= this.remove(key) != null;
		}
		return changed;
	}
	public void clear() {
		this.Columns.clear();
	}
	public Set<String> keySet() {
		return this.Columns.keySet();
	}
	public Collection<Column> values() {
		return this.Columns.values();
	}
	public Set<Entry<String, Column>> entrySet() {
		return this.Columns.entrySet();
	}

	// Sorting
	public void sort(String[] order) {
		Column.sort(this.Columns, order);
	}
	// Input / Output
	private static class StorageInfo {
		int length;
		Map<String, ColumnType> columns;
		public StorageInfo(int l, Map<String, ColumnType> c) {
			this.length = l;
			this.columns = c;
		}
	}
	public void write(Appendable out) throws IOException {
		this.write(out, new Gson());
	}
	public void write(Appendable out, Gson gson) throws IOException {
		StorageInfo info = new StorageInfo(this.Length, new HashMap<String, ColumnType>());
		for (Entry<String, Column> e: this.Columns.entrySet())
			info.columns.put(e.getKey(), e.getValue().kind());

		gson.toJson(info, out);
		out.append('\n');

		for (Column c : this.Columns.values()) {
			c.write(out, gson);
			out.append('\n');
		}
	}
	public static Table read(BufferedReader r) throws IOException {
		return read(r, new Gson());
	}
	public static Table read(BufferedReader r, Gson gson) throws IOException {
		StorageInfo info = gson.fromJson(r.readLine(), StorageInfo.class);
		Table t = new Table(info.length);
		for (Entry<String, ColumnType> e : info.columns.entrySet())
			t.put(e.getKey(), e.getValue().read(r.readLine(), gson));
		return t;
	}

	public Table clone() {
		Table other = new Table(this.Length);
		for (Entry<String, Column> e : this.Columns.entrySet()) {
			other.Columns.put(e.getKey(), e.getValue().clone());
		}
		return other;
	}

	protected Table removeWhere(boolean[] remove, int newsize) {
		assert(this.length() == remove.length);
		Table newEvents = new Table(newsize);
		ArrayList<String> keys = new ArrayList<String>(this.keySet());

		for (String key : keys) {
			Column from = this.remove(key);
			Column to = newEvents.add(key, from.kind());
			int index = 0;
			for(int i = 0; i < from.length(); i++) {
				if (remove[i]) continue;
				to.setObject(index, from.getObject(index));
				index++;
			}
		}
		return newEvents;
	}
}
