package tpop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import entities.MailBox;
import entities.User;
import entities.Mail;

public class TPOPServer {

    private static ServerSocket servSock;
    private static Socket link;
    private static int PORT = 1998;
    private static String serverDomain = "BINIOU.com";
    private static String clientDomain;
    static HashMap<String, Integer> dns;
    
	public static void main(String[] args) {
		ArrayList<TPOPClientHandler> clients = new ArrayList<TPOPClientHandler> ();
		ArrayList<MailBox> MailBoxs = new ArrayList<MailBox> ();
		ArrayList<User> users = new ArrayList<User> ();
		
		initiateMailBoxs("Y@BINIOU.com", MailBoxs);
		initiateUsers("Y@BINIOU.com", "password", users);
		
		dns = new HashMap<String, Integer>();	//I did use diffrent port for each TCSMP server because i have only one machine(one NIC Card)
		dns.put("BINIOU.com", 2000);
		dns.put("POUET.com", 2001);
		
		/*System.out.println("Enter Server Domain Name (BINIOU.com/POUET.com) : ");
		Scanner inserverDomain = new Scanner(System.in);
		serverDomain = inserverDomain.nextLine();
		inserverDomain.close();*/
		
		PORT = dns.get(serverDomain);
		
		System.out.println("Server Domain Name : " + serverDomain + "\nPort # : " + PORT);
    	System.out.println("Mail Boxs and Client Handler are created");

        try {
            servSock = new ServerSocket(PORT);
        } catch (IOException ex) {
            System.out.println("Unable to connect to this port");
        }
        
        Thread tpopThread = new Thread()
        {
            public void run() {

                try {
                    while (true) {
                    	System.out.println("Wainting for connection ...");
                        link = servSock.accept();
                        System.out.println("Connection accepted ...");
                        /*------------------------------------------------------------------------------------------------------*/
                    	DataOutputStream out;
                    	DataInputStream in;
            			out = new DataOutputStream(link.getOutputStream());
            			in = new DataInputStream(link.getInputStream());
                        //System.out.println("IP = " + link.getInetAddress() + " Port = " + link.getPort());
            			out.writeUTF("+OK " + serverDomain + " TPOP Server Process at " + new Date().toString());
            			clientDomain = in.readUTF();
            			clientDomain = clientDomain.replace("\n", "").replace("\r", "");
            			/*------------------------------------------------------------------------------------------------------*/
            			TPOPClientHandler cl = new TPOPClientHandler(link, clients, serverDomain, clientDomain, MailBoxs, users);
                        cl.start();
                        clients.add(cl);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        tpopThread.start();
	}
	
	public static void initiateMailBoxs(String user,ArrayList<MailBox> MailBoxs) {

		String s = "Date: Sat Aug 08 07:52:48 EEST 2020\r\n" + 
				"Sender: X@POUET.com\r\n" + 
				"From: X@POUET.com\r\n" + 
				"To: Y@BINIOU.com\r\n" + 
				"Subject: Some subject\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"... Message text s ...\r\n" + 
				".\r\n" + 
				"";
		String s1 = "Date: Sat Aug 08 07:52:48 EEST 2020\r\n" + 
				"Sender: X@POUET.com\r\n" + 
				"From: X@POUET.com\r\n" + 
				"To: Y@BINIOU.com\r\n" + 
				"Subject: Some subject\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"... Message text s1 ...\r\n" + 
				".\r\n" + 
				"";
		try {
			Mail m = new Mail(s);
			Mail m1 = new Mail(s1);
			ArrayList<Mail> mails = new ArrayList<>();
			mails.add(m);
			mails.add(m1);
			MailBox mb = new MailBox(user, mails);
			MailBoxs.add(mb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	public static void initiateUsers(String user,String pass, ArrayList<User> users) {
		User u = new User(user, pass);
		users.add(u);
	}

}
