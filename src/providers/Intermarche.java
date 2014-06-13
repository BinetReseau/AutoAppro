package providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.*;

import models.*;

public class Intermarche extends Provider
{
	private static final int INITIAL_HASHMAP_SIZE = 4096;
	private static final int SIMILARITY_ERR_ACCEPT = 3;
	private static final Pattern pricePattern, qttPattern;

	static {
		pricePattern = Pattern.compile("(\\d+)[,.](\\d{2}) €");
		qttPattern = Pattern.compile("(\\d+(?:[,.]\\d+)?)(?:\\w*k?g)?");
	}

	private HashMap<String, LinkedList<ProviderProduct>> products;

	public Intermarche()
	{
		products = new HashMap<String, LinkedList<ProviderProduct>>(INITIAL_HASHMAP_SIZE);
	}

	@Override
	public boolean tryAutomaticRetrieve()
	{
		return false;
	}

	@Override
	public void retrieveFromString(String data)
	{
		/* Get rid of the useless headers and footers */
		int start = data.indexOf("Total TTC");
		if (start == -1) start = 0;
		else start += 9;
		int end = data.indexOf("Sous-total", start);
		if (end == -1) end = data.length();
		data = data.substring(start, end);
		/* Parse the interior */
		BufferedReader reader = new BufferedReader(new StringReader(data));
		String line, elements[];
		try {
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				if (line.isEmpty()) continue;
				elements = line.split("\t");
				if (elements.length != 4)
				{
					if (line.startsWith("$RESX_")) continue;
					if (line.startsWith("Le prix total unitaire")) continue;
					throw new IllegalArgumentException("Wrong line (need 4 elements) : " + line);
				}
				/* Retrieve the information for the current product */
				ProviderProduct current = new ProviderProduct();
				Matcher matcher;
				matcher = pricePattern.matcher(elements[3].trim());
				if (!matcher.matches())
					throw new IllegalArgumentException("Bad price : " + line);
				current.price = Integer.parseInt(matcher.group(1)) * 100 +
						Integer.parseInt(matcher.group(2));
				matcher = qttPattern.matcher(elements[2].trim());
				if (!matcher.matches())
					throw new IllegalArgumentException("Bad quantity : " + line);
				current.quantity = Double.parseDouble(matcher.group(1));
				elements[0] = elements[0].trim();
				current.providerID = elements[0];
				/* Append the result to the list in the hash map */
				LinkedList<ProviderProduct> list = products.get(elements[0]);
				if (list == null)
				{
					list = new LinkedList<ProviderProduct>();
					products.put(elements[0], list);
				}
				list.add(current);
			}
			reader.close();
		} catch (IOException e) { }
	}

	@Override
	public boolean useMissingList()
	{
		return true;
	}

	@Override
	public void retrieveMissing(String data)
	{
		/* Get rid of the useless headers and footers */
		int start = data.indexOf("Préparé");
		if (start == -1) start = 0;
		else start += 7;
		int end = data.indexOf("Votre magasin,", start);
		if (end == -1) end = data.length();
		data = data.substring(start, end);
		/* Parse the interior */
		BufferedReader reader = new BufferedReader(new StringReader(data));
		String line, elements[];
		try {
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				if (line.isEmpty()) continue;
				if (line.startsWith("Ce produit")) continue;
				elements = line.split("\t");
				if (elements.length != 5)
					throw new IllegalArgumentException("Wrong line (need 5 elements) : " + line);
				/* Retrieve the information for the current product */
				Matcher matcher = qttPattern.matcher(elements[3].trim());
				if (!matcher.matches())
					throw new IllegalArgumentException("Bad quantity (3) : " + line);
				double startQtt = Double.parseDouble(matcher.group(1));
				matcher = qttPattern.matcher(elements[4].trim());
				if (!matcher.matches())
					throw new IllegalArgumentException("Bad quantity (4) : " + line);
				double endQtt = Double.parseDouble(matcher.group(1));
				/* We try to find the same product */
				String simKey = elements[1].toUpperCase() + " - " + elements[2];
				LinkedList<ProviderProduct> list = null;
				for (Map.Entry<String, LinkedList<ProviderProduct>> entry : products.entrySet())
				{
					if (isSimilar(entry.getKey(), simKey))
					{
						list = entry.getValue();
						break;
					}
				}
				if (list == null)
					throw new IllegalArgumentException("Unknown product : " + simKey);
				/* First, we also try to find the same quantity, else we take the first element */
				ProviderProduct myItem = list.getFirst();
				for (ProviderProduct item : list)
				{
					if (item.quantity == startQtt)
					{
						myItem = item;
						break;
					}
				}
				/* And we modify the quantity */
				if (endQtt == 0)
				{
					list.remove(myItem);
				} else {
					myItem.price = (int) (myItem.price / startQtt * endQtt);
					myItem.quantity = endQtt;
				}
			}
			reader.close();
		} catch (IOException e) { }
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

	private static boolean isSimilar(String a, String b)
	{
		a = a.toUpperCase();
		b = b.toUpperCase();
		if (a.equals(b)) return true;
		//return levenshteinOK(a, b, a.length() - 1, b.length() - 1, SIMILARITY_ERR_ACCEPT);
		return false;
	}

	@Override
	public void getItems(Retriever retriever)
	{
		for (LinkedList<ProviderProduct> list : products.values())
		{
			for (ProviderProduct p : list)
				retriever.addProduct(p);
		}
	}
}
