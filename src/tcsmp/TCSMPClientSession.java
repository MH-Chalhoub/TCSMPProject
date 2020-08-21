package tcsmp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import puzzle.Puzzle;

public class TCSMPClientSession {
	/** 15 sec. socket read timeout */

	public static final int SOCKET_READ_TIMEOUT = 15 * 1000;

	private String host;
	private int port;
	private String recipient;
	private String sender;
	private String subject;
	private String message;

	protected Socket tcsmpSocket;
	private Puzzle p;
	/*
	 * protected BufferedReader in; protected OutputStreamWriter out;
	 */
	protected DataInputStream in;
	protected DataOutputStream out;
	

	/**
	 * Creates new TCSMP session by given TCSMP host and port, recipient's email
	 * address, sender's email address, email subject and email message text.
	 */

	public TCSMPClientSession(String host, int port, String recipient, String sender, String subject, String message) {
		this.host = host;
		this.port = port;
		this.recipient = recipient;
		this.message = message;
		this.sender = sender;
		this.subject = subject;
	}

	/**
	 * Creates new TCSMP session by given TCSMP host, recipient's email address,
	 * sender's email address, email subject and email message text. Assumes TCSMP
	 * port is 1998 (default for TCSMP service).
	 */

	public TCSMPClientSession(String host, String recipient, String sender, String subject, String message) {
		this(host, 1999, recipient, sender, subject, message);
	}
	

