package de.power;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.control.User;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	
	EditText[] ipField;
	
	EditText inPort;
	EditText outPort;
	
	EditText user;
	EditText password;
	
	final Context context = this;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configactivity);
        
        Log.i("config activity", AnelPowerActivity.udp.getUser().toString() + "  " + (savedInstanceState == null));
        if(savedInstanceState == null || !savedInstanceState.getBoolean("Resume")) {
        	String[] t = AnelPowerActivity.udp.getAddress().getHostAddress().split("\\.");
        	if(t.length == 4) {
        		setConfig(t[0], t[1], t[2], t[3], 
        				AnelPowerActivity.udp.getPortInput() + "", 
        				AnelPowerActivity.udp.getPortOutput() + "",
        				AnelPowerActivity.udp.getUser().getUser(),
        				AnelPowerActivity.udp.getUser().getPassword());
        	}else {
        		setConfig("127", "0", "0", "1", 
        				AnelPowerActivity.udp.getPortInput() + "", 
        				AnelPowerActivity.udp.getPortOutput() + "",
        				AnelPowerActivity.udp.getUser().getUser(),
        				AnelPowerActivity.udp.getUser().getPassword());
        	}
        } else {
        	setConfig(savedInstanceState.getString("ip1"),
        			savedInstanceState.getString("ip2"),
        			savedInstanceState.getString("ip3"),
        			savedInstanceState.getString("ip4"),
        			savedInstanceState.getString("inPort"),
        			savedInstanceState.getString("outPort"),
        			savedInstanceState.getString("user"),
        			savedInstanceState.getString("password"));
        }
    }
    
    public void setConfig(String ip1, String ip2, String ip3, String ip4, String inPort_s, String outPort_s, String username_s, String password_s) {
    	ipField = new EditText[4];
    	
        
        ipField[0] = (EditText) findViewById(R.id.ip1);
        ipField[1] = (EditText) findViewById(R.id.ip2);
        ipField[2] = (EditText) findViewById(R.id.ip3);
        ipField[3] = (EditText) findViewById(R.id.ip4);
       
        ipField[0].setText(ip1);
        ipField[1].setText(ip2);
        ipField[2].setText(ip3);
        ipField[3].setText(ip4);

        
        inPort = (EditText) findViewById(R.id.portIn);
        inPort.setText(inPort_s);
        
        outPort = (EditText) findViewById(R.id.portOut);
        outPort.setText(outPort_s);
        
        user = (EditText) findViewById(R.id.user);
        user.setText(username_s);
        
        password = (EditText) findViewById(R.id.pswd);
        password.setText(password_s);
        
        Button save = (Button) findViewById(R.id.savebtn);
        save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean flag = true;
				for(int i = 0; i < 4; i++) {
					if(!AnelPowerActivity.isIP(ipField[i].getText().toString())) {
						Toast toast = Toast.makeText(context, "Die IP Addresse ist nicht korrekt!", Toast.LENGTH_LONG);			
						toast.show();
						flag = false;
					}
				}
				if(!AnelPowerActivity.isNumeric(inPort.getText().toString())) {
					Toast toast = Toast.makeText(context, "Der Input Port ist nicht korrekt!", Toast.LENGTH_LONG);			
					toast.show();
					flag = false;
				}
				if(!AnelPowerActivity.isNumeric(outPort.getText().toString())) {
					Toast toast = Toast.makeText(context, "Der Output Port ist nicht korrekt!", Toast.LENGTH_LONG);
					toast.show();
					flag = false;
				}
				if(!flag) return;
				
				try {
					AnelPowerActivity.udp.setAddress(InetAddress.getByName(ipField[0].getText().toString() + "."
							+ ipField[1].getText().toString() + "."
							+ ipField[2].getText().toString() + "."
							+ ipField[3].getText().toString()));
				}catch (UnknownHostException e1) {
					Toast toast = Toast.makeText(context, "Die IP Addresse ist nicht korrekt!", Toast.LENGTH_LONG);			
					toast.show();
					flag = false;
				}
				AnelPowerActivity.udp.setPortInput(Integer.parseInt(inPort.getText().toString()));
				AnelPowerActivity.udp.setPortOutput(Integer.parseInt(outPort.getText().toString()));
				AnelPowerActivity.udp.setUser(new User(user.getText().toString(), password.getText().toString()));
				
				//update the UDPReciever
				//if(AnelPowerActivity.udpR != null) {
				//	AnelPowerActivity.udpR.interrupt();
				//	while(AnelPowerActivity.udpR.isAlive()) {
						
				//	}
				//	AnelPowerActivity.udpR = new AnelPowerActivity.UDPReciever(AnelPowerActivity.udp);
				//	AnelPowerActivity.udpR.start();
				//}

				AnelPowerActivity.writeToFile();
				
				finish();
			}
		});
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putBoolean("Resume", true);
    	
    	outState.putString("ip1", ipField[0].getText().toString());
    	outState.putString("ip2", ipField[1].getText().toString());
    	outState.putString("ip3", ipField[2].getText().toString());
    	outState.putString("ip4", ipField[3].getText().toString());
    	
    	outState.putString("inPort", inPort.getText().toString());
    	outState.putString("outPort", outPort.getText().toString());
    	
    	outState.putString("user", user.getText().toString());
    	outState.putString("password", password.getText().toString());
    	
    }
}
