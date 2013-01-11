package de.control;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDP {

	private User user;
	private int portInput;
	private int portOutput;
	private InetAddress address;
	
	public UDP() {
		user = new User();
		portInput = 4444;
		portOutput = 4445;
		try {
			address = InetAddress.getLocalHost();
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public UDP(User u, int pI, int pO, InetAddress a) {
		user = u;
		portInput = pI;
		portOutput = pO;
		address = a;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setUser(String user, String password) {
		this.user = new User(user, password);
	}

	public void setPassword(String password) {
		if(this.user == null) {
			this.user = new User("admin", password);
		}else {
			this.user.setPassword(password);
		}
	}
	
	/**
	 * @return the portInput
	 */
	public int getPortInput() {
		return portInput;
	}

	/**
	 * @param portInput the portInput to set
	 */
	public void setPortInput(int portInput) {
		this.portInput = portInput;
	}

	/**
	 * @return the portOutput
	 */
	public int getPortOutput() {
		return portOutput;
	}

	/**
	 * @param portOutput the portOutput to set
	 */
	public void setPortOutput(int portOutput) {
		this.portOutput = portOutput;
	}

	/**
	 * @return the address
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
	}
}
