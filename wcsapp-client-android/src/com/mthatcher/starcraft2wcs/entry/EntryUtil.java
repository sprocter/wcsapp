package com.mthatcher.starcraft2wcs.entry;

import com.mthatcher.starcraft2wcs.LandingPage.Country;
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
	
	public static int getFlagDrawable(Country country) {
		switch(country){
		case AR:
			break;
		case AT:
			break;
		case AU:
			break;
		case BE:
			break;
		case CA:
			break;
		case CL:
			break;
		case CN:
			break;
		case DE:
			break;
		case DK:
			break;
		case ES:
			break;
		case FI:
			break;
		case FR:
			break;
		case GB:
			break;
		case KR:
			break;
		case MX:
			break;
		case NL:
			break;
		case NO:
			break;
		case NZ:
			break;
		case PE:
			break;
		case PL:
			break;
		case RU:
			break;
		case SE:
			break;
		case TW:
			break;
		case UA:
			break;
		case US:
			break;
		default:
			return R.drawable.flags_kr;
		}
		return 0;
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
		if(country.equalsIgnoreCase("ar") || country.equalsIgnoreCase("argentina")){
			return Country.AR;
		} else if (country.equalsIgnoreCase("at") || country.equalsIgnoreCase("austria")){
			return Country.AT;
		} else if (country.equalsIgnoreCase("au") || country.equalsIgnoreCase("australia")){
			return Country.AU;
		} else if (country.equalsIgnoreCase("be") || country.equalsIgnoreCase("belgium")){
			return Country.BE;
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
		} else if (country.equalsIgnoreCase("pl") || country.equalsIgnoreCase("poland")){
			return Country.PL;
		} else if (country.equalsIgnoreCase("ru") || country.equalsIgnoreCase("russia")){
			return Country.RU;
		} else if (country.equalsIgnoreCase("se") || country.equalsIgnoreCase("sweden")){
			return Country.SE;
		} else if (country.equalsIgnoreCase("tw") || country.equalsIgnoreCase("taiwan")){
			return Country.TW;
		} else if (country.equalsIgnoreCase("ua") || country.equalsIgnoreCase("ukraine")){
			return Country.UA;
		} else if (country.equalsIgnoreCase("us") || country.equalsIgnoreCase("usa")){
			return Country.US;
		} else {
			return Country.KR;
		}
	}
}