	public TCSMPClientSession(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Closes down the connection to TCSMP server (if open). Should be called if an
	 * exception is raised during the TCSMP session.
	 */

	public void close() {
		try {
			in.close();
			out.close();
			tcsmpSocket.close();
		} catch (Exception ex) {
			// Ignore the exception. Probably the socket is not open.
		}
	}

	/**
	 * 
	 * Connects to the TCSMP server and gets input and output streams (in, out).
	 * 
	 */

	protected void connect() throws IOException {
		tcsmpSocket = new Socket(host, port);
		// tcsmpSocket.setSoTimeout(SOCKET_READ_TIMEOUT);
		/*
		 * in = new BufferedReader(new InputStreamReader(tcsmpSocket.getInputStream()));
		 * out = new OutputStreamWriter(tcsmpSocket.getOutputStream());
		 */
		in = new DataInputStream(tcsmpSocket.getInputStream());
		out = new DataOutputStream(tcsmpSocket.getOutputStream());

		out.writeUTF(getDomain(sender) + "\n");
	}

	static String getDomain(String mail) {
		String[] domain = mail.split("@");
		return domain[1];
	}

	/**
	 * Sends given command and waits for a response from server.
	 * 
	 * @return response received from the server.
	 */
	
	protected String sendCommand(String commandString) throws IOException {
		out.writeUTF(commandString + "\n");
		System.out.println("C : " + commandString);
		out.flush();
		String response = getResponse();
		return response;
	}

	/**
	 * Sends given commandString to the server, gets its reply and checks if it
	 * starts with expectedResponseStart. If not, throws IOException with server's
	 * reply (which is unexpected).
	 */

	protected String doCommand(String commandString, char expectedResponseStart) throws IOException {
		String response = sendCommand(commandString);
		checkServerResponse(response, expectedResponseStart);
		return response;
	}

	/**
	 * Checks if given server reply starts with expectedResponseStart. If not,
	 * throws IOException with this reply (because it is unexpected).
	 */

	protected void checkServerResponse(String response, char expectedResponseStart) throws IOException {
		if (response.charAt(0) != expectedResponseStart) {
			JOptionPane.showMessageDialog(null, "WRONG SOLUTION !!!","Alert",JOptionPane.WARNING_MESSAGE); 
			throw new IOException(response);
		}
	}

	/**
	 * Gets a response back from the server. Handles multi-line responses (according
	 * to TCSMP protocol) and returns them as multi-line string. Each line of the
	 * server's reply consists of 3-digit number followed by some text. If there is
	 * a '-' immediately after the number, the TCSMP response continues on the next
	 * line. Otherwise it finished at this line.
	 */

	protected String getResponse() throws IOException {
		String response = "";
		String line;

		do {
			line = in.readUTF();
			if ((line == null) || (line.length() < 3)) {
				// TCSMP response lines should at the very least have a 3-digit number
				throw new IOException("Bad response from server.");
			}
            String [] tokens = line.split(" ", 0);
			if(tokens[0].equals("215")) {
				p = new Puzzle(tokens[3], getRow(tokens[2]), getCol(tokens[2]));
			}
			response += line + "\n";
		} while ((line.length() > 3) && (line.charAt(3) == '-'));
		System.out.println("S : " + response);

		return response;

	}
	
	private int getRow(String s) {
        String [] tokens = s.split(",", 0);
        return Integer.parseInt(tokens[0]);
	}	
	private int getCol(String s) {
        String [] tokens = s.split(",", 0);
        return Integer.parseInt(tokens[1]);
	}

	/**
	 * Prepares and returns e-mail message headers.
	 */

	protected String getMessageHeaders() {
		// Most header are less than 1024 characters long
		String headers = "";
		headers = headers + "Date: " + new Date().toString() + "\n";
		headers = headers + "Sender: " + sender + "\n";
		headers = headers + "From: " + sender + "\n";
		headers = headers + "To: " + recipient + "\n";
		headers = headers + "Subject: " + subject + "\n";
		return headers + "\n\n";
	}

	/**
	 * Register new user if the user does not already exist and if the
	 * User already registerd it only sign in.
	 */

	public boolean register(String username, String password) throws IOException {

		tcsmpSocket = new Socket(host, port);
		
		in = new DataInputStream(tcsmpSocket.getInputStream());
		out = new DataOutputStream(tcsmpSocket.getOutputStream());

		out.writeUTF("REG");
		doCommand("REG " + username + " " + password, '2');
		close();
		
		return true;
	}

	/**
	 * Sends a message using the TCSMP protocol.
	 */
	public void sendMessage() throws IOException {
		connect();

		// System.out.println("connect done");
		// After connecting, the TCSMP server will send a response string.
		// Make sure it starts with a '2' (reponses in the 200's are positive).
		String response = getResponse();
		checkServerResponse(response, '2');
		// System.out.println("checkServerResponse done");

		// Introduce ourselves to the TCSMP server with a polite "HELO localhostname"
		doCommand("TELO " + checkForPattern(response), '2');

		// Tell the server who this message is from
		doCommand("FROM <" + sender + ">", '2');

		// Now tell the server who we want to send a message to
		doCommand("RCPT <" + recipient + ">", '2');

		String strPuzzle = doCommand("APZL", '2');
		Puzzle puzzle = new Puzzle(strPuzzle);

		// Okay, now send the mail message. We expect a response beginning
		// with '3' indicating that the server is ready for data.
		doCommand("MAIL", '3');

		// Send the message headers
		out.writeUTF(getMessageHeaders());

		BufferedReader msgBodyReader = new BufferedReader(new StringReader(message));
		// Send each line of the message
		String line;

		while ((line = msgBodyReader.readLine()) != null) {
			// If the line begins with a ".", put an extra "." in front of it.
			
			  if (line.startsWith(".")) 
				  out.writeUTF("." + line + "\n");
			  else
				  out.writeUTF(line + "\n");
		}
		// A "." on a line by itself ends a message.
		doCommand(".", '2');

        String m = JOptionPane.showInputDialog("Sort the Puzzle : " + puzzle.getPuzzle());
        //System.out.println(m);
		
		//doCommand("PKEY " + checkForPattern(response) + " " + p.getRow() + "," + p.getCol() + " " + sortString(p.getPuzzle()), '2');
		doCommand("PKEY " + checkForPattern(response) + " " + p.getRow() + "," + p.getCol() + " " + m, '2');

		doCommand("QUIT", '2');

		// Message is sent. Close the connection to the server
		 close();
	}
	
	static String checkForPattern(String string) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_-]+.com", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(string);
		boolean matchFound = matcher.find();
		String theGroup = null;
	    if (matchFound)
	    {
	      // we're only looking for one group, so get it
	      theGroup = matcher.group(0);
	    }
		return theGroup;
	}
	
	// Method to sort a string alphabetically 
    public String sortString(String inputString) 
    { 
        // convert input string to char array 
        char tempArray[] = inputString.toCharArray(); 
          
        // sort tempArray 
        Arrays.sort(tempArray); 
          
        // return new sorted string 
        return new String(tempArray); 
    } 
}
