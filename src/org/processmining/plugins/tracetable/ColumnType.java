package org.processmining.plugins.tracetable;

import java.util.HashMap;
import java.util.Map.Entry;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnBoolean;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnCategoricalLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnContinuous;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnDiscrete;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnLiteral;
import org.processmining.plugins.tracetable.ColumnImpl.ColumnTimestamp;

import com.google.gson.Gson;

public enum ColumnType {
	Boolean,
	Continuous,
	Discrete,
	Literal,
	CategoricalLiteral,
	Timestamp,
	Custom;

	public static ColumnType from(XAttribute a) {
		if (a instanceof XAttributeBoolean)
			return Boolean;
		if (a instanceof XAttributeContinuous)
			return Continuous;
		if (a instanceof XAttributeDiscrete)
			return Discrete;
		if (a instanceof XAttributeTimestamp)
			return Timestamp;
		if (a instanceof XAttributeLiteral)
			return Literal;
		throw new ClassCastException("XAttribute could not be casted to a known type.");
	}
	public static HashMap<String, ColumnType> from(XAttributeMap attrs) {
		if (attrs == null)
			return null;
		HashMap<String, ColumnType> columns = new HashMap<String, ColumnType>(attrs.size());
		for (Entry<String,XAttribute> e: attrs.entrySet()) {
			columns.put(e.getKey(), from(e.getValue()));
		}
		return columns;
	}

	public Column create(int size) {
		switch (this) {
		case Boolean:
			return new ColumnBoolean(size);
		case Continuous:
			return new ColumnContinuous(size);
		case Discrete:
			return new ColumnDiscrete(size);
		case Literal:
			return new ColumnLiteral(size);
		case CategoricalLiteral:
			return new ColumnCategoricalLiteral(size);
		case Timestamp:
			return new ColumnTimestamp(size);
		case Custom:
			throw new IllegalStateException("Cannot create a new column of type Custom");
		default:
			return null;
		}
	}
	private Column createNull() {
		switch (this) {
		case Boolean:
			return new ColumnBoolean(null);
		case Continuous:
			return new ColumnContinuous(null);
		case Discrete:
			return new ColumnDiscrete(null);
		case Literal:
			return new ColumnLiteral(null);
		case CategoricalLiteral:
			return new ColumnCategoricalLiteral(null, null);
		case Timestamp:
			return new ColumnTimestamp(null);
		case Custom:
			throw new IllegalStateException("Cannot create a new column of type Custom");
		default:
			return null;
		}
	}
	public Column read(String s) {
		return this.read(s, new Gson());
	}
	public Column read(String s, Gson gson) {
		Column c = this.createNull();
		if (c == null)
			return c;
		c.read(s, gson);
		return c;
	}
}
