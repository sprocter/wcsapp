<?php
	date_default_timezone_set('UTC');
	$matchId = 0;

	final class Match{
		public $id;
		public $matchname;
		public $matchnum;
		public $scheduleid;
		public $player1name;
		public $player2name;
		public $player1race;
		public $player2race;
		public $player1flag;
		public $player2flag;
		public $winner;
		public $numgames;
		
		static function splitTitle($title){
			$title_arr = explode('/', $title);
			
			if(strpos($title_arr[0], 'America') !== false)
				$region = 'AM';
			else if(strpos($title_arr[0], 'Europe') !== false)
				$region = 'EU';
			else if(strpos($title_arr[0], 'Korea') !== false)
				$region = 'KR';
			else
				$region = 'XX';
				
			if(strpos($title_arr[1], 'Premier') !== false || strpos($title_arr[1], 'Code S') !== false)
				$division = 'P';
			else if(strpos($title_arr[1], 'Challenger') !== false || strpos($title_arr[1], 'Code A') !== false)
				$division = 'C';
			else
				$region = 'X';
				
			$round = $title_arr[2];
			
			return array($region, $division, $round);
		}
		
		static function getName($s){
			return substr($s, 0, 7);
		}
		
		static function getNum($s){
			return substr($s, 0, strpos($s, '='));
		}
		
		static function getPlayerName($s, $num){
			return Match::getValue($s, "player$num");
		}
		
		static function getPlayerRace($s, $num){
			return Match::getValue($s, "player$num" . "race");
		}
		
		static function getPlayerFlag($s, $num){
			return Match::getValue($s, "player$num" . "flag");
		}
		
		static function getWinner($s){
			return Match::getValue($s, 'winner');
		}
		
		static function getNumGames($s){
			// Note there are sometimes typos in liquipedia's info, so we
			// count the number of vods.
			return substr_count($s, 'vodgame');
		}
		
		private static function getValue($s, $key){
			$keyPos = strpos($s, "|$key=") + strlen($key) + 2;
			$endPos = strpos($s, '|', $keyPos);
			$valLength = $endPos - $keyPos;
			return trim(substr($s, $keyPos, $valLength));
		}
	}
	
	final class Game{
		public $mapname;
		public $mapwinner;
		public $vodlink;
		public $matchid;
		
		public static function getMapName($s, $n){
			$keyPos = strpos($s, "|map$n=") + strlen($n) + 5;
			$endPos = strpos($s, "\n", $keyPos);
			$valLength = $endPos - $keyPos;
			return trim(substr($s, $keyPos, $valLength));
		}
		
		public static function getMapWinner($s, $n){
			$keyPos = strpos($s, "|map$n" . 'win=') + strlen($n) + 8;
			$endPos = strpos($s, '|', $keyPos) - 1;
			$valLength = $endPos - $keyPos;
			return trim(substr($s, $keyPos, $valLength));
		}
		
		public static function getVodLink($s, $n){
			$keyPos = strpos($s, "|vodgame$n=") + strlen($n) + 9;
			$endPos = strpos($s, "\n", $keyPos);
			$valLength = $endPos - $keyPos;
			return trim(substr($s, $keyPos, $valLength));
		}
	}
	
	final class Schedule{
		public $id;
		public $time;
		public $division;
		public $region;
		public $round;
		public $name;

		static function getRegion($s){
			if(stripos($s, 'korea') !== false)
				return 'KR';
			else if(stripos($s, 'europe') !== false)
				return 'EU';
			else if(stripos($s, 'america') !== false)
				return 'AM';
			else
				return 'XX';
		}
		
		static function getTime($s){
			if(strpos($s, '(') !== false)
				$timestamp = strtotime(substr($s, 9, strpos($s, '(') - 9));
			else if(strpos($s, ' – ') !== false)
				$timestamp = strtotime(substr($s, 9, strpos($s, ' – ') - 9));			
			else
				$timestamp = strtotime(substr($s, 9, strpos($s, '</small>') - 9));
			if($timestamp !== false)
				return $timestamp * 1000; // Java uses milliseconds since the Unix epoch
			else
				throw new Exception("TimeFormatException: Couldn't parse $s");
		}
	
		static function getDivision($s){
			if(stripos($s, 'premier') !== false || stripos($s, 'code_s') !== false)
				return 'P';
			else if(stripos($s, 'challenger') !== false || stripos($s, 'code_s') !== false) 
				return 'C';
			else
				return 'X';
		}
	
		static function getRoundAndName($s){
			$dbl_curly_brace_pos = strpos($s, '}}');
			$pipe_pos = strpos($s, '|', $dbl_curly_brace_pos) + 1;
			$dbl_square_brace_pos = strpos($s, ']]', $pipe_pos);
			$match_name_len = $dbl_square_brace_pos - $pipe_pos;
			return substr($s, $pipe_pos, $match_name_len);
		}
	
		static function getRound($s){
			$roPos = strpos($s, 'Ro');
			if($roPos !== false)
				return substr($s, $roPos, 4);
			else {
				$colonPos = strpos($s, ': ');
				if($colonPos !== false)
					return substr($s, 0, $colonPos);
				else
					return $s;
			}
			
		}
		
		static function getName($s){
			$colonPos = strpos($s, ': ');
			if($colonPos !== false)
				return substr($s, $colonPos + 2);
			else
				return $s;
		}
	}

	final class Participant{
		public $name;
		public $flag;
		public $race;
		public $place;
		public $matcheswon;
		public $matcheslost;
		public $mapswon;
		public $mapslost;
		public $result;
		public $scheduleid;
		
		public static function getGroupName($s){
			return trim(substr($s, 0, strpos($s, "}}")));
		}
		
		public static function getName($s){
			$dblCurlyBracePos = strpos($s, "}}");
			$pipePos = strrpos(substr($s, 0, $dblCurlyBracePos), '|') + 1;
			$nameLength = $dblCurlyBracePos - $pipePos;
			return trim(substr($s, $pipePos, $nameLength));		
		}
		
		public static function getValue($s, $key){
			$keyPos = strpos($s, "|$key=") + strlen($key) + 2;
			$endPos = strpos($s, '|', $keyPos);
			$valLength = $endPos - $keyPos;
			return trim(substr($s, $keyPos, $valLength));
		}
	}

	function parseSchedule($mwtext_str){
		global $db, $scheduleIdMap;
		$mwsched_arr = explode('Countdown',$mwtext_str);
		$st = $db->prepare('INSERT INTO schedule (id, time, division, region, name, round) values (:id, :time, :division, :region, :name, :round)');
		$sched = new Schedule();
		$id = 0;
		foreach ($mwsched_arr as $s) {
			if(substr($s, 0, 9) != "\n|<small>")
				continue;
			$id++;
			$sched->id = $id;
			$sched->region = Schedule::getRegion($s);
			$sched->time = Schedule::getTime($s);
			$sched->division = Schedule::getDivision($s);
			$roundAndName = Schedule::getRoundAndName($s);
			$sched->name = Schedule::getName($roundAndName);
			$sched->round = Schedule::getRound($roundAndName);
			$scheduleIdMap[$sched->region][$sched->division][$sched->round][$sched->name] = $id;
			$st->execute((array)$sched);
		}
	}
	
	function parseMatches($title, $mwtext_str){
		global $db, $scheduleIdMap, $matchId;
		list($region, $division, $round) = Match::splitTitle($title);
		$fields[] = "id";
		$fields[] = "winner";
		$fields[] = "player1name";
		$fields[] = "player2name";
		$fields[] = "player1race";
		$fields[] = "player2race";
		$fields[] = "player1flag";
		$fields[] = "player2flag";
		$fields[] = "numgames";
		$fields[] = "matchname";
		$fields[] = "scheduleid";
		$fields[] = "matchnum";
		$colNames = implode (', ', $fields);
		$valNames = ':' . implode (', :', $fields);
		$st = $db->prepare("INSERT INTO matches ($colNames) values ($valNames)");
		$group_arr = explode('{{HiddenSort|', $mwtext_str);
		$m = new Match();
		foreach($group_arr as $group_str){
			if(substr($group_str, 0, 5) != 'Group')
				continue;
			$scheduleName = Match::getName($group_str);
			if(!array_key_exists($region, $scheduleIdMap) || 
				!array_key_exists($division, $scheduleIdMap[$region]) ||
				!array_key_exists($round, $scheduleIdMap[$region][$division]) ||
				!array_key_exists($scheduleName, $scheduleIdMap[$region][$division][$round]))
				continue;
			$scheduleId = $scheduleIdMap[$region][$division][$round][$scheduleName];
			//if($scheduleId == null || $scheduleId == '')
				// Shouldn't happen...
			$match_arr = explode('|match', $group_str);
			foreach($match_arr as $match_str){
				if(strpos($match_str, 'MatchMaps') === false)
					continue;
				$matchId++;
				$m->id = $matchId;
				$m->matchname = $scheduleName;
				$m->matchnum = Match::getNum($match_str);
				$m->scheduleid = $scheduleId;
				$m->player1name = Match::getPlayerName($match_str, 1);
				$m->player2name = Match::getPlayerName($match_str, 2);
				$m->player1race = Match::getPlayerRace($match_str, 1);
				$m->player2race = Match::getPlayerRace($match_str, 2);
				$m->player1flag = Match::getPlayerFlag($match_str, 1);
				$m->player2flag = Match::getPlayerFlag($match_str, 2);
				$m->winner = Match::getWinner($match_str);
				$m->numgames = Match::getNumGames($match_str);
				$st->execute((array)$m);
				$gamesToParse[] = array($match_str, $m->numgames, $matchId);
			}
		}
		
		$fields = array();
		$fields[] = "mapname";
		$fields[] = "mapwinner";
		$fields[] = "vodlink";
		$fields[] = "matchid";
		$colNames = implode (', ', $fields);
		$valNames = ':' . implode (', :', $fields);
		$st = $db->prepare("INSERT INTO games ($colNames) values ($valNames)");
		foreach($gamesToParse as $unparsedGame){
			$parsedGames = parseGames($unparsedGame[0], $unparsedGame[1], $unparsedGame[2], $st);
		}
	}
	
	function parseParticipants($title, $mwtext_str){
		global $db, $scheduleIdMap;
		list($region, $division, $round) = Match::splitTitle($title);
		//TODO: Refactor this query generation into a function?
		$fields[] = "name";
		$fields[] = "flag";
		$fields[] = "race";
		$fields[] = "place";
		$fields[] = "matcheswon";
		$fields[] = "matcheslost";
		$fields[] = "mapswon";
		$fields[] = "mapslost";
		$fields[] = "result";
		$fields[] = "scheduleid";
		$colNames = implode (', ', $fields);
		$valNames = ':' . implode (', :', $fields);
		$st = $db->prepare("INSERT INTO participants ($colNames) values ($valNames)");
		$group_arr = explode('{{HiddenSort|', $mwtext_str);
		foreach ($group_arr as $group_str){
			if(substr($group_str, 0, 5) != "Group")
				continue;
			$scheduleName = Participant::getGroupName($group_str);
			$partipant_arr = explode('{{GroupTableSlot|', $group_str);
			$p = new Participant();
			foreach($partipant_arr as $s){
				if(substr($s, 0, 9) != " {{player")
					continue;
				$p->name = Participant::getName($s);
				$p->flag = Participant::getValue($s, 'flag');
				$p->race = Participant::getValue($s, 'race');
				$p->place = Participant::getValue($s, 'place');
				$p->matcheswon = Participant::getValue($s, 'win_m');
				$p->matcheslost = Participant::getValue($s, 'lose_m');
				$p->mapswon = Participant::getValue($s, 'win_g');
				$p->mapslost = Participant::getValue($s, 'lose_g');
				$p->result = Participant::getValue($s, 'bg');
				//TODO: Put this in it's own method...
				if(!array_key_exists($region, $scheduleIdMap) || 
				!array_key_exists($division, $scheduleIdMap[$region]) ||
				!array_key_exists($round, $scheduleIdMap[$region][$division]) ||
				!array_key_exists($scheduleName, $scheduleIdMap[$region][$division][$round]))
					continue;
				$scheduleId = $scheduleIdMap[$region][$division][$round][$scheduleName];
				$st->execute((array)$p);
			}
		}
	}
	
	function parseGames($s, $numGames, $matchId, $st){
		$g = new Game();
		for($i = 1; $i <= $numGames; $i++){
			$g->mapname = Game::getMapName($s, $i);
			$g->mapwinner = Game::getMapWinner($s, $i);
			$g->vodlink = Game::getVodLink($s, $i);
			$g->matchid = $matchId;
			$st->execute((array)$g);
		}
	}
	
	$titles[] = '2013_StarCraft_II_World_Championship_Series/Schedule';
	$titles[] = '2013_WCS_Season_1_America/Premier/Ro32';
	$titles[] = '2013_WCS_Season_1_America/Premier/Ro16';
	$titles[] = '2013_WCS_Season_1_Europe/Premier/Ro32';
	$titles[] = '2013_WCS_Season_1_Europe/Premier/Ro16';
	$titles[] = '2013 WCS Season 1 Korea GSL/Code S/Ro32';
	$titles[] = '2013 WCS Season 1 Korea GSL/Code S/Ro16';
	$url = 'http://wiki.teamliquid.net/starcraft2/api.php?action=query&export&exportnowrap&titles=' . implode ('|', $titles);
	
	try{
		$mediawiki_obj = simplexml_load_file($url);
		$db = new PDO("sqlite:wcsapp.sqlite");
	 	$db->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
	 	$db->exec('DROP TABLE IF EXISTS games');
	 	$db->exec('DROP TABLE IF EXISTS matches');
	 	$db->exec('DROP TABLE IF EXISTS schedule');
	 	$db->exec('DROP TABLE IF EXISTS participants');
	 	$db->exec('CREATE TABLE "games" ("id" INTEGER PRIMARY KEY  NOT NULL ,"mapname" TEXT,"mapwinner" INTEGER DEFAULT (null) ,"vodlink" TEXT,"matchid" INTEGER NOT NULL  DEFAULT (null) );');
	 	$db->exec('CREATE TABLE "matches" ("id" INTEGER PRIMARY KEY  NOT NULL ,"winner" TEXT,"player1name" TEXT,"player2name" TEXT,"player1race" TEXT,"player2race" TEXT,"player1flag" TEXT,"player2flag" TEXT,"numgames" INTEGER DEFAULT (null) ,"matchname" TEXT,"scheduleid" INTEGER NOT NULL  DEFAULT (null) , "matchnum" INTEGER);');
	 	$db->exec('CREATE TABLE "schedule" ("id" INTEGER PRIMARY KEY NOT NULL ,"time" INTEGER,"division" TEXT,"region" TEXT,"name" TEXT, "round" TEXT);');
	 	$db->exec('CREATE TABLE "participants" ("id" INTEGER PRIMARY KEY NOT NULL, "name" TEXT, "flag" TEXT, "race" TEXT, "place" INTEGER, "matcheswon" INTEGER, "matcheslost" INTEGER, "mapswon" INTEGER, "mapslost" INTEGER, "result" TEXT, "scheduleid" INTEGER)');
		parseSchedule($mediawiki_obj->page[0]->revision->text);
		for($i = 1; $i <= 6; $i++){
			parseMatches($mediawiki_obj->page[$i]->title, $mediawiki_obj->page[$i]->revision->text);
			parseParticipants($mediawiki_obj->page[$i]->title, $mediawiki_obj->page[$i]->revision->text);
		}
		$db = null;
		`echo '.dump' | sqlite3 wcsapp.sqlite | gzip -c > wcsapp.dump.gz`;
	} catch (Exception $e){
		file_put_contents('errors.txt', $e->getMessage() . "\n", FILE_APPEND);
	}
?>
