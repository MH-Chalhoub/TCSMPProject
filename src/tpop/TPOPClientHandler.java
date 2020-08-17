package tpop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.Mail;
import entities.MailBox;
import entities.User;
import puzzle.Puzzle;
import tcsmp.EnregistrementIncorrecte;
import tcsmp.TCSMPClientHandler;
import tcsmp.TCSMPClientSession;

public class TPOPClientHandler extends Thread {
	Socket link;
	DataInputStream in;
	DataOutputStream out;
	ArrayList<TPOPClientHandler> clients;
	String serverDomain, clientDomain;
	String username, password;
	boolean identifed = false, found = false;
	Mail mail;
	MailBox box;
	ArrayList<MailBox> mailBoxs;
	ArrayList<User> users;

	public TPOPClientHandler(Socket link, ArrayList<TPOPClientHandler> clients, String serverDomain,
			String clientDomain, ArrayList<MailBox> mailBoxs, ArrayList<User> users) {
		this.link = link;
		this.clients = clients;
		this.serverDomain = serverDomain;
		this.clientDomain = clientDomain;
		this.mailBoxs = mailBoxs;
		this.users = users;

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
				String[] tokens = message_in.split(" ", 0);

				if (tokens[0].equals("QUIT")) {
					out.writeUTF("+OK TPOP  " + serverDomain + " Server exiting");
					System.out.println("C : " + message_in);
					System.out.println("S : +OK TPOP  " + serverDomain + " Server exiting");
					System.out.println("S : Connection closed by foreign host.");
					break;
				} else if (tokens[0].equals("USER")) {
					if (getUsernames(users).contains(tokens[1])) {
						out.writeUTF("+OK User name (" + tokens[1] + ") ok. Password, please.");
						System.out.println("C : " + message_in);
						System.out.println("S : +OK User name (" + tokens[1] + ") ok. Password, please.");
						username = tokens[1];
						// System.out.println("250-" + serverDomain + " greets " + clientDomain);
						found = true;
					} else {
						out.writeUTF("-ERR sorry, " + tokens[1] + " doesn't get his mail here");
						System.out.println("C : " + message_in);
						System.out.println("S : -ERR sorry, " + tokens[1] + " doesn't get his mail here");
						// System.out.println("250-" + serverDomain + " greets " + clientDomain);
						found = false;
					}
				}
				if (found) {
					// System.out.println("tokens[0] " + tokens[0]);
					// System.out.println("tokens[0].equals(\"FROM\") " + tokens[0].equals("FROM"));
					if (tokens[0].equals("PASS")) {
						if (getUser(users, username).getPassword().equals(tokens[1])) {
							box = getUserMailBox(mailBoxs, username);
							if(!(box == null)) {
								out.writeUTF("+OK " + username + "'s maildrop has " + box.getMailBoxMessagesNb()
								+ " messages (" + box.getMailBoxMessagesSize() + " octets)");
						System.out.println("C : " + message_in);
						System.out.println("S : +OK " + username + "'s maildrop has " + box.getMailBoxMessagesNb()
								+ " messages (" + box.getMailBoxMessagesSize() + " octets)");
							}
							else {
								out.writeUTF("+OK " + username + "'s maildrop has 0 messages (0 octets)");
								System.out.println("C : " + message_in);
								System.out.println("S : +OK " + username + "'s maildrop has 0 messages (0 octets)");
							}
							identifed = true;
						} else {
							out.writeUTF("-ERR invalid password");
							System.out.println("C : " + message_in);
							System.out.println("S : -ERR invalid password");
							identifed = true;
						}
					}
					if (identifed) {
						if (tokens[0].equals("STAT")) {
							out.writeUTF("+OK " + box.getMailBoxMessagesNb() + " " + box.getMailBoxMessagesSize());
							System.out.println("C : " + message_in);
							System.out.println(
									"S : +OK " + box.getMailBoxMessagesNb() + " " + box.getMailBoxMessagesSize());
						} else if (tokens[0].equals("LIST")) {
							if (tokens.length == 1) {
								out.writeUTF("+OK " + box.getMailBoxMessagesNb() + " messages (" + box.getMailBoxMessagesSize() + " octets)\n");
								System.out.println("C : " + message_in);
								System.out.println("S : +OK " + box.getMailBoxMessagesNb() + " messages (" + box.getMailBoxMessagesSize() + " octets)");
								
								ArrayList<Mail> mailList = box.getMails();
								int mailNb = 1;
								for(Mail m : mailList) {
									out.writeUTF(mailNb + " " + m.getMailSize());
									System.out.println("S : " + mailNb + " " + m.getMailSize());
									mailNb++;
								}
								out.writeUTF(".");
								System.out.println(".");
								
							} else {
								int msgNb = Integer.parseInt(tokens[1]);
								int mailBoxmsgNb = box.getMails().size();
								if(msgNb <= mailBoxmsgNb) {
									out.writeUTF("+OK " + msgNb + " " + box.getMails().get(msgNb).getMailSize());
									System.out.println("C : " + message_in);
									System.out.println("S : +OK " + msgNb + " " + box.getMails().get(msgNb).getMailSize());
								}
								else {
									out.writeUTF("-ERR no such message, only " + mailBoxmsgNb + " messages in mailbox");
									System.out.println("C : " + message_in);
									System.out.println("S : -ERR no such message, only " + mailBoxmsgNb + " messages in mailbox");
								}
							}
						}
						else if (tokens[0].equals("RETR")) {
							int msgNb = Integer.parseInt(tokens[1]) - 1;
							int mailBoxmsgNb = box.getMails().size();
							if(msgNb <= mailBoxmsgNb) {
								out.writeUTF("+OK " + box.getMails().get(msgNb).getMailSize() + " octets");
								out.writeUTF(box.getMails().get(msgNb).getfullMail());
								out.writeUTF(".");
								System.out.println("C : " + message_in);
								System.out.println("S : +OK " + box.getMails().get(msgNb).getMailSize() + " octets");
								System.out.println("S : " + box.getMails().get(msgNb).getfullMail());
								System.out.println(".");
							}
							else {
								out.writeUTF("-ERR no such message, only " + mailBoxmsgNb + " messages in mailbox");
								System.out.println("C : " + message_in);
								System.out.println("S : -ERR no such message, only " + mailBoxmsgNb + " messages in mailbox");
							}
						}
						else if (tokens[0].equals("DELE")) {
							int msgNb = Integer.parseInt(tokens[1]);
							int mailBoxmsgNb = box.getMails().size();
							if(msgNb <= mailBoxmsgNb) {
								out.writeUTF("+OK message # " + msgNb + " deleted");
								System.out.println("C : " + message_in);
								System.out.println("S : +OK message # " + msgNb + " deleted");
							}
							else {
								out.writeUTF("-ERR no such message, only " + mailBoxmsgNb + " messages in mailbox");
								System.out.println("C : " + message_in);
								System.out.println("S : -ERR no such message, only " + mailBoxmsgNb + " messages in mailbox");
							}
						}
					}
				}
			} while (true);
		} catch (IOException ex) {
			ex.printStackTrace();
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

	public void closeconnection() {
		clients.remove(this);
		try {
			link.close();
		} catch (IOException ex) {
			Logger.getLogger(TCSMPClientHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public ArrayList<String> getUsernames(ArrayList<User> users) {
		ArrayList<String> usernames = new ArrayList<>();
		for (User u : users) {
			usernames.add(u.getUsername());
		}
		return usernames;
	}

	public User getUser(ArrayList<User> users, String username) {
		User user = new User();
		for (User u : users) {
			if (u.getUsername().equals(username)) {
				user = u;
			}
		}
		return user;
	}

	public MailBox getUserMailBox(ArrayList<MailBox> mailboxs, String user) {
		for (MailBox b : mailboxs) {
			if (b.getUser().equals(user))
				return b;
		}
		return new MailBox(user, new ArrayList<Mail>());
	}
}
