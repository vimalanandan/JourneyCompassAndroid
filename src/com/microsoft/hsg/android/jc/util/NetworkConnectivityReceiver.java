package com.microsoft.hsg.android.jc.util;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

import com.microsoft.hsg.Request;
import com.microsoft.hsg.android.HealthVaultService;
import com.microsoft.hsg.android.PersonInfo;
import com.microsoft.hsg.android.Record;
import com.microsoft.hsg.android.jc.SymptomActivity;
import com.microsoft.hsg.request.SimpleRequestTemplate;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
	private HealthVaultService service;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Log.w("NetworkConnectivityReceiver", "onReceive()");
		if (connectivityMgr == null) {
			return;
		} else if (connectivityMgr.getActiveNetworkInfo() != null
				&& connectivityMgr.getActiveNetworkInfo().isConnected()) {
			SendOfflineRequest sendRequests = new SendOfflineRequest(context);
			sendRequests.execute();
		}

	}

	public boolean inetAddr() {

		boolean x1 = false;

		try {
			Socket s = new Socket("utcnist.colorado.edu", 37);

			InputStream i = s.getInputStream();

			Scanner scan = new Scanner(i);

			while (scan.hasNextLine()) {

				System.out.println(scan.nextLine());
				x1 = true;
			}
		} catch (Exception e) {

			x1 = false;
		}

		return x1;

	}

	private class SendOfflineRequest extends AsyncTask<Void, Void, Void> {
		private String painScale;
		private Exception exception;
		private Context context;

		public SendOfflineRequest(Context context) {
			this.context = context;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		protected Void doInBackground(Void... v) {
			try {

				Log.w("NetworkConnectivityReceiver", "connected");
				ArrayList<Request> requests = DBHandler.getInstance()
						.getRequests();
				service = HealthVaultService.getInstance();
				service.connect(context);
				SimpleRequestTemplate template = null;
				if (service.getConnectionStatus() == HealthVaultService.ConnectionStatus.Connected) {
					while (!inetAddr())
						;
					PersonInfo personInfo = service.getPersonInfoList().get(0);
					Record record = personInfo.getRecords().get(0);
					template = new SimpleRequestTemplate(
							service.getConnection(), record.getPersonId(),
							record.getId());
					Log.w("NetworkConnectivityReceiver", "record details : "+record.getName());
				}
				if (template != null) {

					if (requests != null) {
						ListIterator<Request> iter = requests.listIterator();
						Log.w("NetworkConnectivityReceiver", "request count : "+requests.size());
						while (iter.hasNext()) {
							Request currentRequest = iter.next();
							template.makeRequest(currentRequest);
						}
					}
				}

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

		}
	}

}
