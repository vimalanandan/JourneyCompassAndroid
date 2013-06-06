package com.microsoft.hsg.android.jc;




import java.io.File;

import com.microsoft.hsg.android.jc.util.ConnectivityService;
import com.microsoft.hsg.android.jc.util.DBHandler;
import com.microsoft.hsg.android.HealthVaultFileSettings;
import com.microsoft.hsg.android.HealthVaultService;
import com.microsoft.hsg.android.HealthVaultSettings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private HealthVaultService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		File f = this.getDir("request", Context.MODE_PRIVATE);
		DBHandler.getInstance().setDirectory(f.getPath());
	//	Intent conService = new Intent(MainActivity.this, ConnectivityService.class);
	//	MainActivity.this.startService(conService); 
		HealthVaultSettings settings = new HealthVaultFileSettings(this);
        //settings.setMasterAppId("b8b45670-ed69-465c-8fab-41ec95e12b45");
        //settings.setMasterAppId("47aaa335-ef45-4729-862f-b154a36f09b2");
		settings.setMasterAppId("acbf2219-b590-4db4-b742-76bcfa66943a");
       // settings.setAppId("b8b45670-ed69-465c-8fab-41ec95e12b45");
        settings.setServiceUrl("https://platform.healthvault-ppe.com/platform/wildcat.ashx");
        settings.setShellUrl("https://account.healthvault-ppe.com");
        
        HealthVaultService.initialize(settings);
        service = HealthVaultService.getInstance();
        
        Button go = (Button) findViewById(R.id.goButton);
        go.setOnClickListener(new View.OnClickListener() {
            private ProgressDialog progress;

            public void onClick(View view) {
                progress = ProgressDialog.show(
                		MainActivity.this, 
                    "",
                    "Please wait...", 
                    true);
                
                new AsyncTask<Context, Void, Intent>() {
                    private Exception exception;
                    
                    protected Intent doInBackground(Context... context) {
                        try
                        {    
                            return service.connect(MainActivity.this);
                        }
                        catch(Exception e)
                        {
                            exception = e;
                        }
                        return null;
                    }
                   
                   @Override
                   protected void onPostExecute(Intent intent) {
                       progress.dismiss();
                       if (exception != null) {
                           Toast.makeText(
                                MainActivity.this, 
                                exception.getMessage(), 
                                Toast.LENGTH_LONG).show();
                       } else {
                           
                           if (intent != null) {
                               startActivity(intent);
                           }
                           else
                           {
                               //intent = new Intent(MainActivity.this, CustomActivity.class);
                        	   intent = new Intent(MainActivity.this, SymptomActivity.class);
                               startActivity(intent);
                               finish();
                           }
                       }
                   }
                }.execute();
            }
        });
	}
	@Override
    protected void onResume() {
        InitializeControls();
        super.onResume();
    }
	public void loadSymptomActivity(View arg){
		Intent intent = new Intent(MainActivity.this, SymptomActivity.class);
		MainActivity.this.startActivity(intent);
	}
    
	public void loadReportActivity(View arg){
		Intent intent = new Intent(MainActivity.this, ReportActivity.class);
		MainActivity.this.startActivity(intent);
	}
	private void InitializeControls() {
        TextView msg = (TextView)findViewById(R.id.welcomeText);
        switch(service.getConnectionStatus()) {
        case Connected:
            msg.setText("Click button to go to next screen");
            break;
        case NotConnected:
            msg.setText("Connect this application to HealthVault");
            break;
        }
    }

}
