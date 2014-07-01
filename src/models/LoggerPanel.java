package models;

import javax.swing.JPanel;

/** The panel to display when choosing the bar product. */
@SuppressWarnings("serial")
public abstract class LoggerPanel extends JPanel
{
	/** When the user validates the form, this should return the correct value.
	 *
	 * @return The bar ID for the product.
	 */
	public abstract int getBarID();

	/** When the user wants to give it a certain ID, perhaps indirectly.
	 *
	 * @param id The bar ID for this product.
	 */
	public abstract void setBarID(int id);
}
