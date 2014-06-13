import java.awt.*;

import util.*;

/** The main class for the program. */
public class AutoAppro {
	/* Example values for the splash screen */
	private static final Rectangle SPLASH_PRGSS_AREA = new Rectangle(20, 350, 600, 5);
	private static final Color SPLASH_PRGSS_COLOR = Color.WHITE;
	private static final Point SPLASH_STATUS_POS = new Point(25, 360);
	private static final Color SPLASH_STATUS_COLOR = Color.WHITE;

	private static MySplash splash;

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
		// TODO
		/* Loading the language bundle */
		// TODO
		/* Getting the provider */
		splash.setStatus("Retrieving the provider ...", 0.2);
		try { Thread.sleep(2000); } catch (InterruptedException e) { } // TODO
		/* Checking for updates */
		splash.setStatus("Checking for updates ...", 0.4);
		try { Thread.sleep(2000); } catch (InterruptedException e) { } // TODO
		/* Creating the main window */
		splash.setStatus("", 1);
		try { Thread.sleep(2000); } catch (InterruptedException e) { } // TODO
		splash.dispose();
		// TODO display the main window
	}
}
