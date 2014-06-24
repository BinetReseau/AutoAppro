package util;

import java.awt.Toolkit;
import java.awt.datatransfer.*;

/** A simple class to handle text writing and reading in clip-board. */
public class MyClipBoard {
	/** Get the clip-board text.
	 *
	 * @return The clip-board text, or <code>null</code> if there is none.
	 * @throws Exception If an error occurs.
	 */
	public static String getClipboardText() throws Exception
	{
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if ((t == null) || (!t.isDataFlavorSupported(DataFlavor.stringFlavor)))
			return null;
		return (String) t.getTransferData(DataFlavor.stringFlavor);
	}

	/** Set the clip-board text.
	 *
	 * @param newValue The new text value.
	 * @throws Exception If an error occurs.
	 */
	public static void setClipboardText(String newValue) throws Exception
	{
		StringSelection ss = new StringSelection(newValue);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
}
