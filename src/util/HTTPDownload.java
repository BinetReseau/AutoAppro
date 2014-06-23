package util;

import java.io.*;
import java.net.URL;
import java.util.concurrent.*;

/** An easy download tool. */
public class HTTPDownload {
	private static final int BUFFER_SIZE = 256;

	/** Read the first line of a file through HTTP.
	 *
	 * @param urlString The URL of the file to read.
	 * @param msTimeout The maximum number of millisecond for this function to terminate,
	 *   or <code>0</code> to indicate that no timeout shall be used.
	 * @return The content of the first line of the file,
	 *   or <code>null</code> if an error happens.
	 */
	public static String readFirstLine(String urlString, int msTimeout)
	{
		try {
			final URL url = new URL(urlString);
			if (msTimeout > 0)
			{
				ExecutorService executor = new ForkJoinPool(1);
				Future<String> result = executor.submit(new Callable<String>() {
					@Override
					public String call() throws Exception
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
						String result = reader.readLine();
						reader.close();
						return result;
					}
				});
				return result.get(msTimeout, TimeUnit.MILLISECONDS);
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String result = reader.readLine();
				reader.close();
				return result;
			}
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
			System.out.println(readFirstLine(args[0], 0));
	}
}
