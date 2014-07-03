package models;

import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import AutoAppro.AutoAppro;

/** The abstract class every provider must implement.
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
 * <p>
 * One important thing you should keep in mind is that the {@link Serializable} results that might
 * be passed into the {@link Retriever} should implement the functions {@link Object#hashCode()}
 * and {@link Object#equals(Object)} (even though the hash code may be always the same,
 * which is not recommended for performance reasons).
 */
public abstract class Provider
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
	public abstract boolean tryAutomaticRetrieve();

	/** Retrieve the main information related to the delivery from a string parameter.
	 * <p>
	 * In case the main information related to the last delivery could not be automatically
	 * retrieved with {@link #tryAutomaticRetrieve()}, this function is used to get the
	 * information from a string given by the user (e.g. the content of an email).
	 *
	 * @param data The data containing the information.
	 * @throws IllegalArgumentException If <code>data</code> is malformed.
	 */
	public abstract void retrieveFromString(String data);

	/** Should we ask the used for any missing list content.
	 *
	 * @return <code>true</code> if the program is to ask the user for a
	 *   missing list and give the content with {@link #retrieveMissing(String)}.
	 */
	public abstract boolean useMissingList();

	/** Retrieve the missing information related to the delivery from a string parameter.
	 * <p>
	 * In case {@link #useMissingList()} returns <code>true</code>, this function is used to
	 * get the missing information out of the <code>data</code> string parameter.
	 *
	 * @param data The missing information in a string.
	 * @throws IllegalArgumentException If <code>data</code> is malformed.
	 */
	public abstract void retrieveMissing(String data);

	/** Inform the <code>retriever</code> of all the final products and prices of the delivery.
	 * <p>
	 * Note that this function should block until all the information has been transmitted.
	 * A direct second call to this function should do nothing since it clears the corresponding list.
	 *
	 * @param retriever The {@link Retriever} to use to tell the results.
	 */
	public abstract void getItems(Retriever retriever);

	/** Get the name of the provider.
	 *
	 * @return The name of the provider.
	 */
	public abstract String getName();

	/** Initialize the provider.
	 * <p>
	 * Actually, some initializing part might be in the constructor,
	 * but the constructor may be called even if this provider is not used.
	 * Therefore, this function is called once the provider chosen,
	 * and only for this one.
	 */
	public void initialize()
	{
	}

	@Override
	public String toString()
	{
		return this.getName();
	}

	/** Ask the user for any specific settings for this provider (GUI thread). */
	public void askSettings(JFrame parent)
	{
		JOptionPane.showMessageDialog(parent, AutoAppro.messages.getString("provider_no_opt"),
				AutoAppro.messages.getString("common_info"), JOptionPane.INFORMATION_MESSAGE);
	}
}
