package tcsmp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import entities.Mail;
import entities.MailBox;
import entities.User;

//The role of the TCSMP Server is contacting clients according to TCSMP Protocol defined in the RFC and forwarding the msg to the TPOP Server

public class TCSMPServer {
    private static ServerSocket servSock;
    private static Socket link;
    private static int PORT = 1998;
    private static String serverDomain = "POUET.com";
    private static String clientDomain;
    static HashMap<String, Integer> dns;
    static boolean registerd = false;
    static DataOutputStream out;
    static DataInputStream in;
    static ArrayList<User> users;

	public static void main(String[] args) {
		ArrayList<TCSMPClientHandler> clients = new ArrayList<TCSMPClientHandler> ();
		ArrayList<MailBox> MailBoxs = new ArrayList<MailBox> ();
		 users = new ArrayList<User> ();
		
		dns = new HashMap<String, Integer>();	//I did use diffrent port for each TCSMP server because i have only one machine(one NIC Card)
		dns.put("BINIOU.com", 1998);
		dns.put("POUET.com", 1999);
		
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
        
        Thread tcsmpThread = new Thread()
        {
            public void run() {

                try {
                    while (true) {
                    	System.out.println("Wainting for connection ...");
                        link = servSock.accept();
                        System.out.println("Connection accepted ...");
                        /*------------------------------------------------------------------------------------------------------*/
            			out = new DataOutputStream(link.getOutputStream());
            			in = new DataInputStream(link.getInputStream());
                        //System.out.println("IP = " + link.getInetAddress() + " Port = " + link.getPort());
            			out.writeUTF("220 " + serverDomain + " Time Control Stamped Mail Protocol");
            			String s = in.readUTF();
            			s = s.replace("\n", "").replace("\r", "");
            			if(!s.equals("REG")) {
                			clientDomain = s;
                            TCSMPClientHandler cl = new TCSMPClientHandler(link, clients, serverDomain, clientDomain, MailBoxs);
                            cl.start();
                            clients.add(cl);
            			}
            			else {
                			String message_in = in.readUTF();
            				message_in = message_in.replace("\n", "").replace("\r", "");
            				String [] tokens = message_in.split(" ", 0);
            				if(tokens[0].equals("REG")) {
            					addUser(tokens[1], tokens[2], users);
            					registerd = true;
            					
            				}
            			}
            			//System.out.println("Users list : " + users.toString());
            			/*------------------------------------------------------------------------------------------------------*/
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
	
	public static void addUser(String user,String pass, ArrayList<User> users) throws IOException {
		System.out.println("Adding " + user + " to the list.");
			if(userExiste(user,users)) {
				User u = getUser(user,users);
				if(u.checkPassword(pass)) {
	    			out.writeUTF("240 " + user + " " + pass + " logged in successfully.");
				}
				else {
	    			out.writeUTF("540 " + user + " " + pass + " wrong password.");
				}
			}
			else {
				User newUser = new User(user, pass);
				users.add(newUser);
    			out.writeUTF("230 " + user + " " + pass + " added to the users List.");
				System.out.println("S : 230 " + user + " " + pass + " added to the users List.");
			}
		
	}
	
	public static void initiateUsers(String user,String pass, ArrayList<User> users) {
		User u = new User(user, pass);
		users.add(u);
	}
	
	public static boolean userExiste(String user, ArrayList<User> users) {
		if (!users.isEmpty()) {
			for (User u : users) {
				if (u.getUsername().equals(user))
					return true;
			}
			return false;
		}
		return false;
	}	
	
	public static User getUser(String user, ArrayList<User> users) {
		for (User u : users) {
			if (u.getUsername().equals(user)) {
				return u;
			}
		}
		return null;
	}

}
