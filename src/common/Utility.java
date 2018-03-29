/**
 * 
 */
package common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author i
 *
 */
public abstract class Utility {
	public static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static String getStringDate() {
		return dateTimeFormat.format(new Date());
	}
	public static String getStringTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		return dateFormat.format(new Date());
	}
	public static long difference(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}
	public static void println(String data) { System.out.println('[' + Utility.getStringTime() + "]" + data); }
	public static Date stringToDate(String input) {
		Date date = null;
		try {
			date = dateTimeFormat.parse(input);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}