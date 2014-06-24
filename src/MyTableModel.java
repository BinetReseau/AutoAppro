import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import models.ProviderProduct;

@SuppressWarnings("serial")
public class MyTableModel extends AbstractTableModel
{
	private final ProviderProduct[] data;
	private final boolean[] dataSwitch;
	private final String[] columnNames;

	public MyTableModel(Collection<ProviderProduct> initialData)
	{
		data = new ProviderProduct[initialData.size()];
		dataSwitch = new boolean[initialData.size()];
		int i = 0;
		for (ProviderProduct p : initialData)
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
}
