package org.processmining.plugins.tracetable.ColumnImpl;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.processmining.plugins.tracetable.ColumnType;
import org.processmining.plugins.tracetable.ColumnAbstract.ColumnArrayObject;

public class ColumnLiteral extends ColumnArrayObject<String> {
	public ColumnLiteral(int size) {
		this(new String[size]);
	}
	public ColumnLiteral(String[] values) {
		super(values);
	}
	protected Class<String[]> getValuesClass() {
		return String[].class;
	}
	public ColumnType kind() {
		return ColumnType.Literal;
	}
	public ColumnLiteral clone() {
		return new ColumnLiteral(this.values.clone());
	}

	protected boolean canTypeLiteral() {
		return true;
	}
	protected ColumnLiteral intoTypeLiteral() {
		return this;
	}
	protected String intoTypeLiteral(int i) {
		return this.values[i];
	}
	protected boolean canTypeBoolean() {
		return true;
	}
	protected boolean intoTypeBoolean(int i) {
		return parseBoolean(this.values[i]);
	}
	protected boolean canTypeContinuous() {
		return canParseContinuous(this.values[0]);
	}
	protected double intoTypeContinuous(int i) {
		return parseContinuous(this.values[i]);
	}
	protected boolean canTypeDiscrete() {
		return canParseDiscrete(this.values[0]);
	}
	protected long intoTypeDiscrete(int i) {
		return parseDiscrete(this.values[i]);
	}
	protected boolean canTypeTimestamp() {
		return canParseTimestamp(this.values[0]);
	}
	protected Date intoTypeTimestamp(int i) throws ParseException {
		return parseTimestamp(this.values[i]);
	}


	public static boolean parseBoolean(String s) {
		if (s == null)
			return false;
		return Boolean.parseBoolean(s);
	}
	public static boolean canParseContinuous(String s) {
		try {
			parseContinuous(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	public static double parseContinuous(String s) throws NumberFormatException {
		if (s == null)
			return 0;
		return Double.parseDouble(s);
	}
	public static boolean canParseDiscrete(String s) {
		try {
			parseDiscrete(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	public static long parseDiscrete(String s) throws NumberFormatException {
		if (s == null)
			return 0;
		return Long.parseLong(s);
	}
	public static boolean canParseTimestamp(String s) {
		try {
			parseTimestamp(s);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	public static Date parseTimestamp(String s) throws ParseException {
		return parseTimestamp(s, DEFAULT_DATE_FORMATS);
	}
	public static Date parseTimestamp(String s, String[] formats) throws ParseException {
		if (s == null)
			return null;
		return DateUtils.parseDate(s, formats);
	}
	public static final String[] DEFAULT_DATE_FORMATS = new String[] {
	    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(),
	    DateFormatUtils.ISO_DATETIME_FORMAT.getPattern(),
	    DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern()
	};
}