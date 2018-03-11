/**
 * 
 */
package common;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author i
 *
 */
public abstract class Utility {
	public static String getDate() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	public static String getTime() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	public static void println(String data) {
		System.out.println('[' + Utility.getTime() + "]" + data);
	}
}
