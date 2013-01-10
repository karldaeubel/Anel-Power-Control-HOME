package de.power;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;

import de.control.UDPReciever;
import de.control.UDPSender;
import de.control.User;

import de.control.UDP;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AnelPowerActivity extends Activity {
	
	Context context = this;
	
	Spinner spinner;
	
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
	
	static UDP udp = null;
	static File file = null;
	static UDPReciever udpR = null;
	
	public static ToggleButton toggle1 = null;
	public static ToggleButton toggle2 = null;
	public static ToggleButton toggle3 = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Log.i("onCreate", "onCreate()");
        
		readData();
		
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.login_layout);
		dialog.setTitle("Log In");
		
		final EditText username = (EditText) dialog.findViewById(R.id.username);
		username.setText(udp.getUser().getUser());
		
		Button dialogButton = (Button) dialog.findViewById(R.id.loginButton);
		dialogButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText paswd = (EditText) dialog.findViewById(R.id.paswd);
				udp.setUser(new User(username.getText().toString(), paswd.getText().toString()));
				writeToFile();
				dialog.dismiss();
			}
		});
		dialog.show();
		
		toggle1 = (ToggleButton) findViewById(R.id.toggleButton1);
		toggle1.setOnCheckedChangeListener(new myOnCheckChangeListener());
		toggle2 = (ToggleButton) findViewById(R.id.toggleButton2);
		toggle2.setOnCheckedChangeListener(new myOnCheckChangeListener());
		toggle3 = (ToggleButton) findViewById(R.id.toggleButton3);
		toggle3.setOnCheckedChangeListener(new myOnCheckChangeListener());

		Button update = (Button) findViewById(R.id.update);
		update.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UDPSender s = new UDPSender(udp);
				s.update();
			}
		});
			
		spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.value, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		Button send = (Button) findViewById(R.id.timesend);
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText time = (EditText) findViewById(R.id.editText1);
				
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
    	//Log.i("on-Start", "on.Startttttt!!");
    	super.onStart();
    	if(udpR == null) {
    		udpR = new UDPReciever(udp);
    		udpR.start();
    	}
    }
    
    @Override
    public void onStop() {
    	//Log.i("on-Stop", "on'Stopppp!!");
    	super.onStop();
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