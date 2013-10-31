package com.mthatcher.starcraft2wcs.entry;

import android.view.View;
import android.widget.TextView;

import com.mthatcher.starcraft2wcs.LandingPage.Country;
import com.mthatcher.starcraft2wcs.LandingPage.Race;
import com.mthatcher.starcraft2wcs.R;

public class BracketEntry implements Entry {
	private String p1Name;
	private String p2Name;
	private Country p1Country;
	private Country p2Country;
	private Race p1Race;
	private Race p2Race;
	private int winner;
	private int p1wins;
	private int p2wins;
	private boolean isWalkover;
	private static int[] rowIds = { R.id.bracket_row_1, R.id.bracket_row_2,
			R.id.bracket_row_3, R.id.bracket_row_4, R.id.bracket_row_5,
			R.id.bracket_row_6, R.id.bracket_row_7, R.id.bracket_row_8,
			R.id.bracket_row_9, R.id.bracket_row_10, R.id.bracket_row_11,
			R.id.bracket_row_12, R.id.bracket_row_13, R.id.bracket_row_14,
			R.id.bracket_row_15, R.id.bracket_row_16, R.id.bracket_row_17,
			R.id.bracket_row_18, R.id.bracket_row_19, R.id.bracket_row_20,
			R.id.bracket_row_21, R.id.bracket_row_22, R.id.bracket_row_23,
			R.id.bracket_row_24, R.id.bracket_row_25, R.id.bracket_row_26,
			R.id.bracket_row_27, R.id.bracket_row_28, R.id.bracket_row_29,
			R.id.bracket_row_30, R.id.bracket_row_31, R.id.bracket_row_32 };

	public BracketEntry(String p1Name, String p2Name, String p1Race,
			String p2Race, String p1Country, String p2Country, String winner,
			String p1wins, String p2wins) {
		this.p1Name = p1Name != null && p1Name.length() == 0 ? "TBD" : p1Name;
		this.p2Name = p2Name != null && p2Name.length() == 0 ? "TBD" : p2Name;
		this.p1Country = EntryUtil.getCountryFromString(p1Country);
		this.p2Country = EntryUtil.getCountryFromString(p2Country);
		this.p1Race = EntryUtil.getRaceFromString(p1Race);
		this.p2Race = EntryUtil.getRaceFromString(p2Race);
		this.winner = winner == null ? 0 : winner.length() == 0 ? 0 : Integer
				.parseInt(winner);
		this.p1wins = EntryUtil.getWinsFromString(p1wins);
		this.p2wins = EntryUtil.getWinsFromString(p2wins);
		if ((p1wins != null && p1wins.equalsIgnoreCase("w")) || (p2wins != null && p2wins.equalsIgnoreCase("w")))
			isWalkover = true;
		else
			isWalkover = false;
	}

	public int getP1wins() {
		return p1wins;
	}

	public int getP2wins() {
		return p2wins;
	}

	public boolean isWalkover() {
		return isWalkover;
	}

	public int getWinner() {
		return winner;
	}

	public String getP1Name() {
		return p1Name;
	}

	public String getP2Name() {
		return p2Name;
	}

	public Country getP1Country() {
		return p1Country;
	}

	public Country getP2Country() {
		return p2Country;
	}

	public Race getP1Race() {
		return p1Race;
	}

	public Race getP2Race() {
		return p2Race;
	}
	
	public int getP1BackgroundColor() {
		if(winner == 1)
			return EntryUtil.getWinnerColor();
		else
			return EntryUtil.getLoserColor();
	}
	
	public int getP2BackgroundColor() {
		if(winner == 2)
			return EntryUtil.getWinnerColor();
		else
			return EntryUtil.getLoserColor();
	}

	public static ViewHolder getHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.isGroupHolder = false;
		holder.groupName = (TextView) convertView.findViewById(R.id.schedule_name);
		holder.date = (TextView) convertView.findViewById(R.id.schedule_date);
		holder.size = 0;
		int p1i, p2i;
		for (int i = 0; i < DEFAULT_NUM_ENTRANTS; i++) {
			p1i = i * 2;
			p2i = i * 2 + 1;
			addRow(convertView.findViewById(rowIds[i]), p1i, p2i, holder);
		}
		return holder;
	}

	public static void addRow(View currentRow, int p1i, int p2i, ViewHolder holder) {
		holder.playerName.add(p1i, (TextView) currentRow.findViewById(R.id.bracket_player_1_name));
		holder.playerName.add(p2i, (TextView) currentRow
				.findViewById(R.id.bracket_player_2_name));

		holder.flag.add(p1i, (TextView) currentRow
				.findViewById(R.id.bracket_player_1_flag));
		holder.flag.add(p2i, (TextView) currentRow
				.findViewById(R.id.bracket_player_2_flag));

		holder.race.add(p1i, (TextView) currentRow
				.findViewById(R.id.bracket_player_1_race));
		holder.race.add(p2i, (TextView) currentRow
				.findViewById(R.id.bracket_player_2_race));

		holder.mapScore.add(p1i, (TextView) currentRow
				.findViewById(R.id.bracket_player_1_map_score));
		holder.mapScore.add(p2i, (TextView) currentRow
				.findViewById(R.id.bracket_player_2_map_score));
		
		holder.size++;
	}

	public static void removeRow(ViewHolder holder) {
		int p2i = --holder.size * 2;
		int p1i = holder.size * 2 + 1;
		holder.playerName.remove(p1i);
		holder.playerName.remove(p2i);
		holder.flag.remove(p1i);
		holder.flag.remove(p2i);
		holder.race.remove(p1i);
		holder.race.remove(p2i);
		holder.mapScore.remove(p1i);
		holder.mapScore.remove(p2i);
	}
}
