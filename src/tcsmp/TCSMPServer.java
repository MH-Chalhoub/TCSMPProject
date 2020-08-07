package tcsmp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;



public class TCSMPServer {
    private static ServerSocket servSock;
    private static Socket link;
    private static int PORT = 1998;
    private static String serverDomain = "BINIOU.com";
    private static String clientDomain;
    static HashMap<String, Integer> dns;

	public static void main(String[] args) {
		ArrayList<MailBox> MailBoxs = new ArrayList<MailBox> ();
		ArrayList<TCSMPClientHandler> clients = new ArrayList<TCSMPClientHandler> ();
		dns = new HashMap<String, Integer>();
		dns.put("BINIOU.com", 1998);
		dns.put("POUET.com", 1999);
		
		System.out.println("Enter Server Domain Name (BINIOU.com/POUET.com) : ");
		Scanner inserverDomain = new Scanner(System.in);
		serverDomain = inserverDomain.nextLine();
		
		PORT = dns.get(serverDomain);
		
		System.out.println("Server Domain Name " + serverDomain);
		
        try {
            servSock = new ServerSocket(PORT);
        } catch (IOException ex) {
            System.out.println("Unable to connect to this port");
        }
        
        try {
            while (true) {
            	System.out.println("Wainting for connection ...");
                link = servSock.accept();
            	DataOutputStream out;
            	DataInputStream in;
    			out = new DataOutputStream(link.getOutputStream());
    			in = new DataInputStream(link.getInputStream());
                //System.out.println("IP = " + link.getInetAddress() + " Port = " + link.getPort());
    			out.writeUTF("220 " + serverDomain + " Time Control Stamped Mail Protocol");
    			clientDomain = in.readUTF();
    			clientDomain = clientDomain.replace("\n", "").replace("\r", "");
                System.out.println("Connection accepted ...");

                TCSMPClientHandler cl = new TCSMPClientHandler(link, clients, serverDomain, clientDomain);
                cl.start();
                clients.add(cl);
                MailBox mb = new MailBox("", link.getPort(), link.getRemoteSocketAddress().toString(), "", serverDomain, link);
                MailBoxs.add(mb);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

}
