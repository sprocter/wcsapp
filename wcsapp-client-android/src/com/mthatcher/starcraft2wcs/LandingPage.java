package com.mthatcher.starcraft2wcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LandingPage extends Activity {

	private final String DATA_URL = "http://skorchedearth.com/sandbox/wcsapp/wcsapp/wcsapp-server/wcsapp.dump.gz";
	private final String DEBUG_TAG = "LANDING PAGE";
	public int startPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		AsyncTask<String, Integer, ArrayList<ScheduleEntry>> task = null;
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			task = new DownloadDataAndUpdateDBTask(getApplicationContext())
					.execute(DATA_URL);
		} else {
			// TODO: Handle this.
		}

		ScheduleAdapter listAdapter = null;
		try {
			// listAdapter = new ArrayAdapter<String>(this, R.layout.am_row,
			// task.get());
			listAdapter = new ScheduleAdapter(task.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final ListView listView = (ListView) findViewById(R.id.mainListView);
		listView.setAdapter(listAdapter);
		listView.setSelection(startPos);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landing_page, menu);
		return true;
	}

	private class ScheduleAdapter extends BaseAdapter {

		ArrayList<ScheduleEntry> items;

		@Override
		public boolean hasStableIds(){
			return true;
		}
		
		public int getStartingPosition() {
			// TODO Auto-generated method stub
			return 0;
		}

		public ScheduleAdapter(ArrayList<ScheduleEntry> items) {
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
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater vi;
				vi = LayoutInflater.from(getBaseContext());
				convertView = vi.inflate(R.layout.group_stage_tbl, parent, false);
				convertView.setPadding(15, 15, 15, 15);
				holder = new ViewHolder();
				holder.groupName = (TextView) convertView.findViewById(R.id.schedule_name);
				holder.date = (TextView) convertView.findViewById(R.id.schedule_date);
				holder.playerName[0] = (TextView) convertView.findViewById(R.id.player_1_name);
				holder.playerName[1] = (TextView) convertView.findViewById(R.id.player_2_name);
				holder.playerName[2] = (TextView) convertView.findViewById(R.id.player_3_name);
				holder.playerName[3] = (TextView) convertView.findViewById(R.id.player_4_name);
				holder.rank[0] = (TextView) convertView.findViewById(R.id.player_1_rank);
				holder.rank[1] = (TextView) convertView.findViewById(R.id.player_2_rank);
				holder.rank[2] = (TextView) convertView.findViewById(R.id.player_3_rank);
				holder.rank[3] = (TextView) convertView.findViewById(R.id.player_4_rank);
				holder.flag[0] = (TextView) convertView.findViewById(R.id.player_1_flag);
				holder.flag[1] = (TextView) convertView.findViewById(R.id.player_2_flag);
				holder.flag[2] = (TextView) convertView.findViewById(R.id.player_3_flag);
				holder.flag[3] = (TextView) convertView.findViewById(R.id.player_4_flag);
				holder.race[0] = (TextView) convertView.findViewById(R.id.player_1_race);
				holder.race[1] = (TextView) convertView.findViewById(R.id.player_2_race);
				holder.race[2] = (TextView) convertView.findViewById(R.id.player_3_race);
				holder.race[3] = (TextView) convertView.findViewById(R.id.player_4_race);
				holder.matchScore[0] = (TextView) convertView.findViewById(R.id.player_1_match_score);
				holder.matchScore[1] = (TextView) convertView.findViewById(R.id.player_2_match_score);
				holder.matchScore[2] = (TextView) convertView.findViewById(R.id.player_3_match_score);
				holder.matchScore[3] = (TextView) convertView.findViewById(R.id.player_4_match_score);
				holder.mapScore[0] = (TextView) convertView.findViewById(R.id.player_1_map_score);
				holder.mapScore[1] = (TextView) convertView.findViewById(R.id.player_2_map_score);
				holder.mapScore[2] = (TextView) convertView.findViewById(R.id.player_3_map_score);
				holder.mapScore[3] = (TextView) convertView.findViewById(R.id.player_4_map_score);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ScheduleEntry item = items.get(position);

			if (item != null) {
				getGroupView(holder, item);
			}
			return convertView;
		}

		private void getGroupView(ViewHolder holder, ScheduleEntry item) {
			int bgColor = item.getColor();
			int numPlayers = item.getNumPlayers();
			holder.groupName.setText(item.getName());
			holder.groupName.setCompoundDrawablesWithIntrinsicBounds(item.getTitleDrawable(), 0, 0, 0);
			holder.groupName.setBackgroundColor(bgColor);
			holder.date.setText(item.getTime());
			for(int i = 0; i < numPlayers; i++){
				GroupPlayerEntry player = item.getPlayer(i);
				bgColor = player.getBackgroundColor();
				holder.rank[i].setText(Integer.toString(player.getPlace()));
				holder.rank[i].setBackgroundColor(bgColor);
				holder.flag[i].setCompoundDrawablesWithIntrinsicBounds(player.getFlagDrawable(), 0, 0, 0);
				holder.flag[i].setBackgroundColor(bgColor);
				holder.race[i].setCompoundDrawablesWithIntrinsicBounds(player.getRaceDrawable(), 0, 0, 0);
				holder.race[i].setBackgroundColor(bgColor);
				holder.playerName[i].setText(player.getName());
				holder.playerName[i].setBackgroundColor(bgColor);
				holder.matchScore[i].setText(Integer.toString(player
						.getMatchesWon())
						+ "-"
						+ Integer.toString(player.getMatchesLost()));
				holder.matchScore[i].setBackgroundColor(bgColor);
				holder.mapScore[i].setText(Integer.toString(player
						.getMapsWon())
						+ "-"
						+ Integer.toString(player.getMapsLost()));
				holder.mapScore[i].setBackgroundColor(bgColor);
			}
		}

	}
	
	private static class ViewHolder{
		TextView groupName;
		TextView date;
		TextView rank[] = new TextView[4];
		TextView flag[] = new TextView[4];
		TextView race[] = new TextView[4];
		TextView playerName[] = new TextView[4];
		TextView matchScore[] = new TextView[4];
		TextView mapScore[] = new TextView[4];
	}

	private class DownloadDataAndUpdateDBTask extends
			AsyncTask<String, Integer, ArrayList<ScheduleEntry>> {
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
			ArrayList<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String table = "schedule";
			String[] columns = new String[] { "id", "time", "division",
					"region", "name" };
			String selection = null;
			String[] selectionArgs = null;
			String groupBy = null;
			String having = null;
			String orderBy = "time";
			Cursor c = db.query(table, columns, selection, selectionArgs,
					groupBy, having, orderBy);
			publishProgress(60);
			c.moveToFirst();
			long currentTime = System.currentTimeMillis();
			boolean startPosFound = false;
			while (!c.isLast()) {
				if(!startPosFound && Long.parseLong(c.getString(1)) > currentTime){
					startPosFound = true;
					startPos = entries.size() > 2 ? entries.size() - 2 : 0; 
				}
				entries.add(new ScheduleEntry(c.getString(0), c.getString(1), c
						.getString(2), c.getString(3), c.getString(4)));
				c.move(1);
			}
			c.close();
			publishProgress(70);
			table = "participants";
			columns = new String[] { "id", "name", "flag", "race", "place",
					"matcheswon", "matcheslost", "mapswon", "mapslost",
					"result", "scheduleid" };
			orderBy = null;
			for (ScheduleEntry entry : entries) {
				selection = "scheduleid = " + String.valueOf(entry.getId());
				c = db.query(table, columns, selection, selectionArgs, groupBy,
						having, orderBy);
				c.moveToFirst();
				if (c.getCount() == 0) {
					c.close();
					continue;
				}
				while (!c.isLast()) {
					entry.addPlayer(new GroupPlayerEntry(c.getString(1), c
							.getString(2), c.getString(3), c.getString(4), c
							.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8),
							c.getString(9)));
					c.move(1);
				}
				entry.addPlayer(new GroupPlayerEntry(c.getString(1), c
						.getString(2), c.getString(3), c.getString(4), c
						.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8), c
						.getString(9)));
				c.close();
			}
			publishProgress(80);
			return entries;
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
				InputStreamReader isreader = new InputStreamReader(gzis,
						"US-ASCII");
				BufferedReader buffReader = new BufferedReader(isreader);
				String line = "";
				StringBuilder strBuilder = new StringBuilder();
				while ((line = buffReader.readLine()) != null) {
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
			// try{

			try {
				URL url = new URL(urlAsStr);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
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

	private enum Division {
		PREMIER, CHALLENGER
	};

	private enum Region {
		AMERICA, EUROPE, KOREA
	};

	private enum Race {
		TERRAN, PROTOSS, ZERG, RANDOM
	};

	private enum GroupResult {
		UP, STAYDOWN, NOTYET
	};

	private class GroupPlayerEntry {
		private String name;
		private String country;
		private Race race;
		private int place;
		private int matchesWon;
		private int matchesLost;
		private int mapsWon;
		private int mapsLost;
		private GroupResult result;

		public GroupPlayerEntry(String name, String country, String race,
				String place, int matchesWon, int matchesLost, int mapsWon,
				int mapsLost, String result) {
			this.name = name;
			this.country = country;
			this.race = getRaceFromString(race);
			this.place = place.length() > 0 ? Integer.parseInt(place) : -1;
			this.matchesWon = matchesWon;
			this.matchesLost = matchesLost;
			this.mapsWon = mapsWon;
			this.mapsLost = mapsLost;
			this.result = getResultFromString(result);
		}

		public int getBackgroundColor() {
			switch (result) {
			case UP:
				return 0xFFCCFFCC;
			case STAYDOWN:
				return 0xFFFFDDAA;
			default:
				return 0xFFFFFFFF;
			}
		}

		public int getFlagDrawable() {
			return R.drawable.flags_kr;
		}

		public int getRaceDrawable() {
			switch (race) {
			case TERRAN:
				return R.drawable.raceicons_terran;
			case ZERG:
				return R.drawable.raceicons_zerg;
			case PROTOSS:
				return R.drawable.raceicons_protoss;
			default:
				return R.drawable.raceicons_random;
			}
		}

		private GroupResult getResultFromString(String result) {
			if (result.equalsIgnoreCase("up"))
				return GroupResult.UP;
			else if (result.equalsIgnoreCase("staydown"))
				return GroupResult.STAYDOWN;
			else
				return GroupResult.NOTYET;
		}

		public String getName() {
			return name;
		}

		public String getRaceStr() {
			if (race == null)
				return "X";
			switch (race) {
			case ZERG:
				return "Z";
			case TERRAN:
				return "T";
			case PROTOSS:
				return "P";
			case RANDOM:
				return "R";
			default:
				return "X";
			}
		}

		private Race getRaceFromString(String race) {
			if (race.equalsIgnoreCase("z"))
				return Race.ZERG;
			else if (race.equalsIgnoreCase("t"))
				return Race.TERRAN;
			else if (race.equalsIgnoreCase("p"))
				return Race.PROTOSS;
			else if (race.equalsIgnoreCase("r"))
				return Race.RANDOM;
			else
				return null;
		}

		public String getCountry() {
			return country;
		}

		public Race getRace() {
			return race;
		}

		public int getPlace() {
			return place;
		}

		public int getMatchesWon() {
			return matchesWon;
		}

		public int getMatchesLost() {
			return matchesLost;
		}

		public int getMapsWon() {
			return mapsWon;
		}

		public int getMapsLost() {
			return mapsLost;
		}

		public GroupResult getResult() {
			return result;
		}
	}

	private class ScheduleEntry {
		private int id;
		private long time;
		private Division division;
		private Region region;
		private String name;
		private ArrayList<GroupPlayerEntry> players;

		public ScheduleEntry(String id, String time, String division,
				String region, String name) {
			this.id = Integer.parseInt(id);
			this.time = Long.parseLong(time);
			this.division = setDivisionFromString(division);
			this.region = setRegionFromString(region);
			this.name = name;
			players = new ArrayList<GroupPlayerEntry>();
		}

		public int getColor() {
			switch(region){
			case AMERICA:
				return 0xFFFFC5DA;
			case EUROPE:
				return 0xFFF7FFAC;
			case KOREA:
				return 0xFFD1FDAB;
			default:
				return 0xFFFFFFFF;
			}
		}

		public GroupPlayerEntry getPlayer(int n) {
			return players.get(n);
		}

		public int getNumPlayers() {
			return players.size();
		}

		public int getId() {
			return id;
		}

		public void addPlayer(GroupPlayerEntry player) {
			players.add(player);
		}

		public String getTime() {
			return DateFormat.getDateTimeInstance().format(new Date(time));
		}

		public int getTitleDrawable() {
			switch (region) {
			case AMERICA:
				if (division == Division.PREMIER)
					return R.drawable.wcs_logo_am_premier;
				else if (division == Division.CHALLENGER)
					return R.drawable.wcs_logo_am_challenger;
				else
					return R.drawable.wcs_logo_am_plain;
			case EUROPE:
				if (division == Division.PREMIER)
					return R.drawable.wcs_logo_eu_premier;
				else if (division == Division.CHALLENGER)
					return R.drawable.wcs_logo_eu_challenger;
				else
					return R.drawable.wcs_logo_eu_plain;
			case KOREA:
				if (division == Division.PREMIER)
					return R.drawable.wcs_logo_kr_premier;
				else if (division == Division.CHALLENGER)
					return R.drawable.wcs_logo_kr_challenger;
				else
					return R.drawable.wcs_logo_kr_plain;
			default: // TODO: Add leagues to generic logo?
				return R.drawable.wcs_logo_nowhere_plain;
			}
		}

		public String getName() {
			return name;
		}

		private Division setDivisionFromString(String division) {
			if (division.equalsIgnoreCase("P"))
				return Division.PREMIER;
			else if (division.equalsIgnoreCase("C"))
				return Division.CHALLENGER;
			else
				return null;
		}

		private Region setRegionFromString(String region) {
			if (region.equalsIgnoreCase("AM"))
				return Region.AMERICA;
			else if (region.equalsIgnoreCase("KR"))
				return Region.KOREA;
			else if (region.equalsIgnoreCase("EU"))
				return Region.EUROPE;
			else
				return null;
		}
	}
}
