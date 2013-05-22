<?php

	final class Match{
		public $id;
		public $matchname;
		public $matchnum;
		public $matchtype;
		public $scheduleid;
		public $player1name;
		public $player2name;
		public $player1race;
		public $player2race;
		public $player1flag;
		public $player2flag;
		public $player1wins;
		public $player2wins;
		public $winner;
		public $numgames;
		
		static function getTime($s){
			if(strpos($s, '(') !== false){ 
				$timestamp = strtotime(substr($s, 0, strpos($s, '(')));
				return $timestamp;
			} else {
				return 0;
			}
		}
		
		static function getBracketSummaries($s){
			$offset = 0;
			$bmsPos = strpos($s, '{{BracketMatchSummary', $offset) + 21;
			$summaries = array();
			while($bmsPos > 21){
				$offset = strpos($s, '}}', $offset);
				$length = $offset - $bmsPos;
				$summaries[] = substr($s, $bmsPos, $length);
				$bmsPos = strpos($s, '{{BracketMatchSummary', $offset) + 21;
				$offset++;
			}
			return $summaries;
		}
		
		static function getBracketVals($mwStr, $nested){
			$nameValPairs_arr = explode('|', $mwStr);
			$summaries = Match::getBracketSummaries($mwStr);
			$vals = array();
			foreach($nameValPairs_arr as $nameValPair_str){
				if(substr($nameValPair_str, 2, 10) == '<!-- Round' || trim($nameValPair_str) == '')
					continue;
				$nameAndValPair_arr = explode('=', $nameValPair_str);
				if(count($nameAndValPair_arr) < 2){
					continue;
				}
				if(substr($nameAndValPair_arr[1], 0, 2) == '{{')
					$vals[$nameAndValPair_arr[0]] = Match::getBracketVals(array_shift($summaries), true);
				else
					if(count($nameAndValPair_arr) > 2)
						$vals[$nameAndValPair_arr[0]] = trim($nameAndValPair_arr[1] . '=' . $nameAndValPair_arr[2]);
					else
						$vals[$nameAndValPair_arr[0]] = trim($nameAndValPair_arr[1]);
			}
			return $vals;
		}
		
		static function getName($s){
			return substr($s, 0, 7);
		}
		
		static function getNum($s){
			return substr($s, 0, strpos($s, '='));
		}
		
		static function getGroupPlayerName($s, $num){
			return Match::getValue($s, "player$num");
		}
		
		static function getGroupPlayerRace($s, $num){
			return Match::getValue($s, "player$num" . "race");
		}
		
		static function getGroupPlayerFlag($s, $num){
			return Match::getValue($s, "player$num" . "flag");
		}
		
		static function getGroupWinner($s){
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
?>
