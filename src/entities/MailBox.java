package entities;

import java.util.ArrayList;

public class MailBox {
    
	private String user;
	private ArrayList<Mail> mails;
    

	public MailBox() {
	}
	
	public MailBox(String user, ArrayList<Mail> mails) {
		super();
		this.user = user;
		this.mails = mails;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public ArrayList<Mail> getMails() {
		return mails;
	}

	public void setMails(ArrayList<Mail> mails) {
		this.mails = mails;
	}
	
	public void addNewMail(Mail mail) {
		mails.add(mail);
	}
	public int getMailBoxMessagesNb() {
		return mails.size();
	}
	public int getMailBoxMessagesSize() {
		int i = 0;
		for(Mail m : mails) {
			i += m.getMailSize();
		}
		return i;
	}
	public boolean deleteMail(int mailNb) {
		if(mailNb <= mails.size()) {
			mails.remove(mailNb);
			return true;
		}
		else
			return false;
	}

	@Override
	public String toString() {
		return "MailBox [user=" + user + ", mails=" + mails.toString() + "]";
	}
	
    
}
