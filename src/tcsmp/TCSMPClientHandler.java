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

import mail.Mail;
import puzzle.Puzzle;


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
	Mail mail;
	ArrayList<Mail> mailBoxs;
	

	public TCSMPClientHandler(Socket link, ArrayList<TCSMPClientHandler> clients, String serverDomain, String clientDomain, ArrayList<Mail> mailBoxs) {
		this.link = link;
		this.clients = clients;
		this.serverDomain = serverDomain;
		this.clientDomain = clientDomain;
		this.mailBoxs = mailBoxs;

		try {
			in = new DataInputStream(link.getInputStream());
			out = new DataOutputStream(link.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void run() {
		String message_in = null;
		
		try {
			do {
				message_in = in.readUTF();
				message_in = message_in.replace("\n", "").replace("\r", "");
				String [] tokens = message_in.split(" ", 0);
				
				if(tokens[0].equals("QUIT")) {
					out.writeUTF("221 " + serverDomain + " See you next time!");
					System.out.println("C : " + message_in);
					System.out.println("S : 221 " + serverDomain + " See you next time!");
                	System.out.println("Connection closed");
                	break;
				}
				else if(tokens[0].equals("TELO")) 
				{
					if(tokens[1].equals(serverDomain)) {
						out.writeUTF("250-" + serverDomain + " greets " + clientDomain);
						out.writeUTF("250 TCSMPv1");
						System.out.println("C : " + message_in);
						System.out.println("S : 250-" + serverDomain + " greets " + clientDomain);
						System.out.println("S : 250 TCSMPv1");
						//System.out.println("250-" + serverDomain + " greets " + clientDomain);
						identifed = true;
					}
				}
				if(identifed) {
					//System.out.println("tokens[0] " + tokens[0]);
					//System.out.println("tokens[0].equals(\"FROM\") " + tokens[0].equals("FROM"));
					if(tokens[0].equals("FROM")) {
						senderMail = getEmail(tokens[1]);
						out.writeUTF("250 OK");
						System.out.println("C : " + message_in);
						System.out.println("S : 250 OK");
					}
					else if(tokens[0].equals("RCPT")) {
						reciverMail = getEmail(tokens[1]);
						out.writeUTF("250 OK");
						System.out.println("C : " + message_in);
						System.out.println("S : 250 OK");
					}
					else if(tokens[0].equals("APZL")){
						p = new Puzzle(2, 2);
						String puzzle = p.generatePuzzle();
						p.setPuzzle(puzzle);
						out.writeUTF("215 " + serverDomain + " " + p.getRow() + "," + p.getCol() + " " + puzzle);
						System.out.println("C : " + message_in);
						System.out.println("S : 215 " + serverDomain + " " + p.getRow() + "," + p.getCol() + " " + puzzle);
					}
					else if(tokens[0].equals("MAIL")){
						out.writeUTF("354 Start mail input; end with «CRLF».«CRLF»");
						System.out.println("C : " + message_in);
						System.out.println("S : 354 Start mail input; end with «CRLF».«CRLF»");
						String response = "";
						String line;

						do {
							line = in.readUTF();
							//System.out.println(line);
							if ((line == null)) {
								// TCSMP response lines should at the very least have a 3-digit number
								throw new IOException("Bad response from server.");
							}
							if(line.startsWith(".") && !line.equals(".\n"))
								line = line.substring(1);
							response += line;
						} while (!line.equals(".\n"));
						
						System.out.println("C : \n" + response);
						mail = new Mail(response);
						
						out.writeUTF("250 OK");
						System.out.println("S : 250 OK");
					}
					else if(tokens[0].equals("PKEY")){
						System.out.println("C : " + message_in);
						if(p.checkSolution(tokens[3])) {
							out.writeUTF("216 your mail has been kept!");
							System.out.println("216 your mail has been kept!");
						}
						else {
							out.writeUTF("516 solving ERROR");
							System.out.println("516 solving ERROR");
						}
							
					}
				}
				//out.writeUTF("250 TCSMPv1");
				if(message_in.charAt(0) == '5')
	                throw new EnregistrementIncorrecte();
			}while(true);
		} catch (EnregistrementIncorrecte e) {
			e.printStackTrace();
		}
        catch (IOException ex) {
            ex.printStackTrace();
        }
//		System.out.println("reciverMail : " + reciverMail);
//		System.out.println("getDomain(reciverMail) : " + getDomain(reciverMail));
//		System.out.println("getDomain(reciverMail).equals(\"POUET.com\") : " + getDomain(reciverMail).equals("POUET.com"));
		if(!getDomain(reciverMail).equals(serverDomain)) {

	    	TCSMPServerSession tcsmp = new TCSMPServerSession(
	           "localhost",
	           1998,
	           mail.getReciver(),
	           mail.getSender(),
	           mail.getSubject(),
	           mail.getMailData());

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
			mailBoxs.add(mail);
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
