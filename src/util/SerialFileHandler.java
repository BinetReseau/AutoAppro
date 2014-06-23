package util;

import java.io.*;

/** A static-usage class for writing and reading objects into files. */
public class SerialFileHandler {
	/** Write an object into a file.
	 *
	 * @param object The object to write.
	 * @param filename The name of the file to write to.
	 * @return <code>true</code> in case of success, <code>false</code> if an error occurred.
	 */
	public static boolean writeObject(Object object, String filename)
	{
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			out.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null)
			{
				try { out.close(); } catch (IOException e) { }
			}
		}
		return true;
	}

	/** Read an object from a file.
	 *
	 * @param filename The name of the file to read the object from.
	 * @return The object itself, or <code>null</code> if an error happened.
	 */
	public static Object readObject(String filename)
	{
		ObjectInputStream in = null;
		Object result = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
			result = in.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
			{
				try { in.close(); } catch (IOException e) { }
			}
		}
		return result;
	}
}
