/**
 * 
 */
package common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author i
 *
 */
public abstract class Utility {
	public static String getStringDate() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	public static String getStringTime() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	public static void println(String data) {
		System.out.println('[' + Utility.getStringTime() + "]" + data);
	}
	public static Date getDate() {
		return Date.from(java.time.ZonedDateTime.now(ZoneOffset.UTC).toInstant());
	}
}
