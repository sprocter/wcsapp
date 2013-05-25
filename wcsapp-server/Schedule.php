<?php
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
			else if(strpos($s, ' – ') !== false){
				$timestampChunks = explode(' – ', $s);
				$timestamp[0] = strtotime(substr($timestampChunks[0], strrpos($timestampChunks[0], '>') + 1));
				$timestamp[1] = strtotime(substr($timestampChunks[1], 0, strpos($timestampChunks[1], '<')));
			} else
				$timestamp = strtotime(substr($s, 9, strpos($s, '</small>') - 9));
			if($timestamp !== false){
				return $timestamp;
			} else
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
			if($roPos !== false){
				$roundPos = strpos($s, 'Round of');
				if ($roundPos !== false) {
					return trim('Ro' . substr($s, $roundPos + 9, 2));
				} else if(strpos($s, '(') !== false && stripos($s, 'Bracket') !== false){
					return trim(substr($s, strpos($s, ':') + 2, 6));
				} else{
					return trim(substr($s, $roPos, 4));
				}
			} else {
				$colonPos = strpos($s, ': ');
				if($colonPos !== false)
					return trim(substr($s, 0, $colonPos));
				else 
					return trim($s);				
			}
			
		}
		
		static function getName($s){
			$colonPos = strpos($s, ': ');
			if($colonPos !== false){
				if(strpos($s, '(') !== false && stripos($s, 'Bracket') !== false){
					return substr($s, strpos($s, '(') + 1, 4);
				}
				return substr($s, $colonPos + 2);
			} else
				return $s;
		}
	}
?>
