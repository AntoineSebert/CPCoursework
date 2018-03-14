/**
 * 
 */
package common;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
	public static ZonedDateTime getDate() {
		return java.time.ZonedDateTime.now(ZoneOffset.UTC);
	}
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, timeUnit);
	}
	public static int compareDates(ZonedDateTime first, ZonedDateTime second) {
		Comparator<ZonedDateTime> comparator = Comparator.comparing(zdt -> zdt.truncatedTo(ChronoUnit.SECONDS));
		return comparator.compare(first, second);
	}
	public static Duration difference(ZonedDateTime first, ZonedDateTime second) {
		return Duration.between(first, second);
	}
}