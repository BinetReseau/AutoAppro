package updater;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

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

	@Override
	public void run()
	{
		// TODO
		endUpdate();
	}
	
	public static void endUpdate()
	{
		try {
			newState = "Application successfully updated.";
			SwingUtilities.invokeAndWait(updateState);
		} catch (Exception e) {}
		try {
			Runtime.getRuntime().exec("java -jar AutoAppro.jar");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to launch AutoAppro.jar; please re-launch it manually.",
					"Minor error", JOptionPane.ERROR_MESSAGE);
		}
		System.exit(0);
	}
}
