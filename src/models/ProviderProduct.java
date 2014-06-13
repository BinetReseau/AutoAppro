package models;

import java.io.Serializable;

/** An optional class that may reveal useful for combining the information of a product */
public class ProviderProduct
{
	/** The provider ID for the product. */
	public Serializable providerID;
	/** Total final quantity for this product (provider units). */
	public double quantity;
	/** Total price in cents. */
	public int price;
}
