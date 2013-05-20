package com.mthatcher.starcraft2wcs;

import com.mthatcher.starcraft2wcs.LandingPage.MatchResult;
import com.mthatcher.starcraft2wcs.LandingPage.Race;

public class BracketEntry implements GroupOrBracketEntry{
	private String p1Name;
	private String p2Name;
	private String p1Country;
	private String p2Country;
	private Race p1Race;
	private Race p2Race;
	private MatchResult p1Result;

	public BracketEntry(String p1Name, String p2Name, String p1Country, String p2Country, String p1Race, String p2Race, String winner) {
		this.p1Name = p1Name;
		this.p2Name = p2Name;
		this.p1Country = p1Country;
		this.p2Country = p2Country;
		this.p1Race = EntryUtil.getRaceFromString(p1Race);
		this.p2Race = EntryUtil.getRaceFromString(p2Race);
		this.p1Result = doesP1Win(winner);
	}

	private MatchResult doesP1Win(String winner) {
		if(winner.equals("1"))
			return MatchResult.WIN;
		else if(winner.equals("2"))
			return MatchResult.LOSE;
		else
			return MatchResult.NOTYETPLAYED;
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

	public String getP1Country() {
		return p1Country;
	}

	public String getP2Country() {
		return p2Country;
	}

	public Race getP1Race() {
		return p1Race;
	}

	public Race getP2Race() {
		return p2Race;
	}

	public MatchResult getP1Result() {
		return p1Result;
	}

}
