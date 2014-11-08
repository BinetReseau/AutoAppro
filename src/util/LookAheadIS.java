package util;

import java.io.IOException;
import java.io.InputStream;

/** Some kind of BufferedInputStream with a look-ahead capability. */
public class LookAheadIS extends InputStream
{
	private final InputStream in;
	private final byte[] buffer;
	private int bsize, offset;
	
	/** Construct the InputStream.
	 *
	 * @param in The initial InputStream.
	 * @param size The size of the buffer in bytes.
	 * @throws IOException If some IOException happens when reading from <code>in</code>.
	 */
	public LookAheadIS(InputStream in, int size) throws IOException
	{
		this.in = in;
		buffer = new byte[size];
		for (bsize = 0; bsize < size; ++bsize)
		{
			int read = in.read();
			if (read == -1)
				break;
			buffer[bsize] = (byte) read;
		}
		offset = 0;
	}
	
	@Override
	public synchronized int read() throws IOException
	{
		if (bsize <= 0) return -1;
		int returnValue = buffer[offset];
		if (bsize == buffer.length)
		{
			int read = in.read();
			if (read != -1)
				buffer[bsize] = (byte) read;
			else --bsize;
		} else {
			--bsize;
		}
		offset = (++offset) % buffer.length;
		return returnValue;
	}
	
	/** Skip <code>n</code> bytes of data without reading them.
	 * <p>
	 * If the number of remaining bytes if lower than <code>n</code>,
	 * only these bytes are skipped, and no exception is thrown.
	 *
	 * @param n The number of bytes to skip.
	 * @throws IOException If some IOException happens when reading from the initial InputStream.
	 */
	public synchronized void skip(int n) throws IOException
	{
		for (int i = 0; i < n; ++i)
			read();
	}
	
	/** Check if the next bytes correspond to the given sequence of bytes.
	 * 
	 * @param seq The comparison sequence of bytes.
	 * @return <code>true</code> if the next bytes to be read start with <code>seq</code>,
	 *     <code>false</code> otherwise.
	 */
	public synchronized boolean startsWith(byte[] seq)
	{
		if (seq.length > buffer.length)
			throw new IllegalArgumentException();
		if (seq.length > bsize)
			return false;
		for (int i = 0; i < seq.length; ++i)
		{
			if (seq[i] != buffer[(offset + i) % buffer.length])
				return false;
		}
		return true;
	}
}
