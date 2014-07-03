package util;

import java.util.HashMap;
import java.util.Scanner;

/** HTML4 characters escaper. */
public class HTML4Unescape
{
	private static final HashMap<String, Integer> knownEscapes = new HashMap<String, Integer>(1024);

	/** A test function.
	 * <p>
	 * Read a word on the standard input and returns it unescaped on the standard output.
	 *
	 * @param args The program arguments (useless).
	 */
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.println(unescapeHTML4(in.next()));
		in.close();
	}

	/** Unescape a string containing entity escapes to a string containing the actual Unicode characters corresponding to the escapes.
	 * <p>
	 * This is a replacement for the <code>unescapeHtml4</code> function in <code>org.apache.commons.lang3.StringEscapeUtils</code>.
	 *
	 * @param str The string to unescape.
	 * @return A new unescaped String, <code>null</code> if null string input
	 */
	public static String unescapeHTML4(String str)
	{
		if (str == null)
			return null;
		int index = 0;
		Integer cCode;
		while ((index = str.indexOf('&', index)) != -1)
		{
			int index2 = str.indexOf(';', index + 1);
			if (index2 == -1)
				return str;
			if (str.charAt(index + 1) == '#')
			{
				int result = 0;
				try {
					if (str.charAt(index + 2) == 'x')
					{
						for (int i = index + 3; i < index2; ++i)
							result = (result << 4) | hexValue(str.charAt(i));
					} else {
						result = Integer.parseInt(str.substring(index + 2, index2));
					}
				} catch (NumberFormatException e) {
					index = index2;
					continue;
				}
				str = str.substring(0, index) + Character.toString((char) result) + str.substring(index2 + 1);
				++index;
			} else if ((cCode = knownEscapes.get(str.substring(index + 1, index2))) != null)
			{
				str = str.substring(0, index) + Character.toString((char) ((int) cCode)) + str.substring(index2 + 1);
				++index;
			} else {
				index = index2 + 1;
			}
		}
		return str;
	}

	private static int hexValue(char c)
	{
		if ((c >= '0') && (c <= '9'))
			return (int) (c - '0');
		if ((c >= 'A') && (c <= 'F'))
			return ((int) (c - 'A')) + 0x0A;
		throw new NumberFormatException();
	}

	static {
		knownEscapes.put("nbsp", 32);
		knownEscapes.put("lt", 60);
		knownEscapes.put("gt", 62);
		knownEscapes.put("quot", 34);
		knownEscapes.put("amp", 38);
		knownEscapes.put("AElig", 198);
		knownEscapes.put("Aacute", 193);
		knownEscapes.put("Acirc", 194);
		knownEscapes.put("Agrave", 192);
		knownEscapes.put("Aring", 197);
		knownEscapes.put("Atilde", 195);
		knownEscapes.put("Auml", 196);
		knownEscapes.put("Ccedil", 199);
		knownEscapes.put("ETH", 208);
		knownEscapes.put("Eacute", 201);
		knownEscapes.put("Ecirc", 202);
		knownEscapes.put("Egrave", 200);
		knownEscapes.put("Euml", 203);
		knownEscapes.put("Iacute", 205);
		knownEscapes.put("Icirc", 206);
		knownEscapes.put("Igrave", 204);
		knownEscapes.put("Iuml", 207);
		knownEscapes.put("Ntilde", 209);
		knownEscapes.put("Oacute", 211);
		knownEscapes.put("Ocirc", 212);
		knownEscapes.put("Ograve", 210);
		knownEscapes.put("Oslash", 216);
		knownEscapes.put("Otilde", 213);
		knownEscapes.put("Ouml", 214);
		knownEscapes.put("THORN", 222);
		knownEscapes.put("Uacute", 218);
		knownEscapes.put("Ucirc", 219);
		knownEscapes.put("Ugrave", 217);
		knownEscapes.put("Uuml", 220);
		knownEscapes.put("Yacute", 221);
		knownEscapes.put("aacute", 225);
		knownEscapes.put("acirc", 226);
		knownEscapes.put("aelig", 230);
		knownEscapes.put("agrave", 224);
		knownEscapes.put("aring", 229);
		knownEscapes.put("atilde", 227);
		knownEscapes.put("auml", 228);
		knownEscapes.put("ccedil", 231);
		knownEscapes.put("eacute", 233);
		knownEscapes.put("ecirc", 234);
		knownEscapes.put("egrave", 232);
		knownEscapes.put("eth", 240);
		knownEscapes.put("euml", 235);
		knownEscapes.put("iacute", 237);
		knownEscapes.put("icirc", 238);
		knownEscapes.put("igrave", 236);
		knownEscapes.put("iuml", 239);
		knownEscapes.put("ntilde", 241);
		knownEscapes.put("oacute", 243);
		knownEscapes.put("ocirc", 244);
		knownEscapes.put("ograve", 242);
		knownEscapes.put("oslash", 248);
		knownEscapes.put("otilde", 245);
		knownEscapes.put("ouml", 246);
		knownEscapes.put("szlig", 223);
		knownEscapes.put("thorn", 254);
		knownEscapes.put("uacute", 250);
		knownEscapes.put("ucirc", 251);
		knownEscapes.put("ugrave", 249);
		knownEscapes.put("uuml", 252);
		knownEscapes.put("yacute", 253);
		knownEscapes.put("yuml", 255);
		knownEscapes.put("cent", 162);
		knownEscapes.put("OElig", 0x0152);
		knownEscapes.put("oelig", 0x0153);
		knownEscapes.put("euro", 0x20AC);
	}
}
