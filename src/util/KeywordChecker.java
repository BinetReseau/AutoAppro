package util;

import java.text.Normalizer;

/** Class used to check many strings for keywords. */
public class KeywordChecker
{
	private String[] keywords;

	/** Construct the checker with the giver keywords.
	 *
	 * @param keywords The keywords for this checker.
	 */
	public KeywordChecker(String[] keywords)
	{
		this.keywords = new String[keywords.length];
		for (int i = 0; i < keywords.length; ++i)
		{
			/* We remove the accents */
			this.keywords[i] = Normalizer.normalize(keywords[i], Normalizer.Form.NFD)
					.replaceAll("[^\\p{ASCII}]", "").toUpperCase();
		}
	}

	/** Check a string against this checker.
	 *
	 * @param str The string value to check.
	 * @return <code>true</code> if all the keywords were found,
	 *   <code>false</code> otherwise.
	 */
	public boolean check(String str)
	{
		str = Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
		for (String keyword : keywords)
		{
			if (!str.contains(keyword))
				return false;
		}
		return true;
	}

	/** Count the number of keywords found in a string.
	 *
	 * @param str The string value to analyze.
	 * @return The number of keywords found in the string value.
	 */
	public int count(String str)
	{
		int result = 0;
		str = Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
		for (String keyword : keywords)
		{
			if (str.contains(keyword))
				++result;
		}
		return result;
	}
}
