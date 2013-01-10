package de.power;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.control.UDPReciever;
import de.control.User;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
        
        ipField = new EditText[4];
        	
        String[] t = AnelPowerActivity.udp.getAddress().getHostAddress().split("\\.");
        ipField[0] = (EditText) findViewById(R.id.ip1);
        ipField[1] = (EditText) findViewById(R.id.ip2);
        ipField[2] = (EditText) findViewById(R.id.ip3);
        ipField[3] = (EditText) findViewById(R.id.ip4);
        if(t.length == 4) {
            ipField[0].setText(t[0]);
            ipField[1].setText(t[1]);
            ipField[2].setText(t[2]);
            ipField[3].setText(t[3]);
        }
        
        inPort = (EditText) findViewById(R.id.portIn);
        inPort.setText(AnelPowerActivity.udp.getPortInput() + "");
        
        outPort = (EditText) findViewById(R.id.portOut);
        outPort.setText(AnelPowerActivity.udp.getPortOutput() + "");
        
        user = (EditText) findViewById(R.id.user);
        user.setText(AnelPowerActivity.udp.getUser().getUser());
        
        password = (EditText) findViewById(R.id.pswd);
        password.setText(AnelPowerActivity.udp.getUser().getPassword());
        
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
				if(AnelPowerActivity.udpR != null) {
					AnelPowerActivity.udpR.interrupt();
					while(AnelPowerActivity.udpR.isAlive()) {
						
					}
					AnelPowerActivity.udpR = new UDPReciever(AnelPowerActivity.udp);
					AnelPowerActivity.udpR.start();
				}

				AnelPowerActivity.writeToFile();
				
				finish();
			}
		});
    }
}
