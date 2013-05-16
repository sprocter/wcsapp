package com.mthatcher.starcraft2wcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LandingPage extends Activity {

	private final String DATA_URL = "http://skorchedearth.com/sandbox/wcsapp/wcsapp/wcsapp-server/wcsapp.dump.gz";
	private final String DEBUG_TAG = "LANDING PAGE";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        AsyncTask<String, Integer, ArrayList<ScheduleEntry>> task = null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			task = new DownloadDataAndUpdateDBTask(getApplicationContext()).execute(DATA_URL);
		} else {
			//TODO: Handle this.
		}
		
        ScheduleAdapter listAdapter = null;
		try {
			//listAdapter = new ArrayAdapter<String>(this, R.layout.am_row, task.get());
			listAdapter = new ScheduleAdapter(task.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        final ListView listView = (ListView) findViewById(R.id.mainListView);
        listView.setAdapter( listAdapter );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
        return true;
    }
    
    private class ScheduleAdapter extends BaseAdapter {

    	ArrayList<ScheduleEntry> items;
    	
    	public ScheduleAdapter(ArrayList<ScheduleEntry> items){
    		this.items = items;
    	}
    	
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null){
				LayoutInflater vi;
				vi = LayoutInflater.from(getBaseContext());
				v = vi.inflate(R.layout.schedule_row, null);
			}
			
			ScheduleEntry item = items.get(position);
			
			if(item != null){
				TextView tv = (TextView) v.findViewById(R.id.rowTextView);
				if(tv != null){
					tv.setText(item.getName() + " -- " + item.getTime());
					tv.setCompoundDrawablesWithIntrinsicBounds(item.getIcon(), 0, 0, 0);
				}
			}
			return v;
		}
    	
    }

    private class DownloadDataAndUpdateDBTask extends AsyncTask<String, Integer, ArrayList<ScheduleEntry>> {
    	private Context context;
        public DownloadDataAndUpdateDBTask(Context applicationContext) {
        	context = applicationContext;
		}

		@Override
        protected ArrayList<ScheduleEntry> doInBackground(String... urls) {
        	// 1) Download GZipped SQL
        	InputStream is = downloadGzippedSql(urls[0]);
        	// 2) Un-gzip SQL
        	String sql = unGzipSql(is);
        	// 3) Execute SQL
        	WcsDBHelper dbHelper = updateDB(sql);
        	// 4) Retrieve relevant matches
        	ArrayList<ScheduleEntry> matchNames = getMatchNames(dbHelper);
        	// 5) Update UI
			return matchNames;
        }
        
        private ArrayList<ScheduleEntry> getMatchNames(WcsDBHelper dbHelper) {
        	publishProgress(50);
        	ArrayList<ScheduleEntry> ret = new ArrayList<ScheduleEntry>();
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String table = "schedule";
			String[] columns = new String[] {"id", "time", "division", "region", "name"};
			String selection = null;
			String[] selectionArgs = null;
			String groupBy = null;
			String having = null;
			String orderBy = "time";
			Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        	publishProgress(60);
			c.moveToFirst();
			while(!c.isLast()){
				ret.add(new ScheduleEntry(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
				c.move(1);
			}
//			TODO: Start here --> db.compileStatement(sql);
			c.close();
        	publishProgress(70);
			return ret;
		}

		private WcsDBHelper updateDB(String sql) {
        	WcsDBHelper dbHelper = new WcsDBHelper(context);
        	publishProgress(30);
        	dbHelper.updateDB(dbHelper.getWritableDatabase(), sql);
        	publishProgress(40);
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
    }
    
    private enum Division {PREMIER, CHALLENGER};
    private enum Region {AMERICA, EUROPE, KOREA};
    private enum Race {TERRAN, PROTOSS, ZERG};
    private enum Country {SE, SK, US}
    
    private class PlayerEntry{
    	private Race race;
    	private Country country;
    	private String name;
    	
    	public PlayerEntry(String name, String country, String race){
    		this.name = name;
    		this.country = getCountryFromString(country);
    		this.race = getRaceFromString(race);
    	}

		private Race getRaceFromString(String race) {
			if(race.equalsIgnoreCase("z"))
				return Race.ZERG;
			else if(race.equalsIgnoreCase("t"))
				return Race.TERRAN;
			else if(race.equalsIgnoreCase("p"))
				return Race.PROTOSS;
			else
				return null;
		}

		private Country getCountryFromString(String country) {
			if(country.equalsIgnoreCase("us"))
				return Country.US;
			else if(country.equalsIgnoreCase("se"))
				return Country.SE;
			else if(country.equalsIgnoreCase("sk"))
				return Country.SK;
			else 
				return null;
		}
    }
    
    private class ScheduleEntry{
    	private int id;
    	private long time;
       	private Division division;
    	private Region region;
    	private String name;
    	private ArrayList<PlayerEntry> players; 
    	
    	public ScheduleEntry(String id, String time, String division, String region, String name){
    		this.id = Integer.parseInt(id);
    		this.time = Long.parseLong(time);
    		this.division = setDivisionFromString(division);
    		this.region = setRegionFromString(region);
    		this.name = name;
    		players = new ArrayList<PlayerEntry>();
    	}
    	
    	public int getId(){
    		return id;
    	}
    	
    	public void addPlayer(PlayerEntry player){
    		players.add(player);
    	}

		public String getTime() {
			return new Date(time).toString();
		}

		public int getIcon() {
			switch(region){
			case AMERICA:
				if(division == Division.PREMIER)
					return R.drawable.wcsam_logo_small_premier;
				else if(division == Division.CHALLENGER)
					return R.drawable.wcsam_logo_small_challenger;
				else
					return R.drawable.wcsam_logo_small;
			case EUROPE:
				if(division == Division.PREMIER)
					return R.drawable.wcseu_logo_small_premier;
				else if(division == Division.CHALLENGER)
					return R.drawable.wcseu_logo_small_challenger;
				else
					return R.drawable.wcseu_logo_small;
			case KOREA:
				if(division == Division.PREMIER)
					return R.drawable.wcskr_logo_small_premier;
				else if(division == Division.CHALLENGER)
					return R.drawable.wcskr_logo_small_challenger;
				else
					return R.drawable.wcskr_logo_small;
			default: //TODO: Add leagues to generic logo?
				return R.drawable.wcs_logo_small;						
			}
		}

		public String getName() {
			return name;
		}

		private Division setDivisionFromString(String division) {
			if(division.equalsIgnoreCase("P"))
				return Division.PREMIER;
			else if (division.equalsIgnoreCase("C"))
				return Division.CHALLENGER;
			else
				return null;
		}
		
		private Region setRegionFromString(String region){
			if(region.equalsIgnoreCase("AM"))
				return Region.AMERICA;
			else if(region.equalsIgnoreCase("KR"))
				return Region.KOREA;
			else if(region.equalsIgnoreCase("EU"))
				return Region.EUROPE;
			else
				return null;
		}
    }
}
