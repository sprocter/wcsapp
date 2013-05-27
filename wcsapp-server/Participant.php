<?php
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
			if(strlen(trim(substr($s, 0, strpos($s, "}}")))) > 30){
				return trim(substr($s, 0, strpos($s, "====")));
			} else 
				return trim(substr($s, 0, strpos($s, "}}")));
		}
		
		public static function getName($s){
			$s = strip_tags(str_replace('{{TA|2013_WCS_Season_1_America/Challenger#Bracket Stage}}', '',$s));
			$s = str_replace('{{TA|2013_WCS_Season_1_Europe/Challenger#Bracket Stage}}', '',$s);
			$dblCurlyBracePos = strpos($s, "}}");
			$pipePos = strrpos(substr($s, 0, $dblCurlyBracePos), '|') + 1;
			$nameLength = $dblCurlyBracePos - $pipePos;
			$name = trim(substr($s, $pipePos, $nameLength));
			if(strpos($name, '=') !== false){
				$equalsPos = strpos($name, '=') + 1;
				$spacePos = strpos($name, ' ');
				$nameLength = $spacePos - $equalsPos;
				if($spacePos === false)
					$name = substr($name, $equalsPos);
				else
					$name = substr($name, $equalsPos, $nameLength);
			}
			if(strpos($name, '_') !== false) // Who writes "Shuttle_(Korean_Terran)"?  Seriously?
				$name = substr($name, 0, strpos($name, '_'));
			return $name;		
		}
		
		public static function getResult($s){
			if(strpos($s, '|bg=') !== false)
				$key = 'bg';
			else
				$key = 'pbg';
			$keyPos = strpos($s, "|$key=") + strlen($key) + 2;
			$endPos = strpos($s, '}}', $keyPos);
			$valLength = $endPos - $keyPos;
			return trim(substr($s, $keyPos, $valLength));			
		}
		
		public static function getValue($s, $key){
			$keyPos = strpos($s, "|$key=") + strlen($key) + 2;
			$endPos = strpos($s, '|', $keyPos);
			$valLength = $endPos - $keyPos;
			return trim(substr($s, $keyPos, $valLength));
		}
	}
?>
