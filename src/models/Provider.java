package models;

/** The interface every provider must implement.
 * <p>
 * The main principle is the following :
 * <ol>
 *   <li>{@link #tryAutomaticRetrieve()} is called</li>
 *   <li>If it returned <code>false</code>, the user is prompted for the main
 *     delivery content that is then given to {@link #retrieveFromString(String)}</li>
 *   <li>If {@link #useMissingList()} returns true, the user is prompted for the
 *     missing list content that is then given to {@link #retrieveMissing(String)}</li>
 * </ol>
 * At the end of this procedure, {@link #getItems(Retriever)} is called so that
 * the application might get, one by one, all the items (missing list taken into account).
 * {@link #getItems(Retriever)} also clears the list of items so that a new cycle may begin again.
 */
public interface Provider
{
	/** Try to automatically retrieve the information related to the last delivery.
	 * <p>
	 * More precisely, this delivery should be the last delivery that has
	 * not been retrieved yet, not necessarily the very last one.
	 * If this operation is not possible for a given provider,
	 * this function must then only return <code>false</code>.
	 *
	 * @return <code>true</code> in case the information has been
	 *   automatically retrieved, <code>false</code> if a call to
	 *   {@link #retrieveFromString(String)} is required.
	 */
	public boolean tryAutomaticRetrieve();

	/** Retrieve the main information related to the delivery from a string parameter.
	 * <p>
	 * In case the main information related to the last delivery could not be automatically
	 * retrieved with {@link #tryAutomaticRetrieve()}, this function is used to get the
	 * information from a string given by the user (e.g. the content of an email).
	 *
	 * @param data The data containing the information.
	 * @throws IllegalArgumentException If <code>data</code> is malformed.
	 */
	public void retrieveFromString(String data);

	/** Should we ask the used for any missing list content.
	 *
	 * @return <code>true</code> if the program is to ask the user for a
	 *   missing list and give the content with {@link #retrieveMissing(String)}.
	 */
	public boolean useMissingList();

	/** Retrieve the missing information related to the delivery from a string parameter.
	 * <p>
	 * In case {@link #useMissingList()} returns <code>true</code>, this function is used to
	 * get the missing information out of the <code>data</code> string parameter.
	 *
	 * @param data The missing information in a string.
	 * @throws IllegalArgumentException If <code>data</code> is malformed.
	 */
	public void retrieveMissing(String data);

	/** Inform the <code>retriever</code> of all the final products and prices of the delivery.
	 * <p>
	 * Note that this function should block until all the information has been transmitted.
	 * A direct second call to this function should do nothing since it clears the corresponding list.
	 *
	 * @param retriever The {@link Retriever} to use to tell the results.
	 */
	public void getItems(Retriever retriever);
}
