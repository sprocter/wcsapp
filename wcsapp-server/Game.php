<?php
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
?>
