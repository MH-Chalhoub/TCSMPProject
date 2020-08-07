package tcsmp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TCSMPClientHandler extends Thread {

	Socket link;
	DataInputStream in;
	DataOutputStream out;
	ArrayList<TCSMPClientHandler> clients;
	String name;
	String serverDomain, clientDomain;
	String senderMail, reciverMail;
	Puzzle p;
	boolean identifed = false;
	

	public TCSMPClientHandler(Socket link, ArrayList<TCSMPClientHandler> clients, String serverDomain, String clientDomain) {
		this.link = link;
		this.clients = clients;
		this.serverDomain = serverDomain;
		this.clientDomain = clientDomain;

		try {
			in = new DataInputStream(link.getInputStream());
			out = new DataOutputStream(link.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void run() {
		String message_in = null;
		/*
		try {
			out.writeUTF("220 " + domain + " Time Control Stamped Mail Protocol");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		try {
			do {
				//System.out.println(identifed);
				//message_in = "";
				message_in = in.readUTF();
				message_in = message_in.replace("\n", "").replace("\r", "");
				String [] tokens = message_in.split(" ", 0);
				//System.out.println(message_in);
				if(tokens[0].equals("QUIT")) {
                	System.out.println("Connection closed");
					out.writeUTF("221 " + serverDomain + " See you next time!");
                	break;
				}
				else if(tokens[0].equals("TELO")) 
				{
					if(tokens[1].equals(serverDomain)) {
						out.writeUTF("250-" + serverDomain + " greets " + clientDomain);
						out.writeUTF("250 TCSMPv1");
						//System.out.println("250-" + serverDomain + " greets " + clientDomain);
						identifed = true;
					}
				}
				if(identifed) {
					//System.out.println("tokens[0] " + tokens[0]);
					//System.out.println("tokens[0].equals(\"FROM\") " + tokens[0].equals("FROM"));
					if(tokens[0].equals("FROM")) {
						senderMail = getEmail(tokens[1]);
						System.out.println("250 OK");
						out.writeUTF("250 OK");
					}
					else if(tokens[0].equals("RCPT")) {
						reciverMail = getEmail(tokens[1]);
						System.out.println("250 OK");
						out.writeUTF("250 OK");
					}
					else if(tokens[0].equals("APZL")){
						p = new Puzzle(2, 2);
						String puzzle = p.generatePuzzle();
						p.setPuzzle(puzzle);
						System.out.println("215 " + serverDomain + " " + p.getRow() + "," + p.getCol() + " " + puzzle);
						out.writeUTF("215 " + serverDomain + " " + p.getRow() + "," + p.getCol() + " " + puzzle);
					}
					else if(tokens[0].equals("MAIL")){
						System.out.println("354 Start mail input; end with «CRLF».«CRLF»");
						out.writeUTF("354 Start mail input; end with «CRLF».«CRLF»");
						String response = "";
						String line;

						do {
							System.out.println("before readUTF");
							line = in.readUTF();
							System.out.println(line);
							System.out.println("after readUTF");
							if ((line == null)) {
								// TCSMP response lines should at the very least have a 3-digit number
								throw new IOException("Bad response from server.");
							}
							if(line.startsWith(".") && !line.equals(".\n"))
								line = line.substring(1);
							response += line;
						} while (!line.equals(".\n"));
						out.writeUTF("250 OK");
						System.out.println("----------------------------------");
						System.out.println(response);
						System.out.println("----------------------------------");
					}
					else if(tokens[0].equals("PKEY")){
						if(p.checkSolution(tokens[3]))
							out.writeUTF("216 your mail has been kept!");
						else
							out.writeUTF("516 solving ERROR");
							
					}
				}
				//out.writeUTF("250 TCSMPv1");
				if(message_in.charAt(0) == '5')
	                throw new EnregistrementIncorrecte();
			}while(true);
		} catch (EnregistrementIncorrecte e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        catch (IOException ex) {
            ex.printStackTrace();
        }
		System.out.println("reciverMail : " + reciverMail);
		System.out.println("getDomain(reciverMail) : " + getDomain(reciverMail));
		System.out.println("getDomain(reciverMail).equals(\"POUET.com\") : " + getDomain(reciverMail).equals("POUET.com"));
		if(getDomain(reciverMail).equals("POUET.com")) {

	    	TCSMPSession tcsmp = new TCSMPSession(
	           "localhost",
	           1998,
	           "X@POUET.com",
	           "Y@BINIOU.com",
	           "Some subject",
	           "... Message text ...",
	           false);

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
	}
	
	static String getEmail(String mail) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+@[a-zA-Z0-9_]+.com", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(mail);
		// Get the group matched using group() method
		matcher.find();
		return matcher.group();
	}

	public void sendMessage(String m) {
		try {
			this.out.writeUTF(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String getDomain(String mail) {
		String[] domain = mail.split("@");
		return domain[1];
	}

	public String get_Name() {
		return this.name;
	}

	public void closeconnection() {
		clients.remove(this);
		try {
			link.close();
		} catch (IOException ex) {
			Logger.getLogger(TCSMPClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
