package loggers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import AutoAppro.AutoAppro;
import util.MyPreferences;
import models.*;

/** A logger for the currently running version of the bar software.
 * <p>
 * Contrary to the {@link Bar2Manual} implementation, this one is automatic.
 */
public class Bar2Auto extends Logger
{
	private String bar, login, password;
	private HashMap<Integer, BarItem> items;

	private static class BarItem
	{
		String name;
		String defaultQtt;
		double price, quantity;
	}

	@Override
	public String log(Iterable<LogItem> items) throws Exception
	{
		for (BarItem item : this.items.values())
			item.price = item.quantity = 0;
		for (LogItem item : items)
		{
			BarItem currentItem = this.items.get(item.barID);
			if (currentItem == null)
				throw new Exception("Item not found (ID=" + item.barID + ").");
			currentItem.price += item.price;
			currentItem.quantity += item.quantity;
		}
		URL url = new URL("http://bar/" + this.bar + "/new-provision/do");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes("login=" + this.login + "&p455w0rd=" + this.password + "&provimoney_0=");
		for (Entry<Integer, BarItem> entry : this.items.entrySet())
		{
			int price = (int) entry.getValue().price;
			wr.writeBytes("&provimoney_" + entry.getKey() + "=" +
					(price / 100) + (price < 10 ? ".0" : ".") + (price % 100));
			wr.writeBytes("&proviqty_" + entry.getKey() + "=" + myDblToString(entry.getValue().quantity));
			wr.writeBytes("&proviqtybox_" + entry.getKey() + "=1");
		}
		wr.writeBytes("&btn_provision=Valide ton appro !");
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		if (responseCode != 200)
			System.out.println("Response: " + responseCode);
		return null;
	}

	private static String myDblToString(double value)
	{
		int approx = (int) value;
		double remaining = value - approx;
		if (remaining < 0.0001) return Integer.toString(approx);
		if (remaining > 0.9999) return Integer.toString(approx + 1);
		return Double.toString(value);
	}

	@Override
	public String getName()
	{
		return "Site bars 2.0 beta - Auto";
	}

	@Override
	public void initialize()
	{
		Serializable bar = MyPreferences.get(this.getName() + ".bar");
		if (bar == null)
		{
			bar = JOptionPane.showInputDialog(null, AutoAppro.messages.getString("bar2auto_bar_title"),
					AutoAppro.messages.getString("bar2auto_bar_content"), JOptionPane.QUESTION_MESSAGE);
			if (bar == null)
			{
				System.exit(0);
				return;
			}
			MyPreferences.set(this.getName() + ".bar", bar);
		}
		this.bar = (String) bar;
		Serializable login = MyPreferences.get(this.getName() + ".login");
		if (login == null)
		{
			login = JOptionPane.showInputDialog(null, AutoAppro.messages.getString("bar2auto_login_title"),
					AutoAppro.messages.getString("bar2auto_login_content"), JOptionPane.QUESTION_MESSAGE);
			if (login == null)
			{
				System.exit(0);
				return;
			}
		}
		this.login = (String) login;
		Serializable password = MyPreferences.get(this.getName() + ".password");
		if (password == null)
		{
			password = JOptionPane.showInputDialog(null, AutoAppro.messages.getString("bar2auto_passwd_title"),
					AutoAppro.messages.getString("bar2auto_passwd_content"), JOptionPane.QUESTION_MESSAGE);
			if (password == null)
			{
				System.exit(0);
				return;
			}
		} else {
			byte[] data = (byte[]) password;
			try {
				/* Yeah, this is not great, but we hope that those who read this line
				 * will remain wise ... */
				password = (new BufferedReader(new InputStreamReader(new GZIPInputStream(
						new ByteArrayInputStream(data))))).readLine();
			} catch (IOException e) {
				password = null;
			}
		}
		this.password = (String) password;
		items = new HashMap<Integer, BarItem>(2048);
		BufferedReader in = null;
		try {
			URL url = new URL("http://bar/" + this.bar + "/new-provision");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			String urlParameters = "login=" + this.login + "&p455w0rd=" + this.password;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200)
				throw new Exception("Response code: " + responseCode);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while (!(line = in.readLine()).contains("mat&eacute;riel pour le bar"))
			{
				if (line.contains("Utilisateur ou mot de passe incorrect."))
				{
					JOptionPane.showMessageDialog(null, AutoAppro.messages.getString("bar2auto_error_passwd"),
							AutoAppro.messages.getString("common_error"), JOptionPane.ERROR_MESSAGE);
					MyPreferences.set(this.getName() + ".bar", null);
					MyPreferences.set(this.getName() + ".login", null);
					MyPreferences.set(this.getName() + ".password", null);
					System.exit(0);
				}
				if (line.contains("Utilisateur inconnu."))
				{
					JOptionPane.showMessageDialog(null, AutoAppro.messages.getString("bar2auto_error_login"),
							AutoAppro.messages.getString("common_error"), JOptionPane.ERROR_MESSAGE);
					MyPreferences.set(this.getName() + ".bar", null);
					MyPreferences.set(this.getName() + ".login", null);
					MyPreferences.set(this.getName() + ".password", null);
					System.exit(0);
				}
			}
			while (!(line = in.readLine()).startsWith("<td colspan=\"5\""))
			{
				int index = line.indexOf("provimoney_");
				if (index == -1)
					continue;
				BarItem item = new BarItem();
				int index2 = (index += 11);
				while (Character.isDigit(line.charAt(index2)))
					++index2;
				int id = Integer.parseInt(line.substring(index, index2));
				while (!in.readLine().startsWith("<option value=\"1\""));
				item.defaultQtt = in.readLine();
				while ((index = (line = in.readLine()).indexOf("style=\"font-weight:bold;\">"))== -1);
				index += 26;
				item.name = line.substring(index, line.length() - 5);
				items.put(id, item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			if (in != null)
			{
				try {
					in.close();
				} catch (IOException e) { }
			}
		}
	}

	@Override
	public LoggerPanel getLoggerPanel(boolean isNew, int defaultID, String providerName)
	{
		// TODO
		return null;
	}
}
