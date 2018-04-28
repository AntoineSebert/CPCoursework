package common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
/*
 * @author Anthony Sébert
 * A collection of functions related to the dates managing or JavaFX.
 */
public abstract class Utility {
	/* attributes */
		public static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	/* members */
		// date
			public static String getStringDate() { return dateTimeFormat.format(new Date()); }
			public static String getStringTime() {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				return dateFormat.format(new Date());
			}
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
		// operator-
			public static long difference(Date date1, Date date2, TimeUnit timeUnit) {
				long diffInMillies = date2.getTime() - date1.getTime();
				return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
			}
		// display
			public static void println(String data) { System.out.println('[' + Utility.getStringTime() + "]" + data); }
		// javafx components
			public static TextField createEditableTextField(String hint) {
				TextField newTextField = new TextField();
				newTextField.setPromptText(hint);

				return newTextField;
			}
			public static Button createButton(String text, EventHandler<ActionEvent> action) {
				Button newButton = new Button(text);
				newButton.setOnAction(action);

				return newButton;
			}
			public static Text createText(String text, int width, double fontSize, TextAlignment alignment) {
				Text newText = new Text(text);
				newText.setFont(new Font(fontSize));
				newText.setWrappingWidth(width);
				newText.setTextAlignment(alignment);

				return newText;
			}
}