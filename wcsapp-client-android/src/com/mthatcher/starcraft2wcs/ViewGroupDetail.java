package com.mthatcher.starcraft2wcs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mthatcher.starcraft2wcs.entry.ViewHolderData;

public class ViewGroupDetail extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_group_detail);
		setupActionBar();
		Intent intent = getIntent();
		initKnownValues((ViewHolderData) intent
				.getParcelableExtra(LandingPage.GROUP_DATA_EXTRA));
		initNewValues(intent.getExtras().getInt(LandingPage.ENTRY_ID_EXTRA));
	}

	private void initNewValues(int entryid) {
		doDBQuery(entryid);
	}

	private void doDBQuery(int entryid) {
		WcsDBHelper dbHelper = new WcsDBHelper(getBaseContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// TODO: Use a raw query along the lines of...
		/*
		 * SELECT maps.matchid, maps.mapname, maps.mapwinner FROM maps, matches
		 * WHERE maps.matchid = matches.id AND matches.scheduleid = 1;
		 */
	}

	private void initKnownValues(ViewHolderData data) {
		/*
		 * In the normal case we'll have 4 players, we want to avoid as much
		 * work as possible for that normal case so we use != instead of < or >
		 * here.
		 */
		if (data.getPlayerName().length != 4) {
			adjustTableSize(data);
		}

		TextView groupNameTV = (TextView) findViewById(R.id.schedule_name);
		TextView dateTV = (TextView) findViewById(R.id.schedule_date);

		groupNameTV.setText(data.getGroupName());
		dateTV.setText(data.getDate());

		initPlayerRows(data);
	}

	private void initPlayerRows(ViewHolderData data) {
		ViewGroup currentContent = (ViewGroup) findViewById(android.R.id.content);
		TableLayout tl = (TableLayout) currentContent.getChildAt(0);
		TableRow currentRow;
		TextView rankTV, flagTV, raceTV, nameTV, matchTV, gameTV;
		Drawable flagD, raceD;
		for (int i = 0; i < tl.getChildCount() - 2; i++) {
			flagD = new BitmapDrawable(getResources(), data.getFlag()[i]);
			raceD = new BitmapDrawable(getResources(), data.getRace()[i]);

			currentRow = (TableRow) tl.getChildAt(i + 2);
			rankTV = (TextView) currentRow.findViewById(R.id.group_player_rank);
			flagTV = (TextView) currentRow.findViewById(R.id.group_player_flag);
			raceTV = (TextView) currentRow.findViewById(R.id.group_player_race);
			nameTV = (TextView) currentRow.findViewById(R.id.group_player_name);
			matchTV = (TextView) currentRow
					.findViewById(R.id.group_player_match_score);
			gameTV = (TextView) currentRow
					.findViewById(R.id.group_player_map_score);

			rankTV.setText(data.getRank()[i]);
			flagTV.setCompoundDrawablesWithIntrinsicBounds(flagD, null, null,
					null);
			raceTV.setCompoundDrawablesWithIntrinsicBounds(raceD, null, null,
					null);
			nameTV.setText(data.getPlayerName()[i]);
			matchTV.setText(data.getMatchScore()[i]);
			gameTV.setText(data.getMapScore()[i]);
		}
	}

	private void adjustTableSize(ViewHolderData data) {
		// Get the current view
		ViewGroup currentContent = (ViewGroup) findViewById(android.R.id.content);

		// Get the table held by the current view
		TableLayout tl = (TableLayout) currentContent.getChildAt(0);

		LayoutInflater vi = LayoutInflater.from(getBaseContext());

		// Add more rows if we need them. We add 2 to the number of
		// players (name / date rows) to get the number of rows we need
		while (tl.getChildCount() < data.getPlayerName().length + 2) {
			View newRow = vi.inflate(R.layout.group_stage_row, null);
			tl.addView(newRow);
		}

		// Remove rows if we had too many...
		while (tl.getChildCount() > data.getPlayerName().length + 2) {
			tl.removeViewAt(tl.getChildCount() - 1);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_group_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
