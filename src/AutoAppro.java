import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import providers.*;
import models.*;
import util.*;

/** The main class for the program. */
public class AutoAppro
{
	private static final String VERSION = "1.0.0";
	private static final String UPDATE_URL = "http://rosetta.hd.free.fr/";
	private static final String USAGE = "Usage: java -jar AutoAppro.jar [-locale lang country] [-help]";
	/* Example values for the splash screen */
	private static final Rectangle SPLASH_PRGSS_AREA = new Rectangle(20, 350, 600, 5);
	private static final Color SPLASH_PRGSS_COLOR = Color.WHITE;
	private static final Point SPLASH_STATUS_POS = new Point(25, 360);
	private static final Color SPLASH_STATUS_COLOR = Color.WHITE;
	/* List of all the available providers */
	private static final Provider[] providers = {new Intermarche()};

	private static MySplash splash;

	public static ResourceBundle messages;
	public static Provider provider;
	public static Icon icon;

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
					splash.dispose();
					return;
				default:
					throw new IllegalArgumentException("Unknown option " + args[i]);
				}
			}
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			System.err.println(USAGE);
			splash.dispose();
			return;
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
		/* Loading the icon for the application */
		java.net.URL imgURL = AutoAppro.class.getResource("icon.png");
		icon = new ImageIcon(imgURL);
		/* Getting the provider */
		splash.setStatus(messages.getString("load_provider"), 0.05);
		provider = null;
		{
			Serializable record = MyPreferences.get("provider");
			if (record != null)
			{
				String savedName = (String) record;
				for (Provider p : providers)
				{
					if (p.getClass().getSimpleName().equals(savedName))
					{
						provider = p;
						break;
					}
				}
			}
		}
		if (provider == null)
		{
			if (providers.length < 2)
			{
				provider = providers[0];
			} else {
				Object chosen = JOptionPane.showInputDialog(null, messages.getString("provider_msg"),
						messages.getString("provider_title"), JOptionPane.QUESTION_MESSAGE, icon,
						providers, providers[0]);
				if (chosen == null)
				{
					splash.dispose();
					return;
				}
				provider = (Provider) chosen;
			}
			MyPreferences.set("provider", provider.getClass().getSimpleName());
			MyPreferences.save();
		}
		/* Checking for updates */
		splash.setStatus(messages.getString("load_updates"), 0.1);
		String lastVersion = HTTPDownload.readFirstLine(UPDATE_URL + "last.txt");
		if (lastVersion != null)
		{
			if (!lastVersion.equals(VERSION))
			{
				// TODO update the software
			}
		} else {
			splash.setStatus(messages.getString("err_updates"), 0.1);
			try { Thread.sleep(2000); } catch (InterruptedException e) { }
		}
		/* Creating the main window */
		splash.setStatus("", 1);
		try {
			SwingUtilities.invokeAndWait(MainWindow.setupGUI);
			splash.dispose();
			SwingUtilities.invokeLater(MainWindow.displayGUI);
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			splash.dispose();
			return;
		}
	}
}
