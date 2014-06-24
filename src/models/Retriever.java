package models;

import java.io.Serializable;

/** An abstract class that is used for informing the new products from the provider. */
public abstract class Retriever
{
	/** Add a product from its provider ID, quantity and price.
	 *
	 * @param providerID The provider ID for the product.
	 * @param quantity Total final quantity for this product (provider units).
	 * @param price Total price in cents.
	 * @see #addProduct(ProviderProduct)
	 */
	public abstract void addProduct(Serializable providerID, double quantity, int price);

	/** Add a product from a {@link ProviderProduct}.
	 *
	 * @param product The combination of the provider ID, quantity and price
	 *   of the product.
	 * @see #addProduct(Serializable, double, int)
	 */
	public void addProduct(ProviderProduct product)
	{
		this.addProduct(product.providerID, product.quantity, product.price);
	}
}
