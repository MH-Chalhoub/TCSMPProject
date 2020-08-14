package mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/*
 * Date: Sat Aug 08 07:52:48 EEST 2020
 * Sender: X@POUET.com
 * From: X@POUET.com
 * To: Y@BINIOU.com
 * Subject: Some subject
 * 
 * 
 * ... Message text ...
 * .
 * 
 */
public class Mail {
	String date;
	String sender,reciver;
	String subject;
	String mailData = "";
	
	public Mail(String date, String sender, String reciver, String subject, String mailData) {
		this.date = date;
		this.sender = sender;
		this.reciver = reciver;
		this.subject = subject;
		this.mailData = mailData;
	}

	public Mail(String fullMail) throws IOException {
		devideMsg(fullMail);
	}
	
	public void devideMsg(String fullMail) throws IOException {

		BufferedReader msgBodyReader = new BufferedReader(new StringReader(fullMail));
		// Send each line of the message
		String line;

		while ((line = msgBodyReader.readLine()) != null) {
			// If the line begins with a ".", put an extra "." in front of it.
			String [] tokens = line.split(":", 0);
			
			if(tokens[0].equals("Date")) {
				this.date = tokens[1].replace("\n", "").replace("\r", "").trim();
			}
			else if(tokens[0].equals("Sender")) {
				
			}
			else if(tokens[0].equals("From")) {
				this.sender = tokens[1].replace("\n", "").replace("\r", "").trim();
			}
			else if(tokens[0].equals("To")) {
				this.reciver = tokens[1].replace("\n", "").replace("\r", "").trim();
			}
			else if(tokens[0].equals("Subject")) {
				this.subject = tokens[1].replace("\n", "").replace("\r", "").trim();
			}
			else {
				if(!line.equals(".\n") && line!=null)
					this.mailData += line;
			}
		}
		this.mailData = this.mailData.replaceAll("^[\\n\\r\\n\\r]", "");
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciver() {
		return reciver;
	}

	public void setReciver(String reciver) {
		this.reciver = reciver;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMailData() {
		return mailData;
	}

	public void setMailData(String mailData) {
		this.mailData = mailData;
	}
	

}
