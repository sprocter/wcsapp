package com.mthatcher.starcraft2wcs.entry;

import com.mthatcher.starcraft2wcs.LandingPage.Race;
import com.mthatcher.starcraft2wcs.R;

public class EntryUtil {
	public static Race getRaceFromString(String race) {
		if (race.equalsIgnoreCase("z"))
			return Race.ZERG;
		else if (race.equalsIgnoreCase("t"))
			return Race.TERRAN;
		else if (race.equalsIgnoreCase("p"))
			return Race.PROTOSS;
		else
			return Race.RANDOM;
	}
	
	public static int getFlagDrawable(GroupOrBracketEntry player) {
		return R.drawable.flags_kr;
	}

	public static int getRaceDrawable(Race race) {
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
}
