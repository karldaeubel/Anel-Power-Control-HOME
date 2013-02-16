package de.power;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import de.control.UDPSender;
import de.control.User;

import de.control.UDP;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AnelPowerActivity extends Activity{
	
	Context context = this;
	
	public class myOnCheckChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			UDPSender s = new UDPSender(udp);
			int temp = 0;
			if(buttonView.getId() == R.id.toggleButton1) {
				temp = 1;
			}else if(buttonView.getId() == R.id.toggleButton2) {
				temp = 2;
			}else if(buttonView.getId() == R.id.toggleButton3) {
				temp = 3;
			}
			s.switchOutlet(temp, isChecked);
		}
	}
	
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
			        final String[] alles = ausgabe.split(":");

			        if(alles.length >= 8) {
			        	runOnUiThread(new Runnable() {
								
							@Override
							public void run() {
								toggle1.setChecked(alles[6].endsWith("1") ? true : false);
								toggle2.setChecked(alles[7].endsWith("1") ? true : false);
								toggle3.setChecked(alles[8].endsWith("1") ? true : false);
							}
						});
		        	}			        
				}catch (IOException e) {
				}
			}
			recieveSocket.close();
		}
	}

	
	static UDP udp = null;
	static File file = null;
	static UDPReciever udpR = null;
	
	public static Switch toggle1 = null;
	public static Switch toggle2 = null;
	public static Switch toggle3 = null;
	
	Dialog dialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    	StrictMode.setThreadPolicy(policy);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.i("onCreate", "onCreate()");
        
		readData();
		
		if(savedInstanceState == null || !savedInstanceState.getBoolean("Resume")) {
			Log.i("onCreate()", "savedInstances null!");
			setLogIn(udp.getUser().getUser(), "");
			setGUI(false, false, false, 0, "");
		}else if(savedInstanceState.getBoolean("Resume")){
			Log.i("onCreate", "onCreate()2anel");

			udp.setPassword(savedInstanceState.getString("password_real"));
			
			if(savedInstanceState.getBoolean("Dialog")) {
				Log.i("onCreate", "onCreate()3");
				setLogIn(savedInstanceState.getString("user"), savedInstanceState.getString("password"));
			}
			
			setGUI(savedInstanceState.getBoolean("outlet1"),
					savedInstanceState.getBoolean("outlet2"),
					savedInstanceState.getBoolean("outlet3"),
							savedInstanceState.getInt("spinner"),
							savedInstanceState.getString("timeValue"));
		}
    }
    
    public void setLogIn(String u, String pass) {
    	dialog = new Dialog(context);
		dialog.setContentView(R.layout.login_layout);
		dialog.setTitle("Log In");
		
		
		final EditText username = (EditText) dialog.findViewById(R.id.username);
		username.setText(u);
		
		final EditText paswd = (EditText) dialog.findViewById(R.id.paswd);
		paswd.setText(pass);
		
		Button dialogButton = (Button) dialog.findViewById(R.id.loginButton);
		dialogButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				udp.setUser(new User(username.getText().toString(), paswd.getText().toString()));
				Log.i("klick!!", udp.getUser().toString());
				writeToFile();
				dialog.dismiss();
			}
		});
		
		CheckBox box = (CheckBox) dialog.findViewById(R.id.checkBox1);
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					paswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				}else {
					paswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
		
		dialog.show();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	Log.i("onCreate", "onSaveInstance");
    	super.onSaveInstanceState(outState);
    	
    	outState.putBoolean("Resume", true);
    	
    	if(dialog != null) {
    		outState.putString("user", ((EditText) dialog.findViewById(R.id.username)).getText().toString());
    		outState.putString("password", ((EditText) dialog.findViewById(R.id.paswd)).getText().toString());
    	}
    	
    	outState.putString("password_real", udp.getUser().getPassword());
    	
    	outState.putString("timeValue", ((EditText) findViewById(R.id.editText1)).getText().toString());
    	outState.putInt("spinner", ((Spinner) findViewById(R.id.spinner1)).getSelectedItemPosition());
    	
    	outState.putBoolean("outlet1", toggle1.isChecked());
    	outState.putBoolean("outlet2", toggle2.isChecked());
    	outState.putBoolean("outlet3", toggle3.isChecked());
    	    	
    	if(dialog != null && dialog.isShowing()) {
    		outState.putBoolean("Dialog", true);
    	}else {
    		outState.putBoolean("Dialog", false);
    	}
    }
    
    public void setGUI(boolean togg1, boolean togg2, boolean togg3, int spin, String timeValue) {
    	toggle1 = (Switch) findViewById(R.id.toggleButton1);
    	toggle1.setChecked(togg1);
		toggle1.setOnCheckedChangeListener(new myOnCheckChangeListener());
		
		toggle2 = (Switch) findViewById(R.id.toggleButton2);
		toggle2.setChecked(togg2);
		toggle2.setOnCheckedChangeListener(new myOnCheckChangeListener());
		
		toggle3 = (Switch) findViewById(R.id.toggleButton3);
		toggle3.setChecked(togg3);
		toggle3.setOnCheckedChangeListener(new myOnCheckChangeListener());

		Button update = (Button) findViewById(R.id.update);
		update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UDPSender s = new UDPSender(udp);
				s.update();
			}
		});
			
		final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.value, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(spin);
		
		final EditText time = (EditText) findViewById(R.id.editText1);
		time.setText(timeValue);
		
		Button send = (Button) findViewById(R.id.timesend);
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(spinner.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
					Toast toast = Toast.makeText(context, "Keine Steckdose ausgewählt!", Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				if(!isNumeric(time.getText().toString())) {
					Toast toast = Toast.makeText(context, "Falsches Zeitformat!", Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				UDPSender s = new UDPSender(udp);
				int outlet = Integer.parseInt(String.valueOf(spinner.getSelectedItem()));
				int timeForamt = Integer.parseInt(time.getText().toString());
				if(timeForamt > 65535 || timeForamt < 0) {
					Toast toast = Toast.makeText(context, "Die Zeit t ist nicht korrekt! (0 <= t <= 65535)", Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				s.delayedOutlet(outlet, timeForamt);
			}
		});
		
        Button config = (Button) findViewById(R.id.configbtn);
        config.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(AnelPowerActivity.this, ConfigActivity.class);
				startActivity(in);
			}
		});
    }
    
    @Override
    public void onStart() {
    	Log.i("on-Start", "on.Startttttt!!");
    	super.onStart();
    	if(udpR == null) {
    		udpR = new UDPReciever(udp);
    		udpR.start();
    	}
    }
    
    @Override
    public void onPause() {
    	Log.i("on-Stop", "onPause()!!");
    	super.onPause();
    }
    
    @Override
    public void onStop() {
    	Log.i("on-Stop", "on'Stopppp!!");
    	super.onStop();
    	if(dialog != null) {
    		dialog.dismiss();
    	}
    	if(!udpR.isInterrupted()) {
    		udpR.interrupt();
    		while(udpR.isAlive()) {
    		
    		}
    	}
    	udpR = null;
    }
    
    public static void writeToFile() {
    	try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			StringBuilder str = new StringBuilder();
			str.append("IP:" + udp.getAddress().getHostAddress() + "\n");
			str.append("PortIn:" + udp.getPortInput() + "\n");
			str.append("PortOut:" + udp.getPortOutput() + "\n");
			str.append("Name:" + udp.getUser().getUser() + "\n");
			
			w.write(str.toString());
			w.flush();
			w.close();
		}catch (IOException e) {
			Log.e("FileWriter", e.getMessage());
		}
    }
    
    private void readData() {
		udp = new UDP();
		file = getFileStreamPath("config.pw");
		if(!file.exists()) {
			try {
				file.createNewFile();
				writeToFile();
			}catch (IOException e) {
				Log.i("readData()","Es konnte keine Konfigurations Datei erstellt werden!" + e.getMessage());
			}
			
		}else {
			try {
				BufferedReader r = new BufferedReader( new FileReader(file));
				String temp = null;
				while((temp = r.readLine()) != null) {
					String[] all = temp.split(":");
					if(all[0].equalsIgnoreCase("IP")) {
						if(all.length > 1) {
							udp.setAddress(InetAddress.getByName(all[1]));
						}
					}else if(all[0].equalsIgnoreCase("PortIn")) {
						if(all.length > 1 && isNumeric(all[1])) {
							udp.setPortInput(Integer.parseInt(all[1]));
						}
					}else if(all[0].equalsIgnoreCase("PortOut")) {
						if(all.length >1 && isNumeric(all[1])) {
							udp.setPortOutput(Integer.parseInt(all[1]));
						}
					}else if(all[0].equalsIgnoreCase("Name")) {
						if(all.length > 1) {
							udp.setUser(new User(all[1], ""));
						}
					}
				}
			}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
    
    public static boolean isNumeric(String val) {
		char[] temp = val.toCharArray();
		if(temp.length < 1) return false;
		for(int j = 0; j < temp.length; j++) {
			if(!Character.isDigit(temp[j])) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isIP(String val) {
		char[] temp = val.toCharArray();

		if(temp.length < 1 || temp.length > 3) return false;
		for(int j = 0; j < temp.length; j++) {
			if(!Character.isDigit(temp[j])) {
				return false;
			}
		}
		int t = Integer.parseInt(val);
		if(t < 0 || t > 255) return false;
		
		return true;
	}
}