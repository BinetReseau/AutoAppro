package AutoAppro;

import java.io.Serializable;

/** The possible product types. */
enum ProductType
{
	/** Free product. */ OPEN,
	/** Normal product (mult used). */ NORMAL,
	/** Constant quantity product (e.g. paid by weight, logged with number) (mult used). */ CONSTANT_QTT,
	/** Ask the quantity.
	 * (e.g. fruits that are chosen by weight and logged with number of individual fruit) (mult used). */ ASK_QTT,
	/** If you do not want to ask the exact number of fruits, take this option.
	 * You will also have to indicate the average number of weight by fruit (mult used before roundup). */ ROUND_QTT,
	/** Quantity is price (in cents) (mult used). */ QTT_PRICE
}

/** The product class which holds the informations of a single product type. */
public class Product implements Serializable, Comparable<Product>
{
	private static final long serialVersionUID = 4291170913868609146L;

	/** The quantity type for this product. */
	public ProductType type;
	/** The quantity multiplier (if any). */
	public double mult;
	/** The ID for this product on the client end. */
	public int barID;
	/** The necessary information for the provider to identify this product. */
	public Serializable providerID;

	@Override
	public String toString()
	{
		return providerID.toString();
	}

	/* Use this comparison method only for display purposes. */
	@Override
	public int compareTo(Product other)
	{
		return providerID.toString().compareTo(other.providerID.toString());
	}
}
