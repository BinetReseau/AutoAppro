package AutoAppro;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import models.*;

/** The model for the table of the main window. */
@SuppressWarnings("serial")
public class MyTableModel extends AbstractTableModel
{
	private final ProviderProduct[] data;
	private final boolean[] dataSwitch;
	private final String[] columnNames;

	public MyTableModel(Collection<ProviderProduct> initialData)
	{
		ArrayList<ProviderProduct> sortedData = new ArrayList<ProviderProduct>(initialData);
		Collections.sort(sortedData, new Comparator<ProviderProduct>() {
			@Override
			public int compare(ProviderProduct o1, ProviderProduct o2)
			{
				return o1.providerID.toString().compareTo(o2.providerID.toString());
			}
		});
		data = new ProviderProduct[sortedData.size()];
		dataSwitch = new boolean[sortedData.size()];
		int i = 0;
		for (ProviderProduct p : sortedData)
		{
			data[i] = p;
			dataSwitch[i] = true;
			++i;
		}
		columnNames = new String[4];
		for (i = 0; i < 4; ++i)
			columnNames[i] = AutoAppro.messages.getString("column_name_" + i);
	}

	@Override
	public int getColumnCount()
	{
		return 4;
	}

	@Override
	public int getRowCount()
	{
		return data.length;
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		switch (col)
		{
		case 0:
			return new Boolean(dataSwitch[row]);
		case 1:
			return data[row].providerID.toString();
		case 2:
			return new Double(data[row].quantity);
		case 3:
			return new Double(data[row].price / 100.0);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int col)
	{
		switch (col)
		{
		case 0:
			return Boolean.class;
		case 1:
			return String.class;
		case 2:
		case 3:
			return Double.class;
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		switch (col)
		{
		case 0:
		case 2:
		case 3:
			return true;
		}
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
		switch (col)
		{
		case 0:
			dataSwitch[row] = (boolean) value;
			break;
		case 2:
			data[row].quantity = (double) value;
			break;
		case 3:
			data[row].price = (int) Math.round(100.0 * ((double) value));
			break;
		}
		fireTableCellUpdated(row, col);
	}

	/** Log all the items.
	 *
	 * @return <code>null</code> if the logger is automatic,
	 *   or the result string to put in the clip-board.
	 * @throws Exception If an error occurs.
	 */
	public String log() throws Exception
	{
		ArrayList<LogItem> toLog = new ArrayList<LogItem>(data.length);
		LogItem toAdd;
		for (int i = 0; i < data.length; ++i)
		{
			if (dataSwitch[i])
			{
				toAdd = new LogItem();
				toAdd.barID = AutoAppro.products.get(data[i].providerID).barID;
				toAdd.price = data[i].price / 100.0;
				toAdd.quantity = data[i].quantity;
				toLog.add(toAdd);
			}
		}
		return AutoAppro.logger.log(toLog);
	}
}
