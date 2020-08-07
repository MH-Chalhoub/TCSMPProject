package tcsmp;

import java.net.Socket;

public class MailBox {

    private String mailMsg;
    private int port;
    private String ip;
    private String UserMailAddress;
    private String ServerDomain;
	Socket link;
    
    
	public MailBox(String mailMsg, int port, String ip, String UserMailAddress, String ServerDomain, Socket link) {
		super();
		this.mailMsg = mailMsg;
		this.port = port;
		this.UserMailAddress = UserMailAddress;
		this.ServerDomain = ServerDomain;
		this.ip = ip;
		this.link = link;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMailMsg() {
		return mailMsg;
	}


	public void setMailMsg(String mailMsg) {
		this.mailMsg = mailMsg;
	}

	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getUserMailAddress() {
		return UserMailAddress;
	}

	public void setUserMailAddress(String userMailAddress) {
		UserMailAddress = userMailAddress;
	}

	public String getServerDomain() {
		return ServerDomain;
	}

	public void setServerDomain(String serverDomain) {
		ServerDomain = serverDomain;
	}

	@Override
	public boolean equals(Object o) {

        if (o == this) { 
            return true; 
        }
        if (!(o instanceof MailBox)) { 
            return false; 
        }  
        MailBox c = (MailBox) o; 
		return this.getUserMailAddress().equals(c.getUserMailAddress());
	}
    
    
}
