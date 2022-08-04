package org.processmining.plugins.tracedatatable;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

public class ParseUtils {
	public static boolean parseBoolean(String s) {
		if (s == null)
			return false;
		return Boolean.parseBoolean(s);
	}
	public static void parseBoolean(String[] a, boolean[] b) {
		for (int i = 0; i < a.length; i++)
			b[i] = parseBoolean(a[i]);
	}
	public static void parseBoolean(String[] a, boolean[] b, int[] indices) {
		boolean[] map = new boolean[a.length];
		parseBoolean(a, map);
		for (int i = 0; i < b.length; i++)
			b[i] = map[indices[i]];
	}
	public static double parseDouble(String s) throws NumberFormatException {
		if (s == null)
			return 0;
		return Double.parseDouble(s);
	}
	public static void parseDouble(String[] a, double[] b) throws NumberFormatException {
		for (int i = 0; i < a.length; i++) {
			b[i] = parseDouble(a[i]);
		}
	}
	public static void parseDouble(String[] a, double[] b, int[] indices) throws NumberFormatException {
		double[] map = new double[a.length];
		parseDouble(a, map);
		for (int i = 0; i < b.length; i++)
			b[i] = map[indices[i]];
	}
	public static long parseLong(String s) throws NumberFormatException {
		if (s == null)
			return 0;
		return Long.parseLong(s);
	}
	public static void parseLong(String[] a, long[] b) throws NumberFormatException {
		for (int i = 0; i < a.length; i++) {
			b[i] = parseLong(a[i]);
		}
	}
	public static void parseLong(String[] a, long[] b, int[] indices) throws NumberFormatException {
		long[] map = new long[a.length];
		parseLong(a, map);
		for (int i = 0; i < b.length; i++)
			b[i] = map[indices[i]];
	}
	public static Date parseDate(String s) throws ParseException {
		if (s == null)
			return null;
		return DateUtils.parseDate(s, DEFAULT_DATE_FORMATS);
	}
	public static final String[] DEFAULT_DATE_FORMATS = new String[] {
	    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(),
	    DateFormatUtils.ISO_DATETIME_FORMAT.getPattern(),
	    DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern()
	};
	public static void parseDate(String[] a, Date[] b) throws ParseException {
		for (int i = 0; i < a.length; i++) {
			b[i] = parseDate(a[i]);
		}
	}
	public static void parseDate(String[] a, Date[] b, int[] indices) throws ParseException {
		Date[] map = new Date[a.length];
		parseDate(a, map);
		for (int i = 0; i < b.length; i++)
			b[i] = map[indices[i]];
	}
}
