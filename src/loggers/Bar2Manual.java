package loggers;

import java.io.*;
import java.util.List;

import models.*;

/** A logger for the currently running version of the bar software.
 * <p>
 * The {@link #log(List)} function returns some JavaScript code that,
 * once pasted in the JS console on the correct web-page,
 * fills the corresponding fields so that the user only needs to valid
 * the delivery.
 */
public class Bar2Manual extends Logger
{
	private static final String PREAMBULE_FILENAME = "bar1_preambule.js";

	@Override
	public String log(Iterable<LogItem> items) throws Exception
	{
		StringBuilder result = new StringBuilder(2048);
		InputStream in = getClass().getResourceAsStream(PREAMBULE_FILENAME);
		if (in == null)
			throw new Exception(PREAMBULE_FILENAME + " was not found.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = reader.readLine()) != null)
		{
			result.append(line);
			result.append('\n');
		}
		reader.close();
		for (LogItem item : items)
		{
			result.append("addInterProduct(" + item.barID);
			result.append(", " + myDblToString(item.quantity));
			int price = (int) item.price;
			result.append(", '" + (price / 100));
			result.append((price < 10 ? ".0" : ".") + (price % 100));
			result.append(");\n");
		}
		return result.toString();
	}

	private static String myDblToString(double value)
	{
		int approx = (int) value;
		double remaining = value - approx;
		if (remaining < 0.0001) return Integer.toString(approx);
		if (remaining > 0.9999) return Integer.toString(approx + 1);
		return Double.toString(value);
	}

	@Override
	public String getName()
	{
		return "Site bars 2.0 beta - Manuel";
	}
}
