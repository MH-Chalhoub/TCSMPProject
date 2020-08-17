package entities;

public class User {
	private String username;
	private String password;
	
	public User() {
		super();
	}
	
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
    public String getDoamin() {
        return username.split("@")[1];
    }
    
	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}
	
	@Override
	public boolean equals(Object o) {

        if (o == this) { 
            return true; 
        }
        if (!(o instanceof User)) { 
            return false; 
        }  
        User c = (User) o; 
		return this.getUsername().equals(c.getUsername()) && this.getPassword().equals(c.getPassword());
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}
	
}
