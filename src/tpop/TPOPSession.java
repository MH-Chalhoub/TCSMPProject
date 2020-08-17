package tpop;

import java.io.*;

import java.net.*;

import java.util.*;

public class TPOPSession {
	/** 15 sec. socket read timeout */
	public static final int SOCKET_READ_TIMEOUT = 15 * 1000;

	protected Socket tpopSocket;
	protected DataInputStream in;
	protected DataOutputStream out;

	private String host;
	private int port;
	private String userName;
	private String password;

	/**
	 * Creates new TPOP session by given TPOP host, username and password. Assumes
	 * TPOP port is 110 (default for TPOP service).
	 */
	public TPOPSession(String host, String userName, String password) {
		this(host, 2000, userName, password);
	}

	/**
	 * Creates new TPOP session by given TPOP host and port, username and password.
	 */
	public TPOPSession(String host, int port, String userName, String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * Throws exception if given server response if negative. According to TPOP
	 * protocol, positive responses start with a '+' and negative start with '-'.
	 */

	protected void checkForError(String response) throws IOException {
		if (response.charAt(0) == '+' || response.charAt(0) == '2') {
			
		}
		else {
			throw new IOException(response);
		}
			
	}

	/**
	 * @return the current number of messages using the TPOP STAT command.
	 */

	public int getMessageCount() throws IOException {
		// Send STAT command
		String response = doCommand("STAT");

		// The format of the response is +OK msg_count size_in_bytes
		// We take the substring from offset 4 (the start of the msg_count) and
		// go up to the first space, then convert that string to a number.

		try {
			String countStr = response.substring(4, response.indexOf(' ', 4));
			int count = (new Integer(countStr)).intValue();
			return count;
		} catch (Exception e) {
			throw new IOException("Invalid response - " + response);
		}

	}

	/**
	 * Get headers returns a list of message numbers along with some sizing
	 * information, and possibly other information depending on the server.
	 */
	public String[] getHeaders() throws IOException {
		doCommand("LIST");
		return getMultilineResponse();
	}

	/**
	 * Gets header returns the message number and message size for a particular
	 * message number. It may also contain other information.
	 */
	public String getHeader(String messageId) throws IOException {
		String response = doCommand("LIST " + messageId);
		return response;
	}

	/**
	 * Retrieves the entire text of a message using the TPOP RETR command.
	 */
	public String getMessage(String messageId) throws IOException {
		doCommand("RETR " + messageId);
		String[] messageLines = getMultilineResponse();
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < messageLines.length; i++) {
			message.append(messageLines[i]);
			message.append("\n");
		}
		System.out.println("S : " + new String(message));
		return new String(message);
	}

	/**
	 * Retrieves the first <linecount> lines of a message using the TPOP TOP
	 * command. Note: this command may not be available on all servers. If it isn't
	 * available, you'll get an exception.
	 */
	public String[] getMessageHead(String messageId, int lineCount) throws IOException {
		doCommand("TOP " + messageId + " " + lineCount);
		return getMultilineResponse();
	}

	/**
	 * Deletes a particular message with DELE command.
	 */
	public void deleteMessage(String messageId) throws IOException {
		doCommand("DELE " + messageId);
	}

	/**
	 * Initiates a graceful exit by sending QUIT command.
	 */
	public void quit() throws IOException {
		doCommand("QUIT");
	}

	/**
	 * Connects to the TPOP server and logs on it with the USER and PASS commands.
	 */

	public void connectAndAuthenticate() throws IOException {
		// Make the connection
		tpopSocket = new Socket(host, port);
		//tpopSocket.setSoTimeout(SOCKET_READ_TIMEOUT);
		in = new DataInputStream(tpopSocket.getInputStream());
		out = new DataOutputStream(tpopSocket.getOutputStream());

		out.writeUTF(userName + "\n");

		// Receive the welcome message
		String response = in.readUTF();
		checkForError(response);

		// Send a USER command to authenticate
		doCommand("USER " + userName);

		// Send a PASS command to finish authentication
		doCommand("PASS " + password);
	}

	/**
	 * Closes down the connection to TPOP server (if open). Should be called if an
	 * exception is raised during the TPOP session.
	 */
	public void close() {
		try {
			in.close();
			out.close();
			tpopSocket.close();
		} catch (Exception ex) {
			// Ignore the exception. Probably the socket is not open.
		}
	}

	/**
	 * Sends a TPOP command and retrieves the response. If the response is negative
	 * (begins with '-'), throws an IOException with received response.
	 */
	protected String doCommand(String command) throws IOException {
		out.writeUTF(command);
		System.out.println("C : " + command);
		out.flush();
		String response = in.readUTF();
		checkForError(response);
		System.out.println("S : " + response);
		return response;
	}

	/**
	 * Retrieves a multi-line TPOP response. If a line contains "." by itself, it is
	 * the end of the response. If a line starts with a ".", it should really have
	 * two "."'s. We strip off the leading ".". If a line does not start with ".",
	 * there should be at least one line more.
	 */
	protected String[] getMultilineResponse() throws IOException {
		ArrayList<String> lines = new ArrayList<String>();

		while (true) {
			String line = in.readUTF();
			System.out.println("S : " + line);

			if (line == null) {
				// Server closed connection
				throw new IOException("Server unawares closed the connection.");
			}

			if (line.equals(".")) {
				System.out.println("No more lines in the server response");
				// No more lines in the server response
				break;
			}

			if ((line.length() > 0) && (line.charAt(0) == '.')) {
				// The line starts with a "." - strip it off.
				line = line.substring(1);
			}

			// Add read line to the list of lines
			lines.add(line);
		}

		
		String response[] = new String[lines.size()];
		lines.toArray(response);
		return response;
	}

}
