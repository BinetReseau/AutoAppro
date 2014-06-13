import java.io.Serializable;

/** The possible product types. */
enum ProductType {
	/** Free product. */ OPEN,
	/** Normal product. */ NORMAL,
	/** Constant quantity product (e.g. paid by weight, logged with number). */ CONSTANT_QTT,
	/** Ask the quantity.
	 * (e.g. fruits that are chosen by weight and logged with number of individual fruit). */ ASK_QTT,
	/** If you do not want to ask the exact number of fruits, take this option.
	 * You will also have to indicate the average number of weight by fruit. */ ROUND_QTT
}

/** The product class which holds the informations of a single product type. */
public class Product {
	/** The quantity type for this product. */
	public ProductType type;
	/** The quantity multiplier (if any). */
	public double mult;
	/** The ID for this product on the client end. */
	public int barID;
	/** The necessary information for the provider to identify this product. */
	public Serializable providerID;
}
