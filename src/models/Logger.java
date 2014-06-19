package models;

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

	@Override
	public String toString()
	{
		return this.getName();
	}
}
