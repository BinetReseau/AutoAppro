package models;

import java.io.Serializable;

/** An abstract class that is used for informing the new products from the supplier. */
public abstract class Retriever
{
	/** Add a product from its supplier ID, quantity and price.
	 *
	 * @param supplierID The supplier ID for the product.
	 * @param quantity Total final quantity for this product (supplier units).
	 * @param price Total price in cents.
	 * @see #addProduct(SupplierProduct)
	 */
	public abstract void addProduct(Serializable supplierID, double quantity, int price);

	/** Add a product from a {@link SupplierProduct}.
	 *
	 * @param product The combination of the supplier ID, quantity and price
	 *   of the product.
	 * @see #addProduct(Serializable, double, int)
	 */
	public void addProduct(SupplierProduct product)
	{
		this.addProduct(product.supplierID, product.quantity, product.price);
	}
}
