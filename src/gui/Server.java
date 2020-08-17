package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPanel;

import tcsmp.TCSMPServer;

public class Server {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server window = new Server();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Server() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblSelectTheName = new JLabel("Select The Name of the Server");
		lblSelectTheName.setHorizontalAlignment(JLabel.CENTER);
		frame.getContentPane().add(lblSelectTheName, BorderLayout.NORTH);
		
		String s1[] = { "BINIOU.com", "POUET.com"}; 
		
		JButton btnRun = new JButton("RUN");
		frame.getContentPane().add(btnRun, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JComboBox comboBox = new JComboBox(s1);
		comboBox.setBounds(167, 74, 95, 60);
		panel.add(comboBox);

		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(comboBox.getSelectedItem().toString());
				comboBox.setEnabled(false);
				btnRun.setEnabled(false);
				btnRun.setText("Running");
				TCSMPServer.main(new String[] {comboBox.getSelectedItem().toString()});
			}
		});
	}

}
