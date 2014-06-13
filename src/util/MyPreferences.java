package util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/** An easy way to handle preferences. */
public class MyPreferences {
	private static final String PREF_FILENAME = "prefs.dat";

	private static final HashMap<String, Serializable> data = new HashMap<String, Serializable>();
	private static boolean modified;
	static {
		File prefFile = new File(PREF_FILENAME);
		ObjectInputStream in = null;
		String key;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(prefFile)));
			while (in.readBoolean())
			{
				key = (String) in.readObject();
				data.put(key, (Serializable) in.readObject());
			}
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) { }
		}
		modified = false;
	}

	/** Get a preference record from its ID key.
	 *
	 * @param key The ID key of the element to retrieve.
	 * @return The element corresponding to this key,
	 *     or <code>null</code> if there is none recorded.
	 */
	public static Serializable get(String key)
	{
		synchronized (data)
		{
			return data.get(key);
		}
	}

	/** Set a new preference record or override its previous value.
	 * <p>
	 * Note that this function only affects the current run of the program :
	 * you have to call {@link} to save all the new records
	 * to the preferences file.
	 *
	 * @param key The ID key for the new element.
	 * @param value The value for the new element.
	 */
	public static void set(String key, Serializable value)
	{
		synchronized (data)
		{
			Serializable old = data.put(key, value);
			if (((old == null) && (value != null)) || ((old != null) && (!old.equals(value))))
				modified = true;
		}
	}

	/** Save all the new records to the preferences file. */
	public static void save()
	{
		synchronized (data)
		{
			if (!modified) return;
			File prefFile = new File(PREF_FILENAME);
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(prefFile)));
				for (Map.Entry<String, Serializable> recordItem : data.entrySet())
				{
					out.writeBoolean(true);
					out.writeObject(recordItem.getKey());
					out.writeObject(recordItem.getValue());
				}
				out.writeBoolean(false);
				out.flush();
			} catch (IOException e) {
			} finally {
				try {
					if (out != null) out.close();
				} catch (IOException e) { }
			}
			modified = false;
		}
	}
}
