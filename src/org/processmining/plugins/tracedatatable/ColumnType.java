package org.processmining.plugins.tracedatatable;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnBoolean;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnCategoricalLiteral;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnContinuous;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnDiscrete;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnLiteral;
import org.processmining.plugins.tracedatatable.ColumnImpl.ColumnTimestamp;

import com.google.gson.Gson;

public enum ColumnType {
	Boolean,
	Continuous,
	Discrete,
	Literal,
	CategoricalLiteral,
	Timestamp;

	public static ColumnType[] getAlternatives(ColumnType type) {
		if (type == ColumnType.Literal || type == ColumnType.CategoricalLiteral) {
			return ColumnType.values();
		} else {
			return new ColumnType[] {
			           type,
			           ColumnType.Literal,
			           ColumnType.CategoricalLiteral,
			       };
		}
	}

	public static ColumnType from(XAttribute a) {
		if (a instanceof XAttributeBoolean)
			return ColumnType.Boolean;
		if (a instanceof XAttributeContinuous)
			return ColumnType.Continuous;
		if (a instanceof XAttributeDiscrete)
			return ColumnType.Discrete;
		if (a instanceof XAttributeTimestamp)
			return ColumnType.Timestamp;
		if (a instanceof XAttributeLiteral)
			return ColumnType.Literal;
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
		default:
			return null;
		}
	}
	public Column parse(Column c) throws NumberFormatException, ParseException {
		if (c instanceof ColumnLiteral)
			return this.parse((ColumnLiteral)c);
		if (c instanceof ColumnCategoricalLiteral)
			return this.parse((ColumnCategoricalLiteral)c);
		throw new ClassCastException("Could not cast column to a literal column");
	}
	public Column parse(ColumnLiteral c) throws NumberFormatException, ParseException {
		Column nc = this.create(c.length());
		nc.parse(c);
		return nc;
	}
	public Column parse(ColumnCategoricalLiteral c) throws NumberFormatException, ParseException {
		Column nc = this.create(c.length());
		nc.parse(c);
		return nc;
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
