package loggers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.*;

import AutoAppro.AutoAppro;
import util.*;
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
		KeywordChecker checker;
	}

	private static class ChoiceItem implements Comparable<ChoiceItem>
	{
		BarItem item;
		int id;
		int cmpValue;

		@Override
		public int compareTo(ChoiceItem other)
		{
			if ((cmpValue == 0) && (other.cmpValue == 0))
				return item.name.compareTo(other.item.name);
			return other.cmpValue - cmpValue;
		}

		@Override
		public String toString()
		{
			return item.name;
		}
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
		URL url = new URL("http://bar.eleves.polytechnique.fr/" + this.bar + "/new-provision/do");
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

	private String getPassword()
	{
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(AutoAppro.messages.getString("bar2auto_passwd_content"));
		JPasswordField pass = new JPasswordField();
		panel.add(label, BorderLayout.PAGE_START);
		panel.add(pass, BorderLayout.CENTER);
		int valid = JOptionPane.showConfirmDialog(null, panel, AutoAppro.messages.getString("bar2auto_passwd_title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (valid != JOptionPane.OK_OPTION)
			return null;
		return new String(pass.getPassword());
	}

	@Override
	public void initialize()
	{
		/* Get the bar name */
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
		/* Get the login */
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
		/* Get the password */
		Serializable password = MyPreferences.get(this.getName() + ".password");
		if (password == null)
		{
			password = getPassword();
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
		/* Get the list of items */
		items = new HashMap<Integer, BarItem>(2048);
		BufferedReader in = null;
		try {
			URL url = new URL("http://bar.eleves.polytechnique.fr/" + this.bar + "/new-provision");
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
				if (line.contains("administrateur pour rentrer une appro"))
				{
					JOptionPane.showMessageDialog(null, AutoAppro.messages.getString("bar2auto_error_admin"),
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
				item.defaultQtt = HTML4Unescape.unescapeHTML4(in.readLine());
				while ((index = (line = in.readLine()).indexOf("style=\"font-weight:bold;\">"))== -1);
				index += 26;
				item.name = HTML4Unescape.unescapeHTML4(line.substring(index, line.length() - 5));
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
		/* Get the keywords for each item */
		in = null;
		try {
			URL url = new URL("http://bar.eleves.polytechnique.fr/" + this.bar + "/aliments");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode != 200)
				throw new Exception("Response code: " + responseCode);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while (!in.readLine().equals("<th>Mots cl&eacute;s</th>"));
			while (!(line = in.readLine()).equals("</table>"))
			{
				if (!line.startsWith("<tr"))
					continue;
				for (int nbCol = 0; nbCol < 5; ++nbCol)
					while (!in.readLine().startsWith("<td"));
				line = in.readLine();
				int index = line.indexOf("aliments/");
				if (index == -1)
					throw new Exception("\"aliments/\" not found in keywords link");
				index = Integer.parseInt(line.substring(index + 9, line.length() - 2));
				KeywordChecker currentChecker = new KeywordChecker(in.readLine().split("\\s+"));
				items.get(index).checker = currentChecker;
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
	public LoggerPanel getLoggerPanel(int defaultID, String providerName)
	{
		final Vector<ChoiceItem> data = new Vector<ChoiceItem>(items.size());
		for (Entry<Integer, BarItem> item : items.entrySet())
		{
			ChoiceItem toAdd = new ChoiceItem();
			toAdd.id = item.getKey();
			toAdd.item = item.getValue();
			toAdd.cmpValue = item.getValue().checker.count(providerName);
			data.add(toAdd);
		}
		Collections.sort(data);
		final JLabel infoLabel = new JLabel(data.get(0).item.defaultQtt);
		final JComboBox<ChoiceItem> combo = new JComboBox<ChoiceItem>(data);
		combo.setSelectedIndex(0);
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				infoLabel.setText(((ChoiceItem) combo.getSelectedItem()).item.defaultQtt);
			}
		});
		@SuppressWarnings("serial")
		LoggerPanel loggerPanel = new LoggerPanel() {
			@Override
			public void setBarID(int id)
			{
				for (ChoiceItem item : data)
				{
					if (item.id == id)
					{
						combo.setSelectedItem(item);
						infoLabel.setText(item.item.defaultQtt);
						return;
					}
				}
				System.err.println("Corrupt data (Bar2Auto.getLoggerPanel -> setBarID)");
			}
			@Override
			public int getBarID()
			{
				return ((ChoiceItem) combo.getSelectedItem()).id;
			}
		};
		loggerPanel.setLayout(new BoxLayout(loggerPanel, BoxLayout.Y_AXIS));
		JPanel comboPanel = new JPanel(new BorderLayout(5, 0));
		comboPanel.add(new JLabel(AutoAppro.messages.getString("bar2auto_type")), BorderLayout.LINE_START);
		comboPanel.add(combo, BorderLayout.CENTER);
		loggerPanel.add(comboPanel);
		JPanel infoPanel = new JPanel(new BorderLayout(5,  0));
		infoPanel.add(new JLabel(AutoAppro.messages.getString("bar2auto_info")), BorderLayout.LINE_START);
		infoPanel.add(infoLabel, BorderLayout.CENTER);
		loggerPanel.add(infoPanel);
		return loggerPanel;
	}

	@Override
	public void askSettings(JFrame parent)
	{
		String bar = JOptionPane.showInputDialog(null, AutoAppro.messages.getString("bar2auto_bar_title"),
				AutoAppro.messages.getString("bar2auto_bar_content"), JOptionPane.QUESTION_MESSAGE);
		if (bar == null)
			return;
		this.bar = bar;
		MyPreferences.set(this.getName() + ".bar", bar);
		String login = JOptionPane.showInputDialog(null, AutoAppro.messages.getString("bar2auto_login_title"),
				AutoAppro.messages.getString("bar2auto_login_content"), JOptionPane.QUESTION_MESSAGE);
		if (login == null)
			return;
		this.login = login;
		MyPreferences.set(this.getName() + ".login", login);
		String password = getPassword();
		if (password == null)
			return;
		this.password = password;
		ByteArrayOutputStream encoded = new ByteArrayOutputStream();
		try
		{
			OutputStream out = new GZIPOutputStream(encoded);
			out.write((password + "\n\n").getBytes());
			out.flush();
			out.close();
			MyPreferences.set(this.getName() + ".password", encoded.toByteArray());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
