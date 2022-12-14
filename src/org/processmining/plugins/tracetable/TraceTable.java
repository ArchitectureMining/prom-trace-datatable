package org.processmining.plugins.tracetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.Pair;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnBoolean;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnCategoricalLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnContinuous;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnDiscrete;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnTimestamp;

import com.google.gson.Gson;

public class TraceTable {
	// For ProM Plugin Management
	public static final String Name = "TraceTable";
	public static final String Extension = "tracetable";
	// Stored in file
	public static final String MetaEventCount = "meta:event_count";
	// Required
	public static final String MetaTraceId = "meta:trace_id";

	public Table Traces, TraceMeta;
	public Table Events, EventMeta;

	public TraceTable(Table traces, ColumnDiscrete event_count, Table events) {
		this.Traces = traces;
		this.TraceMeta = new Table(traces.length());
		this.Events = events;
		this.EventMeta = new Table(events.length());
		this.TraceMeta.put(MetaEventCount, event_count);

		ColumnDiscrete trace_id = (ColumnDiscrete)this.TraceMeta.add(MetaTraceId, ColumnType.Discrete);
		ColumnDiscrete event_id = (ColumnDiscrete)this.EventMeta.add(MetaTraceId, ColumnType.Discrete);

		int eid = 0;
		for (int t = 0; t < this.Traces.length(); t++) {
			trace_id.set(t, t);
			for (int e = 0; e < event_count.get(t); e++, eid++) {
				event_id.set(eid, t);
			}
		}
	}

	public void clearMetadata() {
		Column count = this.TraceMeta.get(MetaEventCount), trace = this.TraceMeta.get(MetaTraceId), event = this.EventMeta.get(MetaTraceId);
		this.TraceMeta.clear();
		this.EventMeta.clear();
		this.TraceMeta.put(MetaEventCount, count);
		this.TraceMeta.put(MetaTraceId, trace);
		this.EventMeta.put(MetaTraceId, event);
	}

	public void sort(Column[] trace_order, Column[] event_order) {
		if (trace_order != null) {
			ArrayList<Column> traces = new ArrayList<Column>(this.Traces.size() + this.TraceMeta.size());
			traces.addAll(this.Traces.values());
			traces.addAll(this.TraceMeta.values());
			Column.sort(traces, trace_order);
			this.resetTraceIds();
		}
		if (event_order != null) {
			ArrayList<Column> events = new ArrayList<Column>(this.Events.size() + this.EventMeta.size());
			events.addAll(this.Events.values());
			events.addAll(this.EventMeta.values());
			Column.sort(events, event_order);
		}
	}
	public void sort(String[] trace_order, String[] event_order) {
		Column[] trace_columns = null;
		if (trace_order != null) {
			trace_columns = new Column[trace_order.length];
			for (int i = 0; i < trace_order.length; i++) {
				if (this.Traces.containsKey(trace_order[i]))
					trace_columns[i] = this.Traces.get(trace_order[i]);
				else
					trace_columns[i] = this.TraceMeta.get(trace_order[i]);
			}
		}
		Column[] event_columns = null;
		if (event_order != null) {
			event_columns = new Column[event_order.length];
			for (int i = 0; i < event_order.length; i++) {
				if (this.Events.containsKey(event_order[i]))
					event_columns[i] = this.Events.get(event_order[i]);
				else
					event_columns[i] = this.EventMeta.get(event_order[i]);
			}
		}
		this.sort(trace_columns, event_columns);
	}
	public void resetTraceIds() {
		ColumnDiscrete trace_ids = (ColumnDiscrete) this.TraceMeta.get(MetaTraceId);
		ColumnDiscrete event_ids = (ColumnDiscrete) this.EventMeta.get(MetaTraceId);

		int[] trace_ids_map = new int[trace_ids.length()];
		for (int i = 0; i < trace_ids_map.length; i++)
			trace_ids_map[(int) trace_ids.get(i)] = i;

		for (int e = 0; e < event_ids.length(); e++) {
			event_ids.set(e, trace_ids_map[(int) event_ids.get(e)]);
		}
	}
	public void removeWhere(boolean[] remove) {
		assert(this.Events.length() == remove.length);
		ColumnDiscrete ids = this.EventMeta.getDiscrete(MetaTraceId);
		ColumnDiscrete counts = this.TraceMeta.getDiscrete(MetaEventCount);

		int total = 0;
		for (int i = 0; i < remove.length; i++) {
			if (remove[i]) {
				int idx = (int) ids.get(i);
				counts.set(idx, counts.get(idx) - 1);
			} else
				total++;
		}

		this.Events = this.Events.removeWhere(remove, total);
		this.EventMeta = this.EventMeta.removeWhere(remove, total);
	}

