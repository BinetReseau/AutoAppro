package models;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import AutoAppro.AutoAppro;

/** The abstract class every logger must implement. */
public abstract class Logger
{
	/** Log all the given items.
	 *
	 * @param items The list of items to log.
	 * @return <code>null</code> if the function is automatic,
	 *   or the result string to put in the clip-board.
	 * @throws Exception If an error occurs.
	 */
	public abstract String log(Iterable<LogItem> items) throws Exception;

	/** Get the logger name.
	 *
	 * @return The logger name.
	 */
	public abstract String getName();

	/** Initialize the logger.
	 * <p>
	 * Actually, some initializing part might be in the constructor,
	 * but the constructor may be called even if this logger is not used.
	 * Therefore, this function is called once the logger chosen,
	 * and only for this one.
	 */
	public void initialize()
	{
	}

	/** Get the logger panel.
	 * <p>
	 * This function is used when a new product is found on the
	 * provider side, to let the user choose (or not) the
	 * correct corresponding bar item.
	 * <p>
	 * Note that this function is to be called from the GUI thread.
	 *
	 * @param isNew Check whether this is a new product.
	 * @param defaultID The default ID (the old one if the product is not new,
	 *   The last free one after an existing ID else).
	 * @param providerName The name of the product as the provider sees it.
	 * @return A new {@link LoggerPanel} object.
	 */
	public LoggerPanel getLoggerPanel(boolean isNew, int defaultID, String providerName)
	{
		final JSpinner productBarID = new JSpinner(new SpinnerNumberModel(defaultID, 1, 999999, 1));
		@SuppressWarnings("serial")
		LoggerPanel defaultPanel = new LoggerPanel() {
			@Override
			public int getBarID()
			{
				return (int) productBarID.getValue();
			}
			@Override
			public void setBarID(int id)
			{
				productBarID.setValue(id);
			}
		};
		defaultPanel.setLayout(new GridLayout(0, 2, 0, 5));
		defaultPanel.add(new JLabel(AutoAppro.messages.getString("product_type")));
		defaultPanel.add(productBarID);
		return defaultPanel;
	}

	@Override
	public String toString()
	{
		return this.getName();
	}
}
