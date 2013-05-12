<?php
	#$x = simplexml_load_file('http://wiki.teamliquid.net/starcraft2/api.php?action=query&pageids=36486&export&exportnowrap');
	#echo $x->page->revision->text;
	#die();
	function parseMWSchedule($mwsched_obj){
		$mwsched_arr = explode('Countdown',$mwsched_obj->page->revision->text);
		foreach ($mwsched_arr as $mwsched_single) {
			if(substr($mwsched_single, 0, 9) != "\n|<small>")
				continue;
			#echo "$mwsched_single \n";
			if(stripos($mwsched_single, 'korea') !== false)
				$str = "KR: ";
			else if(stripos($mwsched_single, 'europe') !== false)
				$str = "EU: ";
			else if(stripos($mwsched_single, 'america') !== false)
				$str = "AM: ";
			$str .= substr($mwsched_single, 9, strpos($mwsched_single, '</small>') - 9);
			if(stripos($mwsched_single, 'premier') !== false || stripos($mwsched_single, 'code_s') !== false)
				$str .= '{P}';
			else if(stripos($mwsched_single, 'challenger') !== false || stripos($mwsched_single, 'code_s') !== false) 
				$str .= '{C}';
			$dbl_curly_brace_pos = strpos($mwsched_single, '}}');
			$pipe_pos = strpos($mwsched_single, '|', $dbl_curly_brace_pos);
			$dbl_square_brace_pos = strpos($mwsched_single, ']]', $pipe_pos);
			$match_name_len = $dbl_square_brace_pos - $pipe_pos;
			$str .= substr($mwsched_single, $pipe_pos, $match_name_len);
			#echo "{{=$dbl_curly_brace_pos, |=$pipe_pos, len=$match_name_len\n";
			echo "$str\n";
		}
	}

	parseMWSchedule(simplexml_load_file('http://wiki.teamliquid.net/starcraft2/api.php?action=query&pageids=36486&export&exportnowrap'));
?>
