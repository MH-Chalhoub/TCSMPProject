package tcsmp;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entities.Mail;
import registration.Login;
import tpop.TPOPSession;

import javax.swing.JTabbedPane;
import javax.swing.JTable;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;

public class TCSMPClient  extends JFrame
{

	private JPanel contentPane;
	static int adjustWidth = 37;
	static int adjustHeight = 38;
	private JTextField textField;
	private JTextField textField_1;
	String username, password;
	DefaultTableModel m;
	TPOPSession tpop;
    static HashMap<String, Integer> tcsmpdns;
    static HashMap<String, Integer> tpopdns;

    public static void main(String[] args)
    {
    	TCSMPClientSession tcsmp = new TCSMPClientSession(
           "localhost",
           1999,
           "Y@BINIOU.com",
           "X@POUET.com",
           "Some subject",
           "... Message text ...");

		Scanner scanner = new Scanner(System.in);

    	System.out.println("Enter 1 to send a message and 2 to retrive msg from the server");
		int todo = scanner.nextInt();
		
		//1 to send a message and 2 to retrive msg from the server
		if(todo == 1) {
	        try {
	        	System.out.println("Sending e-mail...");
	        	tcsmp.sendMessage();
	        	System.out.println("E-mail sent.");
	        } catch (Exception e) {
	        	tcsmp.close();
	        	System.out.println("Can not send e-mail!");
	        	e.printStackTrace();
	        }
		}
		else {
			
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TCSMPClient frame = new TCSMPClient("X@POUET.com", "password");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
    }
	public TCSMPClient(String username, String password) {
		this.username = username;
		this.password = password;
		
		tcsmpdns = new HashMap<String, Integer>();
		tcsmpdns.put("BINIOU.com", 1998);
		tcsmpdns.put("POUET.com", 1999);

		tpopdns = new HashMap<String, Integer>();	//I did use diffrent port for each TCSMP server because i have only one machine(one NIC Card)
		tpopdns.put("BINIOU.com", 2000);
		tpopdns.put("POUET.com", 2001);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		//setAutoRequestFocus(false);
		setLocationRelativeTo(null);
		setTitle("TCSMPClient");
		setSize(this.getWidth() + adjustWidth, this.getHeight() + adjustHeight);
		contentPane.setLayout(new BorderLayout(0, 0));

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////logo///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ImageIcon newMail_logo = new ImageIcon(new 
				ImageIcon("C:\\Users\\Chalhoub\\eclipse-workspace\\lab\\assets\\depence.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

		ImageIcon inbox_logo = new ImageIcon(new 
				ImageIcon("C:\\Users\\Chalhoub\\eclipse-workspace\\lab\\assets\\entree.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

		ImageIcon facture_logo = new ImageIcon(new 
				ImageIcon("C:\\Users\\Chalhoub\\eclipse-workspace\\lab\\assets\\facture.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

		ImageIcon employe_logo = new ImageIcon(new 
				ImageIcon("C:\\Users\\Chalhoub\\eclipse-workspace\\lab\\assets\\employe.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

		ImageIcon verger_logo = new ImageIcon(new 
				ImageIcon("C:\\Users\\Chalhoub\\eclipse-workspace\\lab\\assets\\verger.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 5, 5);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setTabPlacement(JTabbedPane.LEFT);
		
		JPanel newMail = new JPanel();
		newMail.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab("New Mail", newMail_logo, newMail, "Send new Mail");
		
		JPanel panel = new JPanel();
		newMail.add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 2));
		
		JLabel lblNewLabel = new JLabel("Recipient");
		panel.add(lblNewLabel);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Subject");
		panel.add(lblNewLabel_1);
		
		textField_1 = new JTextField();
		panel.add(textField_1);
		textField_1.setColumns(10);
		
		JEditorPane editorPane = new JEditorPane();
		newMail.add(editorPane, BorderLayout.CENTER);
		
		JButton btnSend = new JButton("SEND");
		newMail.add(btnSend, BorderLayout.SOUTH);

		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

		    	TCSMPClientSession tcsmp = new TCSMPClientSession(
		           "localhost",
		           tcsmpdns.get(getDomain(username)),
		           textField.getText().toString(),
		           username,
		           textField_1.getText().toString(),
		           editorPane.getText().toString());
		        try {
		        	System.out.println("Sending e-mail...");
		        	tcsmp.sendMessage();
		        	System.out.println("E-mail sent.");
		        } catch (Exception e) {
		        	tcsmp.close();
		        	System.out.println("Can not send e-mail!");
		        	e.printStackTrace();
		        }
			}
		});
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		

		JPanel inbox = new JPanel();
		tabbedPane.addTab("Inbox", inbox_logo, inbox, "Check your new Mails");

		m = new DefaultTableModel();
		String[] entete = {"Sender", "Date", "Subject", "Message"};
		m.setColumnIdentifiers(entete);
		JTable table_inbox = new JTable(m);
		table_inbox.setShowGrid(true);
		
		//String[] line = {"X@POUET.COM", "Sat Aug 08 07:52:48 EEST 2020", "Some subject", "... Message text ..."};
		//String[] line1 = {"A@POUET.COM", "Sat Aug 12 01:52:44 EEST 2020", "Some subject 1", "... Message text 1 ..."};
		//m.addRow(line);
		//m.addRow(line1);
		inbox.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane(table_inbox);
		inbox.add(scrollPane);
		
		JButton btnRefresh = new JButton("Refresh");
		inbox.add(btnRefresh, BorderLayout.SOUTH);
		

		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					refresh_inbox();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void refresh_inbox() throws IOException {
		tpop = new TPOPSession("localhost", tpopdns.get(getDomain(username)), username, password);
		tpop.connectAndAuthenticate();
		int messageCount = tpop.getMessageCount();
		String[] messages = tpop.getHeaders();
		for (int i = 0; i < messages.length; i++) {
			StringTokenizer messageTokens = new StringTokenizer(messages[i]);
			String messageId = messageTokens.nextToken();
			String messageSize = messageTokens.nextToken();
			String messageBody = tpop.getMessage(messageId);
			Mail retMail = new Mail(messageBody);
			
			m.addRow(new String[] {retMail.getSender(), retMail.getDate(), retMail.getSubject(), retMail.getMailData()});
		}
		tpop.quit();
	}

	static String getDomain(String mail) {
		String[] domain = mail.split("@");
		return domain[1];
	}
}
