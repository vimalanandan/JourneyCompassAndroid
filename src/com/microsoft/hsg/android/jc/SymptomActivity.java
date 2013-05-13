package com.microsoft.hsg.android.jc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.hsg.Request;
import com.microsoft.hsg.android.HealthVaultService;
import com.microsoft.hsg.android.PersonInfo;
import com.microsoft.hsg.android.Record;
import com.microsoft.hsg.android.custom.wrapper.CustomHealthTypeWrapper;
import com.microsoft.hsg.android.jc.util.Constants;
import com.microsoft.hsg.android.jc.util.CustomUtil;
import com.microsoft.hsg.android.jc.util.DBHandler;
import com.microsoft.hsg.android.symptom.Condition;
import com.microsoft.hsg.android.symptom.PainScale;
import com.microsoft.hsg.request.SimpleRequestTemplate;


public class SymptomActivity extends Activity implements
		OnSeekBarChangeListener {

	// Seekbar objects
	SeekBar painSeek;
	SeekBar fatigueSeek;
	SeekBar constipationSeek;
	SeekBar nauseaSeek;
	SeekBar sleepSeek;

	TextView painValue;
	TextView sleepValue;
	TextView fatigueValue;
	TextView constipationValue;
	TextView nauseaValue;

	Condition symptoms[];
	// Service object
	private HealthVaultService service;
	// HealthVaultRecord
	private Record selectedRecord;
	PainScale pScale = new PainScale();
	Context context;

	protected SeekBar getPainSeek() {
		return painSeek;
	}

	protected void setPainSeek(SeekBar painSeek) {
		this.painSeek = painSeek;
	}

	protected SeekBar getFatigueSeek() {
		return fatigueSeek;
	}

	protected void setFatigueSeek(SeekBar fatigueSeek) {
		this.fatigueSeek = fatigueSeek;
	}

	protected SeekBar getConstipationSeek() {
		return constipationSeek;
	}

	protected void setConstipationSeek(SeekBar constipationSeek) {
		this.constipationSeek = constipationSeek;
	}

	protected SeekBar getNauseaSeek() {
		return nauseaSeek;
	}

	protected void setNauseaSeek(SeekBar nauseaSeek) {
		this.nauseaSeek = nauseaSeek;
	}

	protected SeekBar getSleepSeek() {
		return sleepSeek;
	}

	protected void setSleepSeek(SeekBar sleepSeek) {
		this.sleepSeek = sleepSeek;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.symptom_activity);
		service = HealthVaultService.getInstance();

		painSeek = (SeekBar) findViewById(R.id.painSeek);
		sleepSeek = (SeekBar) findViewById(R.id.sleepSeek);
		fatigueSeek = (SeekBar) findViewById(R.id.fatigueSeek);
		constipationSeek = (SeekBar) findViewById(R.id.constipationSeek);
		nauseaSeek = (SeekBar) findViewById(R.id.nauseaSeek);

		painValue = (TextView) findViewById(R.id.painValue);
		sleepValue = (TextView) findViewById(R.id.sleepValue);
		fatigueValue = (TextView) findViewById(R.id.fatigueValue);
		constipationValue = (TextView) findViewById(R.id.constipationValue);
		nauseaValue = (TextView) findViewById(R.id.nauseaValue);

		painSeek.setOnSeekBarChangeListener(this);
		fatigueSeek.setOnSeekBarChangeListener(this);
		sleepSeek.setOnSeekBarChangeListener(this);
		constipationSeek.setOnSeekBarChangeListener(this);
		nauseaSeek.setOnSeekBarChangeListener(this);

		Button putThing = (Button) findViewById(R.id.putThing);
		
		symptoms = new Condition[5];
		for (int i = 0; i < 5; i++) {
			symptoms[i] = new Condition();
		}
		putThing.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// EditText text = (EditText) findViewById(R.id.weightInput);

				PutCustom putAction = new PutCustom(Integer.toString(pScale
						.getPainThreshold()));
				// InitializeRecords();
				putAction.execute();
			}
		});
		

	}

	public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
		updatePainScaleObject(bar, progress);

	}

	private void updatePainScaleObject(SeekBar bar, int progress) {
		progress = progress / 10;
		switch (bar.getId()) {
		case R.id.constipationSeek:
			symptoms[0].setName(Constants.CONSTIPATION);
			symptoms[0].setStatus(Integer.toString(progress));
			constipationValue.setText(Integer.toString(progress));
			break;
		case R.id.fatigueSeek:
			symptoms[1].setName(Constants.FATIGUE);
			symptoms[1].setStatus(Integer.toString(progress));
			fatigueValue.setText(Integer.toString(progress));
			break;
		case R.id.nauseaSeek:
			symptoms[2].setName(Constants.NAUSEA);
			symptoms[2].setStatus(Integer.toString(progress));
			nauseaValue.setText(Integer.toString(progress));
			break;
		case R.id.painSeek:
			symptoms[3].setName(Constants.PAIN);
			symptoms[3].setStatus(Integer.toString(progress));
			painValue.setText(Integer.toString(progress));
			break;

		case R.id.sleepSeek:
			symptoms[4].setName(Constants.SLEEP);
			symptoms[4].setStatus(Integer.toString(progress));
			sleepValue.setText(Integer.toString(progress));
			break;

		}
		
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// Notify that the user has started a touch gesture.
		// textView.setText(textView.getText()+"\n"+"SeekBar Touch Started");

	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// Notify that the user has finished a touch gesture.
		// textView.setText(textView.getText()+"\n"+"SeekBar Touch Stopped");

	}

	private class PutCustom extends AsyncTask<Void, Void, Void> {
		private String painScale;
		private Exception exception;
		ProgressDialog progressDialog;

		public PutCustom(String painThreshold) {
			painScale = painThreshold;
			progressDialog = ProgressDialog.show(SymptomActivity.this, "",
					"Please wait for put...", true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		protected Void doInBackground(Void... v) {
			try {
				// EditText text = (EditText) findViewById(R.id.weightInput);
				putCustom(painScale);
			} catch (Exception e) {
				exception = e;
			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void v) {
			progressDialog.dismiss();

			if (exception == null) {
				Toast.makeText(SymptomActivity.this,
						"Symptoms have been saved.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(SymptomActivity.this,
						"An error occurred.  " + exception.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void putCustom(String value) {
		

		for (int i = 0; i < 5; i++) {
			StringBuilder infoBuilder = new StringBuilder();
			infoBuilder.append("<info><thing><type-id>");
			infoBuilder.append(Condition.TYPE);
			infoBuilder.append("</type-id><data-xml>");
			infoBuilder.append(symptoms[i].toXml());
			infoBuilder.append("<common/></data-xml></thing></info>");
			Request request = new Request();

			request.setMethodName("PutThings");
			request.setInfo(infoBuilder.toString());
			
			if (CustomUtil.getInstance().isNetworkAvailable(this)) {
				PersonInfo personInfo = service.getPersonInfoList().get(0);
				Record record = personInfo.getRecords().get(0);
				SimpleRequestTemplate template = new SimpleRequestTemplate(
						service.getConnection(), record.getPersonId(),
						record.getId());
				
				template.makeRequest(request);
			} else {

				PackageManager packMgr = getPackageManager();
				String path = getPackageName();
				try {
					PackageInfo pInfo = packMgr.getPackageInfo(path, 0);
					path = pInfo.applicationInfo.dataDir;
				} catch (NameNotFoundException e) {
					Log.w("MYTAG", "Name not found", e);
				}

				DBHandler.getInstance().addRequest(request);
				DBHandler.getInstance().deleteTemplates();
				DBHandler.getInstance().addTemplate(null);
				// write to db
			}
		}
	}

}
