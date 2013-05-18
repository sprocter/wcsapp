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
import android.widget.TableLayout;
import android.widget.TableRow;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landing_page, menu);
		return true;
	}

	private class ScheduleAdapter extends BaseAdapter {

		ArrayList<ScheduleEntry> items;

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
			View v = convertView;
			if (v == null) {
				LayoutInflater vi;
				vi = LayoutInflater.from(getBaseContext());
				v = vi.inflate(R.layout.group_stage_tbl, null);
			}

			ScheduleEntry item = items.get(position);

			if (item != null) {
				TextView scheduleNameView = (TextView) v
						.findViewById(R.id.schedule_name);
				TextView scheduleDateView = (TextView) v
						.findViewById(R.id.schedule_date);

				scheduleNameView.setText(item.getName());
				scheduleDateView.setText(item.getTime());
				if (item.getNumPlayers() > 0) {
					TextView player1Rank = (TextView) v
							.findViewById(R.id.player_1_rank);
					TextView player1Flag = (TextView) v
							.findViewById(R.id.player_1_flag);
					TextView player1Race = (TextView) v
							.findViewById(R.id.player_1_race);
					TextView player1Name = (TextView) v
							.findViewById(R.id.player_1_name);
					TextView player1MatchScore = (TextView) v
							.findViewById(R.id.player_1_match_score);
					TextView player1MapScore = (TextView) v
							.findViewById(R.id.player_1_map_score);
					player1Rank.setText(Integer.toString(item.getPlayer(0).getPlace()));
					player1Flag.setText(item.getPlayer(0).getCountry());
					player1Race.setText(item.getPlayer(0).getRaceStr());
					player1Name.setText(item.getPlayer(0).getName());
					player1MatchScore.setText(Integer.toString(item.getPlayer(0).getMatchesWon())
							+ "-" + Integer.toString(item.getPlayer(0).getMatchesLost()));
					player1MapScore.setText(Integer.toString(item.getPlayer(0).getMapsWon())
							+ "-" + Integer.toString(item.getPlayer(0).getMapsLost()));
				} 
				if (item.getNumPlayers() > 1) {
					TextView player2Rank = (TextView) v
							.findViewById(R.id.player_2_rank);
					TextView player2Flag = (TextView) v
							.findViewById(R.id.player_2_flag);
					TextView player2Race = (TextView) v
							.findViewById(R.id.player_2_race);
					TextView player2Name = (TextView) v
							.findViewById(R.id.player_2_name);
					TextView player2MatchScore = (TextView) v
							.findViewById(R.id.player_2_match_score);
					TextView player2MapScore = (TextView) v
							.findViewById(R.id.player_2_map_score);
					player2Rank.setText(Integer.toString(item.getPlayer(1).getPlace()));
					player2Flag.setText(item.getPlayer(1).getCountry());
					player2Race.setText(item.getPlayer(1).getRaceStr());
					player2Name.setText(item.getPlayer(1).getName());
					player2MatchScore.setText(Integer.toString(item.getPlayer(1).getMatchesWon())
							+ "-" + Integer.toString(item.getPlayer(1).getMatchesLost()));
					player2MapScore.setText(Integer.toString(item.getPlayer(1).getMapsWon())
							+ "-" + Integer.toString(item.getPlayer(1).getMapsLost()));
				}
				if (item.getNumPlayers() > 2) {
					TextView player3Rank = (TextView) v
							.findViewById(R.id.player_3_rank);
					TextView player3Flag = (TextView) v
							.findViewById(R.id.player_3_flag);
					TextView player3Race = (TextView) v
							.findViewById(R.id.player_3_race);
					TextView player3Name = (TextView) v
							.findViewById(R.id.player_3_name);
					TextView player3MatchScore = (TextView) v
							.findViewById(R.id.player_3_match_score);
					TextView player3MapScore = (TextView) v
							.findViewById(R.id.player_3_map_score);
					player3Rank.setText(Integer.toString(item.getPlayer(2).getPlace()));
					player3Flag.setText(item.getPlayer(2).getCountry());
					player3Race.setText(item.getPlayer(2).getRaceStr());
					player3Name.setText(item.getPlayer(2).getName());
					player3MatchScore.setText(Integer.toString(item.getPlayer(2).getMatchesWon())
							+ "-" + Integer.toString(item.getPlayer(2).getMatchesLost()));
					player3MapScore.setText(Integer.toString(item.getPlayer(2).getMapsWon())
							+ "-" + Integer.toString(item.getPlayer(2).getMapsLost()));
				}
					if (item.getNumPlayers() > 3) {
					TextView player4Rank = (TextView) v
							.findViewById(R.id.player_4_rank);
					TextView player4Flag = (TextView) v
							.findViewById(R.id.player_4_flag);
					TextView player4Race = (TextView) v
							.findViewById(R.id.player_4_race);
					TextView player4Name = (TextView) v
							.findViewById(R.id.player_4_name);
					TextView player4MatchScore = (TextView) v
							.findViewById(R.id.player_4_match_score);
					TextView player4MapScore = (TextView) v
							.findViewById(R.id.player_4_map_score);
					player4Rank.setText(Integer.toString(item.getPlayer(3).getPlace()));
					player4Flag.setText(item.getPlayer(3).getCountry());
					player4Race.setText(item.getPlayer(3).getRaceStr());
					player4Name.setText(item.getPlayer(3).getName());
					player4MatchScore.setText(Integer.toString(item.getPlayer(3).getMatchesWon())
							+ "-" + Integer.toString(item.getPlayer(3).getMatchesLost()));
					player4MapScore.setText(Integer.toString(item.getPlayer(3).getMapsWon())
							+ "-" + Integer.toString(item.getPlayer(3).getMapsLost()));
//					tbl.addView(r);
				}
			}
			return v;
		}

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
			while (!c.isLast()) {
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
						.getInt(5), c.getInt(6), c.getInt(7), c.getInt(8),
						c.getString(9)));
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
		UP, STAYDOWN
	};

	private class GroupPlayerEntry {
		private String name;
		private String country;

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

		private GroupResult getResultFromString(String result) {
			if (result.equalsIgnoreCase("up"))
				return GroupResult.UP;
			else if (result.equalsIgnoreCase("staydown"))
				return GroupResult.STAYDOWN;
			else
				return null;
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
			return new Date(time).toString();
		}

		public int getIcon() {
			switch (region) {
			case AMERICA:
				if (division == Division.PREMIER)
					return R.drawable.wcsam_logo_small_premier;
				else if (division == Division.CHALLENGER)
					return R.drawable.wcsam_logo_small_challenger;
				else
					return R.drawable.wcsam_logo_small;
			case EUROPE:
				if (division == Division.PREMIER)
					return R.drawable.wcseu_logo_small_premier;
				else if (division == Division.CHALLENGER)
					return R.drawable.wcseu_logo_small_challenger;
				else
					return R.drawable.wcseu_logo_small;
			case KOREA:
				if (division == Division.PREMIER)
					return R.drawable.wcskr_logo_small_premier;
				else if (division == Division.CHALLENGER)
					return R.drawable.wcskr_logo_small_challenger;
				else
					return R.drawable.wcskr_logo_small;
			default: // TODO: Add leagues to generic logo?
				return R.drawable.wcs_logo_small;
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
