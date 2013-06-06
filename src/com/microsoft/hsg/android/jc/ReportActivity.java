package com.microsoft.hsg.android.jc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ReportActivity extends Activity {
	private WebView webView;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_activity);
		
		webView = (WebView) findViewById(R.id.reportView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new JCWebViewClient());
		webView.loadUrl("http://journeycompass.i3l.gatech.edu");
		
 
	}
	
	public void loadSymptomActivity(View arg){
		Intent intent = new Intent(ReportActivity.this, SymptomActivity.class);
		ReportActivity.this.startActivity(intent);
	}
	
	public void loadSettingsActivity(View arg){
		Intent intent = new Intent(ReportActivity.this, MainActivity.class);
		ReportActivity.this.startActivity(intent);
	}
}
