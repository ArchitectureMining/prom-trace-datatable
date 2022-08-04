package org.processmining.plugins.tracedatatable.ColumnImpl;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.processmining.plugins.tracedatatable.ColumnType;

public class ColumnTimestamp extends ColumnArrayObject<Date> {
	public ColumnTimestamp(int size) {
		this(new Date[size]);
	}
	public ColumnTimestamp(Date[] values) {
		super(values);
	}
	protected Class<Date[]> getValuesClass() {
		return Date[].class;
	}
	public ColumnType kind() {
		return ColumnType.Timestamp;
	}
	public Date parse(String s) throws ParseException {
		return DateUtils.parseDate(s, DEFAULT_DATE_FORMATS);
	}
	public static final String[] DEFAULT_DATE_FORMATS = new String[] {
	    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(),
	    DateFormatUtils.ISO_DATETIME_FORMAT.getPattern(),
	    DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern()
	};
	public ColumnTimestamp clone() {
		return new ColumnTimestamp(this.values.clone());
	}
}