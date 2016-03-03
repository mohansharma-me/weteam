<?php
$return=array();
$return["dialogTitle"]="Error";
$return["dialogMessage"]="Unable to process your request, please try again.";

$token=filter_input(INPUT_GET,"token");

if(isset($token)) {
	require_once "class.database.php";
	$db=new Database();
	$user=$db->find_user($token,true);
	if($user!==false && is_array($user)) {
		//$user=$db->find_user($user["userMobile"]);
		if($user!==false) { // re-code it for editing feature having token
			$return["friends"]=$db->online_friends($user["userGroup"],$user["userMobile"]);
			$return["friend_list"]="";
			foreach($return["friends"] as $friend) {
				$return["friend_list"]=$friend["full_name"].", ".$return["friend_list"];
			}
			if(count($return["friends"])>0) {
				$return["friend_list"]=substr($return["friend_list"],0,strlen($return["friend_list"])-2);
			}
			$return["success"]=true;
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