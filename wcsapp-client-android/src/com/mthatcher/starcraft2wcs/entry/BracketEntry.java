package com.mthatcher.starcraft2wcs.entry;

import android.view.View;
import android.widget.TextView;

import com.mthatcher.starcraft2wcs.LandingPage.Country;
import com.mthatcher.starcraft2wcs.LandingPage.Race;
import com.mthatcher.starcraft2wcs.R;

public class BracketEntry implements GroupOrBracketEntry{
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

	public BracketEntry(String p1Name, String p2Name, String p1Race, String p2Race, String p1Country, String p2Country, String winner, String p1wins, String p2wins) {
		this.p1Name = p1Name;
		this.p2Name = p2Name;
		this.p1Country = EntryUtil.getCountryFromString(p1Country);
		this.p2Country = EntryUtil.getCountryFromString(p2Country);
		this.p1Race = EntryUtil.getRaceFromString(p1Race);
		this.p2Race = EntryUtil.getRaceFromString(p2Race);
		this.winner = winner.equals("") ? 0 : Integer.parseInt(winner);
		this.p1wins = EntryUtil.getWinsFromString(p1wins);
		this.p2wins = EntryUtil.getWinsFromString(p2wins);
		if(p1wins.equalsIgnoreCase("w") || p2wins.equalsIgnoreCase("w"))
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

	public int getWinner(){
		return winner;
	}
	
	@Override
	public int getBackgroundColor() {
		// TODO Auto-generated method stub
		return 0xFFFFFFFF;
	}

	@Override
	public Race getRace() {
		return Race.TERRAN;
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

	public static ViewHolder getHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		
		holder.isGroupHolder = false;
		
		holder.groupName = (TextView) convertView.findViewById(R.id.schedule_name);
		holder.date = (TextView) convertView.findViewById(R.id.schedule_date);
		
		holder.playerName[0] = (TextView) convertView.findViewById(R.id.bracket_player_1_name);
		holder.playerName[1] = (TextView) convertView.findViewById(R.id.bracket_player_2_name);
		holder.playerName[2] = (TextView) convertView.findViewById(R.id.bracket_player_3_name);
		holder.playerName[3] = (TextView) convertView.findViewById(R.id.bracket_player_4_name);
		holder.playerName[4] = (TextView) convertView.findViewById(R.id.bracket_player_5_name);
		holder.playerName[5] = (TextView) convertView.findViewById(R.id.bracket_player_6_name);
		holder.playerName[6] = (TextView) convertView.findViewById(R.id.bracket_player_7_name);
		holder.playerName[7] = (TextView) convertView.findViewById(R.id.bracket_player_8_name);
		
		holder.flag[0] = (TextView) convertView.findViewById(R.id.bracket_player_1_flag);
		holder.flag[1] = (TextView) convertView.findViewById(R.id.bracket_player_2_flag);
		holder.flag[2] = (TextView) convertView.findViewById(R.id.bracket_player_3_flag);
		holder.flag[3] = (TextView) convertView.findViewById(R.id.bracket_player_4_flag);
		holder.flag[4] = (TextView) convertView.findViewById(R.id.bracket_player_5_flag);
		holder.flag[5] = (TextView) convertView.findViewById(R.id.bracket_player_6_flag);
		holder.flag[6] = (TextView) convertView.findViewById(R.id.bracket_player_7_flag);
		holder.flag[7] = (TextView) convertView.findViewById(R.id.bracket_player_8_flag);
		
		holder.race[0] = (TextView) convertView.findViewById(R.id.bracket_player_1_race);
		holder.race[1] = (TextView) convertView.findViewById(R.id.bracket_player_2_race);
		holder.race[2] = (TextView) convertView.findViewById(R.id.bracket_player_3_race);
		holder.race[3] = (TextView) convertView.findViewById(R.id.bracket_player_4_race);
		holder.race[4] = (TextView) convertView.findViewById(R.id.bracket_player_5_race);
		holder.race[5] = (TextView) convertView.findViewById(R.id.bracket_player_6_race);
		holder.race[6] = (TextView) convertView.findViewById(R.id.bracket_player_7_race);
		holder.race[7] = (TextView) convertView.findViewById(R.id.bracket_player_8_race);
		
		holder.mapScore[0] = (TextView) convertView.findViewById(R.id.bracket_player_1_map_score);
		holder.mapScore[1] = (TextView) convertView.findViewById(R.id.bracket_player_2_map_score);
		holder.mapScore[2] = (TextView) convertView.findViewById(R.id.bracket_player_3_map_score);
		holder.mapScore[3] = (TextView) convertView.findViewById(R.id.bracket_player_4_map_score);
		holder.mapScore[4] = (TextView) convertView.findViewById(R.id.bracket_player_5_map_score);
		holder.mapScore[5] = (TextView) convertView.findViewById(R.id.bracket_player_6_map_score);
		holder.mapScore[6] = (TextView) convertView.findViewById(R.id.bracket_player_7_map_score);
		holder.mapScore[7] = (TextView) convertView.findViewById(R.id.bracket_player_8_map_score);
		
		return holder;
	}
}
