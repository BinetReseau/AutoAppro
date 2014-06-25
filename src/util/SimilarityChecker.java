package util;

/** Class used to check whether two strings are similar or not. */
public class SimilarityChecker {
	/** Check whether two strings are similar or not.
	 *
	 * @param a The first string to compare.
	 * @param b The second string to compare.
	 * @param similarityAcceptError The Levenshtein maximal error.
	 * @return <code>true</code> if they are similar, <code>false</code> otherwise.
	 */
	public static boolean isSimilar(String a, String b, int similarityAcceptError)
	{
		a = a.toUpperCase();
		b = b.toUpperCase();
		if (a.equals(b)) return true;
		return levenshteinOK(a, b, a.length() - 1, b.length() - 1, similarityAcceptError);
	}

	private static boolean levenshteinOK(String a, String b, int aOffset, int bOffset, int max)
	{
		if (aOffset < 0) return (bOffset <= max);
		if (bOffset < 0) return (aOffset <= max);
		if (a.charAt(aOffset) == b.charAt(bOffset))
			return levenshteinOK(a, b, aOffset - 1, bOffset - 1, max);
		if (max == 0) return false;
		if (levenshteinOK(a, b, aOffset - 1, bOffset, max - 1))
			return true;
		if (levenshteinOK(a, b, aOffset, bOffset - 1, max - 1))
			return true;
		if (levenshteinOK(a, b, aOffset - 1, bOffset - 1, max - 1))
			return true;
		return false;
	}

	/** Check whether two strings are similar or not, variation.
	 * <p>
	 * Here, the deletions due to the difference of lengths of the two strings
	 * are not taken into account in the processing of the Levenshtein distance.
	 *
	 * @param a The first string to compare.
	 * @param b The second string to compare.
	 * @param similarityAcceptError The Levenshtein maximal error.
	 * @return <code>true</code> if they are similar, <code>false</code> otherwise.
	 */
	public static boolean isSimilarDel(String a, String b, int similarityAcceptError)
	{
		a = a.toUpperCase();
		b = b.toUpperCase();
		if (a.equals(b)) return true;
		return levenshteinDelOK(a, b, a.length() - 1, b.length() - 1, similarityAcceptError,
				Math.min(0, a.length() - b.length()), Math.min(0, b.length() - a.length()));
	}

	private static boolean levenshteinDelOK(String a, String b, int aOffset, int bOffset, int max, int delA, int delB)
	{
		if (aOffset < 0) return (bOffset - delB <= max - delA);
		if (bOffset < 0) return (aOffset - delA <= max - delB);
		if (a.charAt(aOffset) == b.charAt(bOffset))
			return levenshteinDelOK(a, b, aOffset - 1, bOffset - 1, max, delA, delB);
		if ((max + Math.min(delA, aOffset + 1) + Math.min(delB, bOffset + 1)) <= 0) return false;
		if (levenshteinDelOK(a, b, aOffset - 1, bOffset, max, delA - 1, delB))
			return true;
		if (levenshteinDelOK(a, b, aOffset, bOffset - 1, max, delA, delB - 1))
			return true;
		if (levenshteinDelOK(a, b, aOffset - 1, bOffset - 1, max - 1, delA, delB))
			return true;
		return false;
	}
}
