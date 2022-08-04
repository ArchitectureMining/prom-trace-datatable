package org.processmining.plugins.tracedatatable;

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
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnBoolean;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnCategoricalLiteral;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnContinuous;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnDiscrete;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnLiteral;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnTimestamp;

import com.google.gson.Gson;

public class TraceSet {
	// Stored in file
	public static final String MetaEventCount = "meta:event_count";
	// Required
	public static final String MetaTraceId = "meta:trace_id";
	// Optional
	public static final String MetaEventStartTime = "meta:event_start_time";
	public static final String MetaEventEndTime = "meta:event_end_time";

	public final Table Traces, TraceMeta;
	public final Table Events, EventMeta;

	public TraceSet(Table traces, ColumnDiscrete event_count, Table events) {
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

	public void generateTimeMetadata(String timestampColumn) {
		this.TraceMeta.remove(MetaEventStartTime);
		this.TraceMeta.remove(MetaEventEndTime);

		ColumnTimestamp event_timestamp = (ColumnTimestamp) this.Events.get(timestampColumn);
		ColumnDiscrete event_count = (ColumnDiscrete) this.TraceMeta.get(MetaEventCount);

		this.sort(new Column[] {this.TraceMeta.get(MetaTraceId)}, new Column[] {this.EventMeta.get(MetaTraceId), event_timestamp});

		ColumnTimestamp event_starttime = (ColumnTimestamp) this.TraceMeta.add(MetaEventStartTime, ColumnType.Timestamp);
		ColumnTimestamp event_endtime = (ColumnTimestamp) this.TraceMeta.add(MetaEventEndTime, ColumnType.Timestamp);

		int esid = 0;
		for (int t = 0; t < this.Traces.length(); t++) {
			event_starttime.set(t, event_timestamp.get(esid));
			esid += event_count.get(t);
			event_endtime.set(t, event_timestamp.get(esid - 1));
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

	public void write(Appendable out) throws IOException {
		this.write(out, new Gson());
	}
	public void write(Appendable out, Gson gson) throws IOException {
		this.Traces.write(out, gson);
		this.TraceMeta.get(MetaEventCount).write(out, gson);
		out.append('\n');
		this.Events.write(out, gson);
	}
	public static TraceSet read(BufferedReader r) throws IOException {
		return read(r, new Gson());
	}
	public static TraceSet read(BufferedReader r, Gson gson) throws IOException {
		return new TraceSet(Table.read(r, gson), (ColumnDiscrete)ColumnType.Discrete.read(r.readLine(), gson), Table.read(r, gson));
	}

	public static TraceSet create(XLog log, Progress progress) {
		Pair<XTrace,XEvent> first_items = getFirstLogItems(log);
		if (first_items == null)
			return new TraceSet(new Table(0), new ColumnDiscrete(0), new Table(0));

		Map<String, ColumnType> trace_columns = ColumnType.from(first_items.getFirst().getAttributes());
		Map<String, ColumnType> event_columns = ColumnType.from(first_items.getSecond().getAttributes());

		ColumnDiscrete count = new ColumnDiscrete(log.size());

		int trace_id = 0, event_id = 0;
		for (XTrace trace : log) {
			count.set(trace_id, trace.size());
			event_id += trace.size();
			trace_id++;
		}

		TraceSet set = new TraceSet(
		    new Table(trace_id, trace_columns),
		    count,
		    new Table(event_id, event_columns)
		);

		trace_id = 0;
		event_id = 0;
		for (XTrace trace : log) {
			createSet(set.Traces, trace_id, trace.getAttributes());
			for (XEvent event : trace) {
				createSet(set.Events, event_id, event.getAttributes());
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
	private static void createSet(Table table, int index, XAttributeMap map) {
		for (Entry<String, Column> e : table.entrySet()) {
			Column c = e.getValue();
			XAttribute a = map.get(e.getKey());
			if (a == null)
				continue;
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
	}
}
