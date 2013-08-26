package com.juanmacuevas.yourlocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
		// if (wifiEnabled == false) {

		// Toast.makeText(this, "Enabling wifi...", Toast.LENGTH_LONG).show();
		// wifi.setWifiEnabled(true);
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(br);
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(br, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		wifi.startScan();
		Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh) {
			wifi.startScan();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context c, Intent intent) {
			results = wifi.getScanResults();
			size = results.size();
			if (size > 0) {
				new LocationDownloader().execute();
				// getPosition();
			}
		}

	};

	public class LocationDownloader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String str = getPosition();
			if (!TextUtils.isEmpty(str)) {
				showMap(str);
			}
			return null;
		}

	}

	private String getPosition() {
		String apns = getAPs();

		if (apns.length() == 0) {
			return "";
		}
		String api = "https://maps.googleapis.com/maps/api/browserlocation/json?browser=firefox&sensor=true";
		api = api + apns;
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
		if (json == null) {
			return null;
		}

		json = json.optJSONObject("location");
		String lat, lng;
		lat = json.optString("lat");
		lng = json.optString("lng");
		return lat + "," + lng;

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
		try {
		for (ScanResult r : results) {
			str.append("&wifi=mac:");
			str.append(r.BSSID);
			str.append("|ssid:");
				str.append(URLEncoder.encode(r.SSID,"UTF-8"));
			str.append("|ss:");
			str.append(r.level);
		}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str.toString();

	}
}
