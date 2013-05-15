package com.mthatcher.starcraft2wcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;

public class LandingPage extends Activity {

	private final String DATA_URL = "http://skorchedearth.com/sandbox/wcsapp/wcsapp/wcsapp-server/wcsapp.dump.gz";
	private final String DEBUG_TAG = "LANDING PAGE";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadDataAndUpdateDBTask(getApplicationContext()).execute(DATA_URL);
		} else {
			//TODO: Handle this.
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
        return true;
    }

    private class DownloadDataAndUpdateDBTask extends AsyncTask<String, Integer, Boolean> {
    	private Context context;
        public DownloadDataAndUpdateDBTask(Context applicationContext) {
        	context = applicationContext;
		}

		@Override
        protected Boolean doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
        	// 1) Download GZipped SQL
        	InputStream is = downloadGzippedSql(urls[0]);
        	// 2) Un-gzip SQL
        	String sql = unGzipSql(is);
        	// 3) Execute SQL
        	WcsDBHelper dbHelper = updateDB(sql);
        	// 4) Retrieve relevant matches
        	SQLiteDatabase db = dbHelper.getReadableDatabase();
        	// 5) Update UI
			return true;
        }
        
        private WcsDBHelper updateDB(String sql) {
        	WcsDBHelper dbHelper = new WcsDBHelper(context, sql);
        	publishProgress(30);
        	return dbHelper;
		}

		private String unGzipSql(InputStream is) {
        	try {
				GZIPInputStream gzis = new GZIPInputStream(is);
				InputStreamReader isreader = new InputStreamReader(gzis, "US-ASCII");
				BufferedReader buffReader = new BufferedReader(isreader);
				String line = "";
				StringBuilder strBuilder = new StringBuilder();
				while((line = buffReader.readLine()) != null){
					strBuilder.append(line);
				}
				publishProgress(20);
				return strBuilder.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		private InputStream downloadGzippedSql(String urlAsStr) {
//        	try{
				
				try {
					URL url = new URL(urlAsStr);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					// Timeouts are in ms..
					conn.setReadTimeout(10000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					conn.connect();
					publishProgress(10);
					int response = conn.getResponseCode();
					Log.d(DEBUG_TAG, "The response is: " + response);
					return conn.getInputStream();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		}
        
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		// onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean result) {
            //TODO: Call refresh / repaint to display new dataz
       }
    }
}
