package models;

import java.io.Serializable;

/** An optional class that may reveal useful for combining the information of a product */
public class SupplierProduct
{
	/** The supplier ID for the product. */
	public Serializable supplierID;
	/** Total final quantity for this product (supplier units). */
	public double quantity;
	/** Total price in cents. */
	public int price;
}
