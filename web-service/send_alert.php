<?php
$return=array();
$return["dialogTitle"]="Error";
$return["dialogMessage"]="Unable to process your request, please try again.";

$token=filter_input(INPUT_GET,"token");
$userJSON=filter_input(INPUT_GET,"userJSON");
$message=filter_input(INPUT_GET,"message");
if(!isset($message)) $message="Hey!! You got alerted!!";

if(isset($token,$userJSON)) {
	require_once "class.database.php";
	$db=new Database();
	$user=$db->find_user($token,true);
	if($user!==false && is_array($user)) {
		if($user!==false) {
			$users=json_decode($userJSON,true);
			$_mobiles=array();
			foreach($users as $__user) {
				$_mobiles[]=$__user["userMobile"];
			}
			$mobiles=implode(",",$_mobiles);
			$sender_mobile=$user["userMobile"];
			$flag=$db->send_alert($sender_mobile,$mobiles,$message);
			if($flag) {
				$return["success"]=true;
				$return["dialogTitle"]="Success";
				$return["dialogMessage"]="Congratulation, your alert send to your friend(s).";
			} else {
				$return["dialogTitle"]="Sorry :(";
				$return["dialogMessage"]="We're not able to send alerts right now, please try again.";
			}
		} else {
			$return["dialogTitle"]="Error";
			$return["dialogMessage"]="Sorry, this mobile number is already setted their profile.";
		}
	} else {
		$return["dialogTitle"]="Authorization Failed";
		$return["dialogMessage"]="Please try again by re-authenticating yourself through one-time password verification.";
	}
}
$json=json_encode($return);
header("Content-Length: ".strlen($json));
//ob_clean();
echo $json;