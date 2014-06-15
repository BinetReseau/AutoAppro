package util;

import java.io.*;
import java.net.URL;

/** An easy download tool. */
public class HTTPDownload {
	private static final int BUFFER_SIZE = 256;

	/** Read the first line of a file through HTTP.
	 *
	 * @param urlString The URL of the file to read.
	 * @return The content of the first line of the file,
	 *   or <code>null</code> if an error happens.
	 */
	public static String readFirstLine(String urlString)
	{
		try {
			URL url = new URL(urlString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String result = reader.readLine();
			reader.close();
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	/** Download a file through HTTP.
	 *
	 * @param urlString The URL of the file to download.
	 * @param destFile The name of the file to write.
	 * @throws Exception If an error occurs.
	 */
	public static void download(String urlString, String destFile) throws Exception
	{
		URL url = new URL(urlString);
		BufferedInputStream in = new BufferedInputStream(url.openStream());
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
		byte buffer[] = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1)
			out.write(buffer, 0, bytesRead);
		out.close();
		in.close();
	}

	/** A testing function.
	 *
	 * @param args The command-line arguments.
	 */
	public static void main(String[] args) throws Exception
	{
		if (args.length == 0)
			System.err.println("Usage: java HTTPDownload url [file]");
		if (args.length > 1)
			download(args[0], args[1]);
		else
			System.out.println(readFirstLine(args[0]));
	}
}
