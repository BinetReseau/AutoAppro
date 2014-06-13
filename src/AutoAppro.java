import java.awt.*;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import util.*;

/** The main class for the program. */
public class AutoAppro
{
	private static final String USAGE = "Usage: java -jar AutoAppro.jar [-locale lang country] [-help]";
	/* Example values for the splash screen */
	private static final Rectangle SPLASH_PRGSS_AREA = new Rectangle(20, 350, 600, 5);
	private static final Color SPLASH_PRGSS_COLOR = Color.WHITE;
	private static final Point SPLASH_STATUS_POS = new Point(25, 360);
	private static final Color SPLASH_STATUS_COLOR = Color.WHITE;

	private static MySplash splash;

	public static ResourceBundle messages;

	/** The starting point of
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args)
	{
		/* Initializing the splash screen */
		splash = new MySplash(SPLASH_PRGSS_AREA, SPLASH_PRGSS_COLOR,
				SPLASH_STATUS_POS, SPLASH_STATUS_COLOR);
		/* Parsing the command-line arguments */
		try {
			for (int i = 0; i < args.length; ++i)
			{
				switch (args[i])
				{
				case "-locale":
					if (i + 2 >= args.length)
						throw new IllegalArgumentException("Missing argument for the locale.");
					Locale newLocale = new Locale(args[++i], args[++i]);
					MyPreferences.set("locale", newLocale);
					MyPreferences.save();
					break;
				case "-help":
					System.out.println(USAGE);
					return;
				default:
					throw new IllegalArgumentException("Unknown option " + args[i]);
				}
			}
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			System.err.println(USAGE);
		}
		/* Loading the language bundle */
		{
			Locale locale;
			Serializable record = MyPreferences.get("locale");
			if (record != null)
				locale = (Locale) record;
			else
				locale = Locale.getDefault();
			locale = new Locale("en", "US");
			messages = ResourceBundle.getBundle("MessagesBundle", locale);
		}
		/* Getting the provider */
		splash.setStatus(messages.getString("loadprovider"), 0.2);
		try { Thread.sleep(2000); } catch (InterruptedException e) { } // TODO
		/* Checking for updates */
		splash.setStatus(messages.getString("loadupdates"), 0.4);
		try { Thread.sleep(2000); } catch (InterruptedException e) { } // TODO
		/* Creating the main window */
		splash.setStatus("", 1);
		try { Thread.sleep(2000); } catch (InterruptedException e) { } // TODO
		splash.dispose();
		// TODO display the main window
	}
}
