package de.control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPSender {

	private UDP udp;
	
	private DatagramSocket sendSocket;
	
	public UDPSender() {
		udp = new UDP();
		try {
			sendSocket = new DatagramSocket();
		}catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public UDPSender(UDP p) {
		udp = p;
		try {
			sendSocket = new DatagramSocket();
		}catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void switchOutlet(int outlet, boolean sw) {
		String ooo = sw ? "on" : "off";
		String temp = "Sw_" + ooo + outlet + udp.getUser().getUser() + udp.getUser().getPassword();
        byte[] out = temp.getBytes();

        DatagramPacket send = new DatagramPacket(out, out.length, udp.getAddress(), udp.getPortOutput());
        if(sendSocket != null) {
        	try {
        		sendSocket.send(send);
        	}catch (IOException e) {
        		e.printStackTrace();
        	}
        }
	}
	
	public void switchOutlets(boolean[] outlets) {
		byte ooo = 0;
		for(int i = 0; i < outlets.length; i++) {
			ooo |= outlets[i] ? (1 << i) : 0;
		}
		
		String temp = "Sw" + 0 + udp.getUser().getUser() + udp.getUser().getPassword();
        byte[] out = temp.getBytes();
        out[2] = ooo;
        DatagramPacket send = new DatagramPacket(out, out.length, udp.getAddress(), udp.getPortOutput());
        if(sendSocket != null) {
        	try {
        		sendSocket.send(send);
        	}catch (IOException e) {
        		e.printStackTrace();
        	}
        }
	}
	
	public void update() {
		String temp = "wer da?";
        byte[] out = temp.getBytes();

        DatagramPacket send = new DatagramPacket(out, out.length, udp.getAddress(), udp.getPortOutput());
        if(sendSocket != null) {
        	try {
        		sendSocket.send(send);
        	}catch (IOException e) {
        		e.printStackTrace();
        	}
        }
	}
	
	public void delayedOutlet(int outlet, int time) {
		String temp = "St_off" + outlet + 0 + 0 + udp.getUser().getUser() + udp.getUser().getPassword();
        byte[] out = temp.getBytes();
        out[8] = (byte)time;
        out[7] = (byte) (time >> 8);
        DatagramPacket send = new DatagramPacket(out, out.length, udp.getAddress(), udp.getPortOutput());
        if(sendSocket != null) {
        	try {
        		sendSocket.send(send);
        	}catch (IOException e) {
        		e.printStackTrace();
        	}
        }
	}
	
	public void setUDP(UDP p) {
		udp = p;
	}
}