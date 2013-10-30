package com.mthatcher.starcraft2wcs.entry;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

public class DetailEntry {
	private ArrayList<MapDetail> maps;
	private String player1Name;
	private String player2Name;
	private Drawable player1Race;
	private Drawable player2Race;
	private Drawable player1Flag;
	private Drawable player2Flag;

	public DetailEntry(String p1Name, String p2Name, Drawable p1Race,
			Drawable p2Race, Drawable p1Flag, Drawable p2Flag,
			ArrayList<MapDetail> mapDetails) {
		this.player1Name = p1Name;
		this.player2Name = p2Name;
		this.player1Race = p1Race;
		this.player2Race = p2Race;
		this.player1Flag = p1Flag;
		this.player2Flag = p2Flag;
		maps = mapDetails;
	}

	public String getPlayer1Name() {
		return player1Name;
	}

	public String getPlayer2Name() {
		return player2Name;
	}

	public Drawable getPlayer1Race() {
		return player1Race;
	}

	public Drawable getPlayer2Race() {
		return player2Race;
	}

	public Drawable getPlayer1Flag() {
		return player1Flag;
	}

	public Drawable getPlayer2Flag() {
		return player2Flag;
	}

	public ArrayList<MapDetail> getMaps() {
		return maps;
	}

	public void addMapDetail(MapDetail map) {
		maps.add(map);
	}

	public class MapDetail {
		private String mapName;
		private boolean p1Wins;
		private boolean p2Wins;

		public MapDetail(String mapName, String mapWinner) {
			this.mapName = mapName;
			if (mapWinner != null && mapWinner.equals("1")) {
				p1Wins = true;
				p2Wins = false;
			} else if (mapWinner != null && mapWinner.equals("2")) {
				p1Wins = false;
				p2Wins = true;
			} else {
				p1Wins = false;
				p2Wins = false;
			}
		}

		public String getMapName() {
			return mapName;
		}

		public boolean isP1Wins() {
			return p1Wins;
		}

		public boolean isP2Wins() {
			return p2Wins;
		}
	}
}
