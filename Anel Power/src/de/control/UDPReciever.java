package de.control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;

import de.power.AnelPowerActivity;

public class UDPReciever extends Thread{

	UDP udp;
	
	DatagramSocket recieveSocket;
	DatagramPacket recieve;
	
	public UDPReciever() {
		udp = new UDP();
		try {
			recieveSocket = new DatagramSocket(udp.getPortInput());
			recieveSocket.setSoTimeout(500);
		}catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public UDPReciever(UDP p) {
		udp = p;
		try {
			recieveSocket = new DatagramSocket(udp.getPortInput());
			recieveSocket.setSoTimeout(500);
		}catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		byte[] in = null;
		recieve = null;
		in = new byte[512];
		recieve = new DatagramPacket(in, in.length);
		while(!isInterrupted()) {
	        	
			try {
				//Log.i("recieve!°°°°°!!", "läuft immer noch?");
				recieveSocket.receive(recieve);
				// display response
		        String ausgabe = new String(recieve.getData());
		        String[] alles = ausgabe.split(":");

		        if(alles.length >= 8) {
		        	AnelPowerActivity.toggle1.setChecked(alles[6].endsWith("1") ? true : false);
		        	AnelPowerActivity.toggle2.setChecked(alles[7].endsWith("1") ? true : false);
		        	AnelPowerActivity.toggle3.setChecked(alles[8].endsWith("1") ? true : false);
		        }
			}catch (IOException e) {
			}
		}
		recieveSocket.close();
	}
}
