package tcsmp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import mail.Mail;

//The role of the TCSMP Server is contacting clients according to TCSMP Protocol defined in the RFC and forwarding the msg to the TPOP Server

public class TCSMPServer {
    private static ServerSocket servSock;
    private static Socket link;
    private static int PORT = 1998;
    private static String serverDomain = "POUET.com";
    private static String clientDomain;
    static HashMap<String, Integer> dns;

	public static void main(String[] args) {
		//ArrayList<MailBox> MailBoxs = new ArrayList<MailBox> ();
		ArrayList<TCSMPClientHandler> clients = new ArrayList<TCSMPClientHandler> ();
		ArrayList<Mail> mailBoxs = new ArrayList<Mail> ();
		
		dns = new HashMap<String, Integer>();	//I did use diffrent port for each TCSMP server because i have only one machine(one NIC Card)
		dns.put("BINIOU.com", 1998);
		dns.put("POUET.com", 1999);
		
		System.out.println("Enter Server Domain Name (BINIOU.com/POUET.com) : ");
		Scanner inserverDomain = new Scanner(System.in);
		serverDomain = inserverDomain.nextLine();
		
		PORT = dns.get(serverDomain);
		
		System.out.println("Server Domain Name : " + serverDomain + "\nPort # : " + PORT);
    	System.out.println("Mail Boxs and Client Handler are created");
		
        try {
            servSock = new ServerSocket(PORT);
        } catch (IOException ex) {
            System.out.println("Unable to connect to this port");
        }
        
        Thread tcsmpThread = new Thread()
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
            			out.writeUTF("220 " + serverDomain + " Time Control Stamped Mail Protocol");
            			clientDomain = in.readUTF();
            			clientDomain = clientDomain.replace("\n", "").replace("\r", "");
            			/*------------------------------------------------------------------------------------------------------*/
                        TCSMPClientHandler cl = new TCSMPClientHandler(link, clients, serverDomain, clientDomain, mailBoxs);
                        cl.start();
                        clients.add(cl);
                        //MailBox mb = new MailBox("", link.getPort(), link.getRemoteSocketAddress().toString(), "", serverDomain, link);
                        //MailBoxs.add(mb);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        tcsmpThread.start();
        
	}

}
