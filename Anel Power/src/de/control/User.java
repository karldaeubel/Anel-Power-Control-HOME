package de.control;

public class User {

	private String user;
	private String password;
	
	public User() {
		user = "";
		password = "";
	}
	
	public User(String u, String passw) {
		user = u;
		password = passw;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString() {
		return "User: " + user + " Password: " + password;
	}
}
