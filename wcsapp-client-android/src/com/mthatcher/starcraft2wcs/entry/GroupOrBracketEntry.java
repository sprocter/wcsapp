package com.mthatcher.starcraft2wcs.entry;

import com.mthatcher.starcraft2wcs.LandingPage.Race;

//TODO: This is a terrible name.
public interface GroupOrBracketEntry {
	int DEFAULT_NUM_ENTRANTS = 4;
	public int getBackgroundColor();
	public Race getRace();
}
