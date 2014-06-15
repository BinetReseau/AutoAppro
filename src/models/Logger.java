package models;

/** The interface every logger must implement. */
public interface Logger
{
	/** Log all the given items.
	 *
	 * @param items The list of items to log.
	 * @return <code>null</code> if the function is automatic,
	 *   or the result string to put in the clip-board.
	 * @throws Exception If an error occurs.
	 */
	public String log(Iterable<LogItem> items) throws Exception;
}
