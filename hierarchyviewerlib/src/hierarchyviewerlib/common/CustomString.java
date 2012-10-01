package hierarchyviewerlib.common;

import java.util.Locale;
import java.util.ResourceBundle;

public class CustomString {
	private static final String BUNDLE_NAME = "hierarchyviewerlib.resource.custom";
	private static ResourceBundle rb = null;

	public static void setBundle(Locale locale) {
		try {
			rb = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		} catch (Exception e) {
			rb = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
		}
	}

	public static String getString(String key) {
		try {
			String keyValue = new String(rb.getString(key).getBytes(
			        "ISO-8859-1"), "UTF-8");
			return keyValue;
		} catch (Exception e) {
			return key;
		}
	}
}
