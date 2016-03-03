<?php
$return=array();
$return["dialogTitle"]="Error";
$return["dialogMessage"]="Unable to process your request, please try again.";

$mobile=filter_input(INPUT_GET,"mobile");
if(isset($mobile)) {
	if(is_numeric($mobile)) {
		if(strlen($mobile)==10) {
			require_once "class.sms.php";
			$sms=new SMS($mobile);
			if($sms->send_otp()) {
				$return["success"]=true;
				$return["dialogTitle"]="OTP Sent!!";
				$return["dialogMessage"]="Please wait while you got text message from us having your one-time password.";
			} else {
				$return["dialogTitle"]="Error #1";
				$return["dialogMessage"]="Unable to send one-time password to your mobile, please try again.";
			}
		} else {
			$return["dialogTitle"]="Mobile Number";
			$return["dialogMessage"]="Your mobile number must be of length 10.";
		}
	} else {
		$return["dialogTitle"]="Invalid mobile number";
		$return["dialogMessage"]="Please enter valid mobile number.";
	}
}

$json=json_encode($return);
header("Content-Length: ".strlen($json));
ob_clean();
echo $json;
