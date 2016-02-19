package org.pedrero.fbwatcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class Displayer implements IDisplayer {
	public String getInfo() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); /* "dd/MM/yyyy HH:mm:ss" */
		Date today = Calendar.getInstance(Locale.FRANCE).getTime();
		return "Spring boot in action at " + sdf.format(today) + "!";
	}
}