	public void write(Appendable out) throws IOException {
		this.write(out, new Gson());
	}
	public void write(Appendable out, Gson gson) throws IOException {
		this.Traces.write(out, gson);
		this.TraceMeta.get(MetaEventCount).write(out, gson);
		out.append('\n');
		this.Events.write(out, gson);
	}
	public static TraceTable read(BufferedReader r) throws IOException {
		return read(r, new Gson());
	}
	public static TraceTable read(BufferedReader r, Gson gson) throws IOException {
		return new TraceTable(Table.read(r, gson), (ColumnDiscrete)ColumnType.Discrete.read(r.readLine(), gson), Table.read(r, gson));
	}

	public static TraceTable create(XLog log, Progress progress) {
		Pair<XTrace,XEvent> first_items = getFirstLogItems(log);
		if (first_items == null)
			return new TraceTable(new Table(0), new ColumnDiscrete(0), new Table(0));

		Map<String, ColumnType> trace_columns = ColumnType.from(first_items.getFirst().getAttributes());
		Map<String, ColumnType> event_columns = ColumnType.from(first_items.getSecond().getAttributes());

		ColumnDiscrete count = new ColumnDiscrete(log.size());

		int trace_id = 0, event_id = 0;
		for (XTrace trace : log) {
			count.set(trace_id, trace.size());
			event_id += trace.size();
			trace_id++;
		}

		TraceTable set = new TraceTable(
		    new Table(trace_id, trace_columns),
		    count,
		    new Table(event_id, event_columns)
		);

		ArrayList<String> removeKeys = new ArrayList<String>();
		trace_id = 0;
		event_id = 0;
		for (XTrace trace : log) {
			createSet(set.Traces, trace_id, trace.getAttributes(), removeKeys);
			for (XEvent event : trace) {
				createSet(set.Events, event_id, event.getAttributes(), removeKeys);
				event_id++;
			}
			trace_id++;
		}
		return set;
	}
	private static Pair<XTrace, XEvent> getFirstLogItems(XLog log) {
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				return new Pair<XTrace, XEvent>(trace, event);
			}
		}
		return null;
	}
	private static void createSet(Table table, int index, XAttributeMap map, ArrayList<String> remove) {
		for (Entry<String, Column> e : table.entrySet()) {
			Column c = e.getValue();
			XAttribute a = map.get(e.getKey());
			if (a == null) {
				remove.add(e.getKey());
				continue;
			}
			switch (c.kind()) {
			case Boolean:
				((ColumnBoolean)c).set(index, ((XAttributeBoolean)a).getValue());
				break;
			case Continuous:
				((ColumnContinuous)c).set(index, ((XAttributeContinuous)a).getValue());
				break;
			case Discrete:
				((ColumnDiscrete)c).set(index, ((XAttributeDiscrete)a).getValue());
				break;
			case Literal:
				((ColumnLiteral)c).set(index, ((XAttributeLiteral)a).getValue());
				break;
			case CategoricalLiteral:
				((ColumnCategoricalLiteral)c).set(index, ((XAttributeLiteral)a).getValue());
				break;
			case Timestamp:
				((ColumnTimestamp)c).set(index, ((XAttributeTimestamp)a).getValue());
				break;
			default:
				break;
			}
		}
		table.removeAll(remove);
		remove.clear();
	}
}
