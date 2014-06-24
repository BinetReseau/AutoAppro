import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import util.*;

/** The main window handler. */
public class MainWindow
{
	private enum Status
	{
		WAITING_CONTENT,
		WAITING_MISSING,
		WAITING_APPROVAL
	}

	private static JFrame mainWindow;
	private static JButton retrieveContent, retrieveMissing;
	private static JButton btnDismiss, btnValidate;
	private static JButton btnEdit, btnDelete;
	private static JLabel retrieveStatus, lblProducts;
	private static JTable table;
	private static JList<Product> productList;
	private static volatile String msgStr;
	private static volatile Status status;

	/** The initializing function for the main window. */
	public static Runnable setupGUI = new Runnable()
	{
		@Override
		public void run() {
			/* Content partially generated by WindowBuilder for Eclipse */
			mainWindow = new JFrame(lang("window_title") + AutoAppro.VERSION);
			mainWindow.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent winEvt)
				{
					AutoAppro.saveProducts();
					System.exit(0);
				}
			});
			mainWindow.setIconImage(AutoAppro.icon.getImage());
			mainWindow.setBounds(10, 10, 1000, 700);
			mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
			JMenuBar menuBar = new JMenuBar();
			mainWindow.setJMenuBar(menuBar);
			JMenu mnFile = new JMenu(lang("window_menu_file"));
			menuBar.add(mnFile);
			JMenuItem mntmChangeProvider = new JMenuItem(lang("window_menu_chg_provider"));
			mnFile.add(mntmChangeProvider);
			JMenuItem mntmChangeLogger = new JMenuItem(lang("window_menu_chg_logger"));
			mnFile.add(mntmChangeLogger);
			JSeparator separator = new JSeparator();
			mnFile.add(separator);
			JMenuItem mntmExit = new JMenuItem(lang("window_menu_exit"));
			mntmExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					System.exit(0);
				}
			});
			mnFile.add(mntmExit);
			JMenu mnHelp = new JMenu(lang("window_menu_hm"));
			menuBar.add(mnHelp);
			JMenuItem mntmHelp = new JMenuItem(lang("window_menu_help"));
			mnHelp.add(mntmHelp);
			JSeparator separator_1 = new JSeparator();
			mnHelp.add(separator_1);
			JMenuItem mntmAbout = new JMenuItem(lang("window_menu_about"));
			mntmAbout.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JOptionPane.showMessageDialog(mainWindow, lang("about_c1") + AutoAppro.VERSION +
							"\n" + lang("about_c2"), lang("about_title"), JOptionPane.INFORMATION_MESSAGE);
				}
			});
			mnHelp.add(mntmAbout);
			JPanel contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(new BorderLayout(0, 0));
			mainWindow.setContentPane(contentPane);
			JSplitPane splitPane = new JSplitPane();
			splitPane.setResizeWeight(0.6);
			contentPane.add(splitPane, BorderLayout.CENTER);
			JPanel panel = new JPanel();
			splitPane.setLeftComponent(panel);
			panel.setLayout(new BorderLayout(0, 0));
			JPanel panel_1 = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
			flowLayout.setAlignment(FlowLayout.LEADING);
			panel_1.setBorder(new TitledBorder(null, lang("window_retriever_title"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.add(panel_1, BorderLayout.NORTH);
			retrieveContent = new JButton(lang("window_retriever_content"));
			retrieveContent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String data;
					try {
						data = MyClipBoard.getClipboardText();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(mainWindow, lang("retrieve_error_cb1") + "\n" + e.getMessage(),
								lang("common_error"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (data == null)
					{
						JOptionPane.showMessageDialog(mainWindow, lang("retrieve_error_cb2"),
								lang("common_error"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					retrieveStatus.setText(lang("common_loading"));
					disableButtons();
					msgStr = data;
					(new Thread(doManualRetrieval)).start();
				}
			});
			panel_1.add(retrieveContent);
			retrieveMissing = new JButton(lang("window_retriever_missing"));
			retrieveMissing.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String data;
					try {
						data = MyClipBoard.getClipboardText();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(mainWindow, lang("retrieve_error_cb1") + "\n" + e.getMessage(),
								lang("common_error"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (data == null)
					{
						JOptionPane.showMessageDialog(mainWindow, lang("retrieve_error_cb2"),
								lang("common_error"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					retrieveStatus.setText(lang("common_loading"));
					disableButtons();
					msgStr = data;
					(new Thread(doMissingRetrieval)).start();
				}
			});
			panel_1.add(retrieveMissing);
			retrieveStatus = new JLabel(lang("common_loading"));
			panel_1.add(retrieveStatus);
			JPanel panel_2 = new JPanel();
			panel_2.setBorder(new TitledBorder(null, lang("window_list_title"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.add(panel_2);
			panel_2.setLayout(new BorderLayout(0, 0));
			table = new JTable();
			panel_2.add(table, BorderLayout.CENTER);
			JPanel panel_5 = new JPanel();
			panel_2.add(panel_5, BorderLayout.SOUTH);
			btnDismiss = new JButton(lang("window_list_dismiss"));
			panel_5.add(btnDismiss);
			btnValidate = new JButton(lang("window_list_validate"));
			panel_5.add(btnValidate);
			JPanel panel_3 = new JPanel();
			panel_3.setBorder(new TitledBorder(null, lang("window_products_title"),
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			splitPane.setRightComponent(panel_3);
			panel_3.setLayout(new BorderLayout(0, 0));
			JPanel panel_4 = new JPanel();
			panel_3.add(panel_4, BorderLayout.NORTH);
			lblProducts = new JLabel();
			panel_4.add(lblProducts);
			btnEdit = new JButton(new ImageIcon(AutoAppro.class.getResource("icon_info.png")));
			panel_4.add(btnEdit);
			btnDelete = new JButton(new ImageIcon(AutoAppro.class.getResource("icon_delete.png")));
			panel_4.add(btnDelete);
			productList = new JList<Product>();
			productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			panel_3.add(productList, BorderLayout.CENTER);
			updateProducts();
			disableButtons();
		}
	};

	/* Disable all buttons. */
	private static void disableButtons()
	{
		retrieveContent.setEnabled(false);
		retrieveMissing.setEnabled(false);
		btnDismiss.setEnabled(false);
		btnValidate.setEnabled(false);
		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
	}

	/** The displaying function for the main window. */
	public static Runnable displayGUI = new Runnable()
	{
		@Override
		public void run() {
			mainWindow.setVisible(true);
			(new Thread(startNewDelivery)).start();
		}
	};

	/* Start a new delivery (non-GUI thread). */
	private static Runnable startNewDelivery = new Runnable()
	{
		@Override
		public void run() {
			msgStr = null;
			if (AutoAppro.provider.tryAutomaticRetrieve())
				status = Status.WAITING_APPROVAL;
			else
				status = Status.WAITING_CONTENT;
			SwingUtilities.invokeLater(updateGUI);
		}
	};

	/* update the GUI according to the current situation. */
	private static Runnable updateGUI = new Runnable()
	{
		@Override
		public void run() {
			switch (status)
			{
			case WAITING_CONTENT:
				if (msgStr != null)
				{
					JOptionPane.showMessageDialog(mainWindow, lang("retrieve_error_content") + "\n" + msgStr,
							lang("common_error"), JOptionPane.ERROR_MESSAGE);
				}
				retrieveContent.setEnabled(true);
				retrieveStatus.setText(lang("status_need_content"));
				break;
			case WAITING_MISSING:
				if (msgStr != null)
				{
					JOptionPane.showMessageDialog(mainWindow, lang("retrieve_error_content") + "\n" + msgStr,
							lang("common_error"), JOptionPane.ERROR_MESSAGE);
				}
				retrieveMissing.setEnabled(true);
				retrieveStatus.setText(lang("status_need_missing"));
				break;
			case WAITING_APPROVAL:
				updateDelivery();
				btnDismiss.setEnabled(true);
				btnValidate.setEnabled(true);
				retrieveStatus.setText(lang("status_ok"));
				break;
			}
			btnEdit.setEnabled(true);
			btnDelete.setEnabled(true);
		}
	};

	/* Retrieve the contents data from the local variable msgStr (non-GUI thread). */
	private static Runnable doManualRetrieval = new Runnable()
	{
		@Override
		public void run() {
			try {
				AutoAppro.provider.retrieveFromString(msgStr);
			} catch (IllegalArgumentException e) {
				msgStr = e.getMessage();
				SwingUtilities.invokeLater(updateGUI);
				return;
			}
			if (AutoAppro.provider.useMissingList())
				status = Status.WAITING_MISSING;
			else
				status = Status.WAITING_APPROVAL;
			msgStr = null;
			SwingUtilities.invokeLater(updateGUI);
		}
	};

	/* Retrieve the missing products data from the local variable msgStr (non-GUI thread). */
	private static Runnable doMissingRetrieval = new Runnable()
	{
		@Override
		public void run() {
			try {
				AutoAppro.provider.retrieveMissing(msgStr);
			} catch (IllegalArgumentException e) {
				msgStr = e.getMessage();
				SwingUtilities.invokeLater(updateGUI);
				return;
			}
			status = Status.WAITING_APPROVAL;
			msgStr = null;
			SwingUtilities.invokeLater(updateGUI);
		}
	};

	/* Update the list of the products in the delivery. */
	private static void updateDelivery()
	{
		// TODO
	}

	/* Just a little shortcut ... */
	private static String lang(String keyword)
	{
		return AutoAppro.messages.getString(keyword);
	}

	/** Update the list of products for the current provider. */
	public static void updateProducts()
	{
		Vector<Product> data = new Vector<Product>(AutoAppro.products.values());
		Collections.sort(data);
		productList.setListData(data);
		lblProducts.setText(Integer.toString(data.size()) + " " + lang("window_products_qtt"));
	}
}
