<?php
$return=array();
$return["dialogTitle"]="Error";
$return["dialogMessage"]="Unable to process your request, please try again.";

$mobile=filter_input(INPUT_GET,"mobile");
$otp=filter_input(INPUT_GET,"otp");

if(isset($mobile,$otp)) {
	require_once "class.database.php";
	$db=new Database();
	if($db->verify_otp($mobile,$otp)) {
		$user=$db->find_user($mobile);
		if($user!==false) {
			$return["userData"]=$user;
			$return["token"]=$db->generate_user_token($mobile);
			$return["new_user"]=false;
		} else {
			$return["new_user"]=true;
		}
		$return["dialogTitle"]="Success";
		$return["dialogMessage"]="Congratulation, you've verified your mobile number.";
		$return["success"]=true;
	} else {
		$return["dialogTitle"]="Invalid OTP";
		$return["dialogMessage"]="One-time password you provided isn't matched, please try again.";
	}
}

$json=json_encode($return);
header("Content-Length: ".strlen($json));
ob_clean();
echo $json;