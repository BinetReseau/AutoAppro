import javax.swing.*;

/** The main window handler. */
public class MainWindow {
	private static JFrame mainWindow;
	/** The initializing function for the main window. */
	public static Runnable setupGUI = new Runnable() {
		@Override
		public void run() {
			mainWindow = new JFrame(lang("window_title") + AutoAppro.VERSION);
			mainWindow.setIconImage(AutoAppro.icon.getImage());
			// TODO
			mainWindow.pack();
		}
	};

	/** The displaying function for the main window. */
	public static Runnable displayGUI = new Runnable() {
		@Override
		public void run() {
			mainWindow.setVisible(true);
		}
	};

	/* Just a little shortcut ... */
	private static String lang(String keyword)
	{
		return AutoAppro.messages.getString(keyword);
	}
}
