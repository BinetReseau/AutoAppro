package updater;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import suppliers.Intermarche;
import util.HTTPDownload;
import util.LookAheadIS;

public class Updater implements Runnable
{
	private static JLabel label;
	private static String newState;
	
	private static final Runnable updateState = new Runnable() {
		@Override
		public void run()
		{
			label.setText(newState);
		}
	};
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				createAndShowGUI();
			}
		});
	}
	
	private static void createAndShowGUI()
	{
		JFrame window = new JFrame("AutoAppro updater");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		label = new JLabel("Please click on update to replace AutoAppro.jar with the new version.");
		window.getContentPane().add(label, BorderLayout.CENTER);
		JButton button = new JButton("Update");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				((JButton) evt.getSource()).setEnabled(false);
				(new Thread(new Updater())).start();
			}
		});
		window.getContentPane().add(button, BorderLayout.PAGE_END);
		window.pack();
		window.setVisible(true);
	}
	
	/* The updating function to adjust */
	private static void doUpdate() throws Exception
	{
		/* Remove preferences file (get rid of the provider word) */
		(new File("prefs.dat")).delete();
		/* Modify Intermarche file (get rid of the provider word) */
		String InterName = (new Intermarche()).getName();
		LookAheadIS in = null;
		OutputStream out = null;
		try {
			in = new LookAheadIS(new BufferedInputStream(new FileInputStream(InterName + ".dat")), 10);
			out = new BufferedOutputStream(new FileOutputStream(InterName + "_TMP.dat"));
			byte[] myStart = ("providerID").getBytes();
			while (!in.atEnd())
			{
				if (in.startsWith(myStart))
				{
					in.skip(10);
					out.write(("supplierID").getBytes());
				} else {
					out.write(in.read());
				}
			}
		} finally {
			if (out != null) out.close();
			if (in != null) in.close();
		}
		(new File(InterName + ".dat")).renameTo(new File(InterName + "_OLD.dat"));
		(new File(InterName + "_TMP.dat")).renameTo(new File(InterName + ".dat"));
		/* Update AutoAppro.jar */
		(new File("AutoAppro.jar")).delete();
		HTTPDownload.download(AutoAppro.AutoAppro.UPDATE_URL + "AutoAppro.jar", "AutoAppro.jar");
	}

	@Override
	public void run()
	{
		// TODO (for the next releases) Check the version in prefs.dat to see if it is the right one
		try {
			newState = "Updating ...";
			SwingUtilities.invokeAndWait(updateState);
		} catch (Exception e) {}
		try
		{
			doUpdate();
			try {
				newState = "Application successfully updated.";
				SwingUtilities.invokeAndWait(updateState);
			} catch (Exception e) {}
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		try {
			Runtime.getRuntime().exec("java -jar AutoAppro.jar");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to launch AutoAppro.jar; please re-launch it manually.",
					"Minor error", JOptionPane.ERROR_MESSAGE);
		}
		System.exit(0);
	}
}
