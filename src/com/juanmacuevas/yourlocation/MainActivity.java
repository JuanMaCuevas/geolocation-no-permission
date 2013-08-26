package com.juanmacuevas.yourlocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

	private WifiManager wifi;
	private List<ScanResult> results;
	private int size;
	private WebView webView;

	boolean wifiEnabled = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.webview1);
		webView.setWebViewClient(new WebViewClient());
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiEnabled = wifi.isWifiEnabled();
		if (wifiEnabled == false) {
			
			Toast.makeText(this, "Enabling wifi...", Toast.LENGTH_LONG).show();
			wifi.setWifiEnabled(true);
		}

		registerReceiver(br, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		wifi.startScan();
		Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_LONG).show();
	}
	
	
	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context c, Intent intent) {
			results = wifi.getScanResults();
			size = results.size();
			if (size > 0) {
				new LocationDownloader().execute();
//				getPosition();
			}
		}

	};
	
	
	public class LocationDownloader extends AsyncTask<Void, Void, String>{

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
//			showMap(result);
		}

		@Override
		protected String doInBackground(Void... params) {
			String str = getPosition();
			showMap(str);
			unregisterReceiver(br);
			return "";
		}
		
	}

	private String getPosition() {
		String apns = getAPs();

		if (apns.length() == 0) {
			return "";
		}
		String api = "https://maps.googleapis.com/maps/api/browserlocation/json?browser=firefox&sensor=true";
		api=api+apns;
		Log.i("LOCATION", api);
		HttpsURLConnection urlConnection = null;
		JSONObject json = null;
		try {
			URL url = new URL(api);
			urlConnection = (HttpsURLConnection) url.openConnection();
			json = new JSONObject(readStream(urlConnection.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}

		json = json.optJSONObject("location");
		String lat, lng;
		lat = json.optString("lat");
		lng = json.optString("lng");
return lat+","+lng;
		

	}

	private void showMap(final String latlng) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				webView.loadUrl("http://maps.google.com/maps?q=" + latlng);
				
			}
		});
		
	}

	private String readStream(InputStream in) {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		StringBuilder total = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return total.toString();

	}

	private String getAPs() {
		if ((results == null) || (results.size() == 0))
			return "";

		StringBuilder str = new StringBuilder();
		for (ScanResult r : results) {
			str.append("&wifi=mac:");
			str.append(r.BSSID);
			str.append("|ssid:");
			str.append(r.SSID);
			str.append("|ss:");
			str.append(r.level);
		}

		return str.toString();

	}
}
