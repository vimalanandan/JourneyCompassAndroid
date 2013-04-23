package com.microsoft.hsg.android.custom;

import com.microsoft.hsg.Request;
import com.microsoft.hsg.android.HealthVaultService;
import com.microsoft.hsg.android.PersonInfo;
import com.microsoft.hsg.android.Record;
import com.microsoft.hsg.android.custom.painscale.PainScale;
import com.microsoft.hsg.android.custom.util.CustomUtil;
import com.microsoft.hsg.android.custom.util.DBHandler;
import com.microsoft.hsg.android.custom.wrapper.CustomHealthTypeWrapper;
import com.microsoft.hsg.request.SimpleRequestTemplate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SymptomActivity extends Activity implements
		OnSeekBarChangeListener {
	
	//Seekbar objects
	private SeekBar painSeek;
	private SeekBar fatigueSeek;
	private SeekBar constipationSeek;
	private SeekBar nauseaSeek;
	private SeekBar sleepSeek;
	
	private TextView painValue, sleepValue, fatigueValue, constipationValue, nauseaValue;
	//Service object
	private HealthVaultService service;
	//HealthVaultRecord
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
		
		SeekBar painSeek = (SeekBar) findViewById(R.id.painSeek);
		SeekBar sleepSeek = (SeekBar) findViewById(R.id.sleepSeek);
		SeekBar fatigueSeek = (SeekBar) findViewById(R.id.fatigueSeek);
		SeekBar constipationSeek = (SeekBar) findViewById(R.id.constipationSeek);
		SeekBar nauseaSeek = (SeekBar) findViewById(R.id.nauseaSeek);
		
	
		
		TextView painValue = (TextView) findViewById(R.id.painValue);
		TextView sleepValue = (TextView) findViewById(R.id.sleepValue);
		TextView fatigueValue = (TextView) findViewById(R.id.fatigueValue);
		TextView constipationValue = (TextView) findViewById(R.id.constipationValue);
		TextView nauseaValue = (TextView) findViewById(R.id.nauseaValue);
		
		
		Button putThing = (Button) findViewById(R.id.putThing);
		putThing.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// EditText text = (EditText) findViewById(R.id.weightInput);

				PutCustom putAction = new PutCustom(Integer.toString(pScale
						.getPainThreshold()));
				// InitializeRecords();
				putAction.execute();
			}
		});
		
		painSeek.setOnSeekBarChangeListener(this);
		fatigueSeek.setOnSeekBarChangeListener(this);
		sleepSeek.setOnSeekBarChangeListener(this);
		constipationSeek.setOnSeekBarChangeListener(this);
		nauseaSeek.setOnSeekBarChangeListener(this);
	}

	public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
		updatePainScaleObject(bar, progress);

	}



	private void updatePainScaleObject(SeekBar bar, int progress){
		progress = progress/10;
		switch(bar.getId()){
		case R.id.painSeek:
			pScale.setPainThreshold(progress);
			painValue.setText(Integer.toString(progress));
			break;
		case R.id.fatigueSeek:
			pScale.setFatigueThreshold(progress);
			fatigueValue.setText(Integer.toString(progress));
			break;
		case R.id.sleepSeek:
			pScale.setSleepThreshold(progress);
			sleepValue.setText(Integer.toString(progress));
			break;
		case R.id.constipationSeek:
			pScale.setConstipationThreshold(progress);
			constipationValue.setText(Integer.toString(progress));
			break;
		case R.id.nauseaSeek:
			pScale.setNauseaThreshold(progress);
			nauseaValue.setText(Integer.toString(progress));
			break;		
		}
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
				Toast.makeText(SymptomActivity.this, "Symptoms have been saved.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(SymptomActivity.this,
						"An error occurred.  " + exception.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void putCustom(String value) {
		CustomHealthTypeWrapper customWrapper = new CustomHealthTypeWrapper(
				PainScale.class.getSimpleName().toString(), pScale);
		pScale.setWrapper(customWrapper);
		Request request = new Request();
		StringBuilder infoBuilder = new StringBuilder();
		if (CustomUtil.getInstance().isNetworkAvailable(this)) {
			PersonInfo personInfo = service.getPersonInfoList().get(0);
			Record record = personInfo.getRecords().get(0);
			SimpleRequestTemplate template = new SimpleRequestTemplate(
					service.getConnection(), record.getPersonId(),
					record.getId());
			// SimpleRequestTemplate template = new
			// SimpleRequestTemplate(service.getConnection());

			
			infoBuilder.append("<info><thing><type-id>");
			infoBuilder.append(CustomHealthTypeWrapper.TYPE);
			infoBuilder.append("</type-id><data-xml>");
			infoBuilder.append(customWrapper.toXml());
			infoBuilder.append("<common/></data-xml></thing></info>");

			
			request.setMethodName("PutThings");
			request.setInfo(infoBuilder.toString());
			template.makeRequest(request);
		} else {
			DBHandler.getInstance().setDirectory(SymptomActivity.this.getFilesDir().getPath());
			DBHandler.getInstance().addRequest(request);
			DBHandler.getInstance().deleteTemplates();
			DBHandler.getInstance().addTemplate(null);
			// write to db
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}
