<?php
require 'S3.php';

function uploadToS3(){
	$keys = file('s3keys.txt');
	
	$ACCESS_KEY = trim($keys[0]);
	$SECRET_KEY = trim($keys[1]);
	
	$FILENAME = 'wcsapp.dump.gz';
	$BUCKET_NAME = 'sc2wcsapp';
	$UPLOAD_NAME = 'data/sqlite.gz';
			
	$s3 = new S3($ACCESS_KEY, $SECRET_KEY, false, 'objects.dreamhost.com');
	$s3->putObject($s3->inputFile($FILENAME, false), $BUCKET_NAME, $UPLOAD_NAME, S3::ACL_PUBLIC_READ);
}
?>