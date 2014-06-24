import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import providers.*;
import loggers.*;
import models.*;
import util.*;

/** The main class for the program. */
public class AutoAppro
{
	public static final String VERSION = "test";
	private static final String UPDATE_URL = "http://129.104.234.60/AutoAppro/";
	private static final int HTTP_UPDATE_TIMEOUT = 2000;
	private static final String USAGE = "Usage: java -jar AutoAppro.jar [-locale lang country] [-help]";
	/* Example values for the splash screen */
	private static final Rectangle SPLASH_PRGSS_AREA = new Rectangle(20, 350, 600, 5);
	private static final Color SPLASH_PRGSS_COLOR = Color.WHITE;
	private static final Point SPLASH_STATUS_POS = new Point(25, 360);
	private static final Color SPLASH_STATUS_COLOR = Color.WHITE;
	/* List of all the available providers and loggers */
	private static final Provider[] providers = {new Intermarche()};
	private static final Logger[] loggers = {new Bar2()};

	private static MySplash splash;

	public static ResourceBundle messages;
	public static Provider provider;
	public static Logger logger;
	public static ImageIcon icon;
	public static HashMap<Serializable, Product> products;
	public static boolean productsModified;

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
					if (p.getName().equals(savedName))
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
			MyPreferences.set("provider", provider.getName());
		}
		/* Getting the logger */
		splash.setStatus(messages.getString("load_logger"), 0.1);
		logger = null;
		{
			Serializable record = MyPreferences.get("logger");
			if (record != null)
			{
				String savedName = (String) record;
				for (Logger l : loggers)
				{
					if (l.getName().equals(savedName))
					{
						logger = l;
						break;
					}
				}
			}
		}
		if (logger == null)
		{
			if (loggers.length < 2)
			{
				logger = loggers[0];
			} else {
				Object chosen = JOptionPane.showInputDialog(null, messages.getString("logger_msg"),
						messages.getString("logger_title"), JOptionPane.QUESTION_MESSAGE, icon,
						loggers, loggers[0]);
				if (chosen == null)
				{
					splash.dispose();
					return;
				}
				logger = (Logger) chosen;
			}
			MyPreferences.set("logger", logger.getName());
		}
		/* Checking for updates */
		splash.setStatus(messages.getString("load_updates"), 0.15);
		String lastVersion = HTTPDownload.readFirstLine(UPDATE_URL + "last.txt", HTTP_UPDATE_TIMEOUT);
		if (lastVersion != null)
		{
			if (!lastVersion.equals(VERSION))
			{
				splash.setStatus(messages.getString("load_updates2"), 0.4);
				try {
					HTTPDownload.download(UPDATE_URL + "updater.jar", "updater.jar");
					Runtime.getRuntime().exec("java -jar updater.jar");
					return;
				} catch (Exception e) {
					splash.setStatus(messages.getString("err_updates"), 0.4);
					try { Thread.sleep(2000); } catch (InterruptedException e2) { }
				}
			}
		} else {
			splash.setStatus(messages.getString("err_updates"), 0.15);
			try { Thread.sleep(2000); } catch (InterruptedException e) { }
		}
		/* Getting the products associated with the current provider */
		splash.setStatus(messages.getString("load_products"), 0.95);
		getProducts();
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

	/* A little external function to get the products to avoid a SuppressWarnings
	 * over the whole main function. */
	@SuppressWarnings("unchecked")
	private static void getProducts()
	{
		Object productsRecord = SerialFileHandler.readObject(provider.getName() + ".dat");
		if ((productsRecord != null) && (productsRecord.getClass() == HashMap.class))
			products = (HashMap<Serializable, Product>) productsRecord;
		else
			products = new HashMap<Serializable, Product>();
		productsModified = false;
	}

	/** Try to save the list of products if necessary. */
	public static void saveProducts()
	{
		if (!productsModified) return;
		SerialFileHandler.writeObject(products, provider.getName() + ".dat");
		productsModified = false;
	}

	/** Display the help web-page or return its link.
	 *
	 * @return <code>null</code> if the web-page could be opened,
	 *   its URL otherwise.
	 */
	public static String displayHelp()
	{
		String url = UPDATE_URL;
		{
			Locale locale;
			Serializable record = MyPreferences.get("locale");
			if (record != null)
				locale = (Locale) record;
			else
				locale = Locale.getDefault();
			url += "?lg=" + locale.getLanguage();
		}
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			return url;
		}
		return null;
	}
}
