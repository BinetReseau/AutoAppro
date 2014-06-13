package util;

import java.awt.*;

/** An easy way to handle interactive splash screens.
 * <p>
 * Only one single instance of this class may be created,
 * as there is also only one splash-screen.
 * <p>
 * Do not forget to add the "-splash:<filename>" argument to the Java application launcher
 * for the splash screen not to be null. If in a .jar file, you can also add a line
 * "SplashScreen-Image: <image name>" in the manifest file.
 */
public class MySplash
{
	private static volatile boolean instanceCreated = false;
	private static final SplashScreen splash = SplashScreen.getSplashScreen();
	
	private final Graphics2D graphics;
	private final Rectangle progressArea;
	private final Color progressColor;
	private final Point statusPosition;
	private final Color statusColor;
	private int statusLastWidth;

	/** Construct a <code>MySplash</code> object with the given parameters.
	 *
	 * @param progressArea The area to use for the progress-bar.
	 * @param progressColor The color to use for the progress-bar.
	 * @param statusPosition The anchor (upper-left corner) to use for the status text.
	 * @param statusColor The color to use for the status text.
	 */
	public MySplash(Rectangle progressArea, Color progressColor, Point statusPosition, Color statusColor)
	{
		this.progressColor = progressColor;
		this.statusPosition = statusPosition;
		this.statusColor = statusColor;
		this.progressArea = progressArea;
		if (splash == null)
		{
			graphics = null;
			return;
		}
		synchronized (splash)
		{
			if (instanceCreated)
				throw new RuntimeException("Only one single instance accepted.");
			instanceCreated = true;
		}
		graphics = splash.createGraphics();
		graphics.setColor(progressColor);
		graphics.drawRect(progressArea.x, progressArea.y, progressArea.width, progressArea.height);
		splash.update();
		statusLastWidth = 0;
	}

	/** Set the new progress.
	 *
	 * @param progress The value of the new progress, between 0 and 1.
	 * @throws IllegalArgumentException If <code>progress</code> is not between 0 and 1.
	 */
	public synchronized void setProgress(double progress)
	{
		if (splash == null) return;
		if ((progress < 0) || (progress > 1))
			throw new IllegalArgumentException("Invalid progress " + progress);
		graphics.setColor(progressColor);
		int width = (int) (progress * progressArea.width);
		graphics.fillRect(progressArea.x, progressArea.y, width, progressArea.height);
		splash.update();
	}

	/** Set the new status and the corresponding progress.
	 *
	 * @param status The new status string.
	 * @param progress The value of the new progress, between 0 and 1.
	 * @throws IllegalArgumentException If <code>progress</code> is not between 0 and 1.
	 */
	public synchronized void setStatus(String status, double progress)
	{
		if (splash == null) return;
		if ((progress < 0) || (progress > 1))
			throw new IllegalArgumentException("Invalid progress " + progress);
		graphics.setColor(progressColor);
		int width = (int) (progress * progressArea.width);
		graphics.fillRect(progressArea.x, progressArea.y, width, progressArea.height);
		graphics.setComposite(AlphaComposite.Clear);
		graphics.fillRect(statusPosition.x, statusPosition.y, statusLastWidth, graphics.getFontMetrics().getHeight());
		graphics.setPaintMode();
		graphics.setColor(statusColor);
		graphics.drawString(status, statusPosition.x, statusPosition.y + graphics.getFontMetrics().getAscent());
		statusLastWidth = graphics.getFontMetrics().stringWidth(status);
		splash.update();
	}

	/** Remove the splash-screen if it is still visible. */
	public synchronized void dispose()
	{
		if ((splash != null) && splash.isVisible())
			splash.close();
	}
}
