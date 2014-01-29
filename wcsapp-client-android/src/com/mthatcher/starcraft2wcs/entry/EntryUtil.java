package com.mthatcher.starcraft2wcs.entry;

import com.mthatcher.starcraft2wcs.LandingPage.Country;
import com.mthatcher.starcraft2wcs.LandingPage.Race;
import com.mthatcher.starcraft2wcs.R;

public class EntryUtil {
	public static Race getRaceFromString(String race) {
		if(race == null)
			return Race.RANDOM;
		else if (race.equalsIgnoreCase("z"))
			return Race.ZERG;
		else if (race.equalsIgnoreCase("t"))
			return Race.TERRAN;
		else if (race.equalsIgnoreCase("p"))
			return Race.PROTOSS;
		else
			return Race.RANDOM;
	}
	
	public static int getFlagDrawable(Country country) {
		switch(country){
		case AR:
			return R.drawable.flags_ar;
		case AT:
			return R.drawable.flags_at;
		case AU:
			return R.drawable.flags_au;
		case BE:
			return R.drawable.flags_be;
		case BG:
			return R.drawable.flags_bg;
		case CA:
			return R.drawable.flags_ca;
		case CL:
			return R.drawable.flags_cl;
		case CN:
			return R.drawable.flags_cn;
		case DE:
			return R.drawable.flags_de;
		case DK:
			return R.drawable.flags_dk;
		case ES:
			return R.drawable.flags_es;
		case FI:
			return R.drawable.flags_fi;
		case FR:
			return R.drawable.flags_fr;
		case GB:
			return R.drawable.flags_gb;
		case IE:
			return R.drawable.flags_ie;
		case IL:
			return R.drawable.flags_il;
		case KR:
			return R.drawable.flags_kr;
		case MX:
			return R.drawable.flags_mx;
		case NL:
			return R.drawable.flags_nl;
		case NO:
			return R.drawable.flags_no;
		case NZ:
			return R.drawable.flags_nz;
		case PE:
			return R.drawable.flags_pe;
		case PH:
			return R.drawable.flags_ph;
		case PL:
			return R.drawable.flags_pl;
		case RO:
			return R.drawable.flags_ro;
		case RS:
			return R.drawable.flags_rs;
		case RU:
			return R.drawable.flags_ru;
		case SE:
			return R.drawable.flags_se;
		case TW:
			return R.drawable.flags_tw;
		case SI:
			return R.drawable.flags_si;
		case UA:
			return R.drawable.flags_ua;
		case US:
			return R.drawable.flags_us;
		default:
			return R.drawable.flags_fam; // A silly flag for those with no identifiable country.
		}
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

	public static Country getCountryFromString(String country) {
		if (country == null){
			return Country.UNKNOWN;
		} else if(country.equalsIgnoreCase("ar") || country.equalsIgnoreCase("argentina")){
			return Country.AR;
		} else if (country.equalsIgnoreCase("at") || country.equalsIgnoreCase("austria")){
			return Country.AT;
		} else if (country.equalsIgnoreCase("au") || country.equalsIgnoreCase("australia")){
			return Country.AU;
		} else if (country.equalsIgnoreCase("be") || country.equalsIgnoreCase("belgium")){
			return Country.BE;
		} else if (country.equalsIgnoreCase("bg") || country.equalsIgnoreCase("bulgaria")){
			return Country.BG;
		} else if (country.equalsIgnoreCase("ca") || country.equalsIgnoreCase("canada")){
			return Country.CA;
		} else if (country.equalsIgnoreCase("cn") || country.equalsIgnoreCase("china")){
			return Country.CN;
		} else if (country.equalsIgnoreCase("cl") || country.equalsIgnoreCase("chile")){
			return Country.CL;
		} else if (country.equalsIgnoreCase("de") || country.equalsIgnoreCase("germany")){
			return Country.DE;
		} else if (country.equalsIgnoreCase("dk") || country.equalsIgnoreCase("denmark")){
			return Country.DK;
		} else if (country.equalsIgnoreCase("es") || country.equalsIgnoreCase("spain")){
			return Country.ES;
		} else if (country.equalsIgnoreCase("fi") || country.equalsIgnoreCase("finland")){
			return Country.FI;
		} else if (country.equalsIgnoreCase("fr") || country.equalsIgnoreCase("france")){
			return Country.FR;
		} else if (country.equalsIgnoreCase("gb") || country.equalsIgnoreCase("uk") || country.equalsIgnoreCase("great britain")){
			return Country.GB;
		} else if (country.equalsIgnoreCase("ie") || country.equalsIgnoreCase("ireland")){
			return Country.IE;
		} else if (country.equalsIgnoreCase("il") || country.equalsIgnoreCase("israel")){
			return Country.IL;
		} else if (country.equalsIgnoreCase("kr") || country.equalsIgnoreCase("korea")){
			return Country.KR;
		} else if (country.equalsIgnoreCase("mx") || country.equalsIgnoreCase("mexico")){
			return Country.MX;
		} else if (country.equalsIgnoreCase("nl") || country.equalsIgnoreCase("netherlands")){
			return Country.NL;
		} else if (country.equalsIgnoreCase("no") || country.equalsIgnoreCase("norway")){
			return Country.NO;
		} else if (country.equalsIgnoreCase("nz") || country.equalsIgnoreCase("new zealand")){
			return Country.NZ;
		} else if (country.equalsIgnoreCase("pe") || country.equalsIgnoreCase("peru")){
			return Country.PE;
		} else if (country.equalsIgnoreCase("ph") || country.equalsIgnoreCase("philippines")){
			return Country.PH;
		} else if (country.equalsIgnoreCase("pl") || country.equalsIgnoreCase("poland")){
			return Country.PL;
		} else if (country.equalsIgnoreCase("ro") || country.equalsIgnoreCase("romania")){
			return Country.RO;
		} else if (country.equalsIgnoreCase("rs") || country.equalsIgnoreCase("serbia")){
			return Country.RS;
		} else if (country.equalsIgnoreCase("ru") || country.equalsIgnoreCase("russia")){
			return Country.RU;
		} else if (country.equalsIgnoreCase("se") || country.equalsIgnoreCase("sweden")){
			return Country.SE;
		} else if (country.equalsIgnoreCase("si") || country.equalsIgnoreCase("slovenia")){
			return Country.SI;
		} else if (country.equalsIgnoreCase("tw") || country.equalsIgnoreCase("taiwan")){
			return Country.TW;
		} else if (country.equalsIgnoreCase("ua") || country.equalsIgnoreCase("ukraine")){
			return Country.UA;
		} else if (country.equalsIgnoreCase("us") || country.equalsIgnoreCase("usa")){
			return Country.US;
		} else {
			return Country.UNKNOWN;
		}
	}

	public static int getWinsFromString(String wins) {
		if(wins == null)
			return 0;
		else if(wins.length() == 0)
			return 0;
		else if(wins.equalsIgnoreCase("w"))
			return 1;
		else if(wins.equalsIgnoreCase("-"))
			return 0;
		else if(wins.equalsIgnoreCase("q"))
			return 1;
		else
			try {
				return Integer.parseInt(wins);
			} catch(NumberFormatException e) {
				return 0;
			}
	}
	
	public static String getWinsStr(int playerNum, BracketEntry player){
		if(player.isWalkover())
			if(playerNum == 1)
				if(player.getP1wins() == 1)
					return "W";
				else
					return "-";
			else
				if(player.getP2wins() == 1)
					return "W";
				else
					return "-";
		else
			if(playerNum == 1)
				return Integer.toString(player.getP1wins());
			else
				return Integer.toString(player.getP2wins());
	}

	public static int getWinnerColor() {
		return 0xFFCCFFCC;
	}
	
	public static int getLoserColor() {
		return 0x00FFFFFF;
	}
	
	public static int getDownColor() {
		return 0xFFFFDDAA;
	}

	public static int getStayColor() {
		return 0xFFEEEE88;
	}
}
