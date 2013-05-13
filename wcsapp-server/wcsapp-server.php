<?php
	date_default_timezone_set('UTC');

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
				
			if(strpos($title_arr[1], 'Premier') !== false)
				$division = 'P';
			else if(strpos($title_arr[1], 'Challenger') !== false)
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
	}
	
	final class Game{
		public $mapname;
		public $mapwinner;
		public $vodlink;
		public $setid;
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
			try {
				$sched->id = $id;
				$sched->region = Schedule::getRegion($s);
				$sched->time = Schedule::getTime($s);
				$sched->division = Schedule::getDivision($s);
				$roundAndName = Schedule::getRoundAndName($s);
				$sched->name = Schedule::getName($roundAndName);
				$sched->round = Schedule::getRound($roundAndName);
				$scheduleIdMap[$sched->region][$sched->division][$sched->round][$sched->name] = $id;
				$st->execute((array)$sched);
			} catch (Exception $e){
				file_put_contents('errors.txt', $e->getMessage() . "\n", FILE_APPEND);
			}
		}
	}
	
	function parseMatches($title, $mwtext_str){
		/*
		|match1={{MatchMaps
		|vodgame1=http://www.youtube.com/watch?v=75IepHhCUh0&list=SPn9kCgJGjpyL8KjCZeWMoUO4x0_pdwO2F&index=1
		|vodgame2=http://www.youtube.com/watch?v=Y6aruKHpMaM&list=SPn9kCgJGjpyL8KjCZeWMoUO4x0_pdwO2F&index=2
		|vodgame3=http://www.youtube.com/watch?v=MfUcakHfWlk&list=SPn9kCgJGjpyL8KjCZeWMoUO4x0_pdwO2F&index=3
		|player1=KiLLeR |player1race=z |player1flag=cl
		|player2=aLive |player2race=t |player2flag=kr
		|winner=1
		|map1win=2 |map1=Whirlwind
		|map2win=1 |map2=Cloud Kingdom
		|map3win=1 |map3=Akilon Wastes
		*/
		global $db, $scheduleIdMap;
		list($region, $division, $round) = Match::splitTitle($title);
		$group_arr = explode('{{HiddenSort|', $mwtext_str);
		$m = new Match();
		$id = 0;
		foreach($group_arr as $group_str){
			if(substr($group_str, 0, 5) != 'Group')
				continue;
			$scheduleName = Match::getName($group_str);
			$scheduleId = $scheduleIdMap[$region][$division][$round][$name];
			$match_arr = explode('|match', $mwtext_str);
			foreach($match_arr as $match_str){
				if(strpos($match_str, 'MatchMaps') === false)
					continue;
				$id++;
				$m->id = $id;
				$m->matchname = $scheduleName;
				$m->matchnum = Match::getNum($match_str);
				$m->scheduleid = $scheduleId;
				$m->player1name = Match::getPlayerName(1, $match_str);
				$m->player2name = Match::getPlayerName(2, $match_str);
				$m->player1race = Match::getPlayerRace(1, $match_str);
				$m->player2race = Match::getPlayerRace(2, $match_str);
				$m->player1flag = Match::getPlayerFlag(1, $match_str);
				$m->player2flag = Match::getPlayerFlag(2, $match_str);
				$m->winner = Match::getWinner($match_str);
				$m->numgames = Match::getNumGames($match_str);
				parseGames($match_str, $m->numgames, $id);
			}
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
	
	$mediawiki_obj = simplexml_load_file($url);
	$db = new PDO("sqlite:/home/sam/workspace/wcsapp-server/wcsapp.sqlite");
 	$db->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
	#parseSchedule($mediawiki_obj->page[0]->revision->text);
	for($i = 2; $i <= 6; $i++)
		parseMatches($mediawiki_obj->page[$i]->title, $mediawiki_obj->page[$i]->revision->text);
	$db = null;
?>
