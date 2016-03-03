<?php
file_put_contents("lastRequest.html",json_encode($_REQUEST));
$return=array();
$return["success"]=false;

$token=filter_input(INPUT_GET,"token");
$updateTime=filter_input(INPUT_GET,"updateTime");
if(!isset($updateTime)) $updateTime="false";
$lastAlert=filter_input(INPUT_GET,"lastAlert");
if(!isset($lastAlert)) $lastAlert=0;

if(isset($token,$updateTime,$lastAlert)) {
	require_once "class.database.php";
	$db=new Database();
	$user=$db->find_user($token,true);
	if($user!==false && is_array($user)) {
		if($updateTime=="true") {
			$db->updateOnlineTime($user["userMobile"]);
			$alert=$db->get_alert($user["userMobile"],$lastAlert);
			if($alert!==false) {
				$return["alert"]=$alert;
			}
		}
		$return["success"]=true;
		$return["userStatus"]=$user["userStatus"];
	}
}

$json=json_encode($return);
header("Content-Length: ".strlen($json));
//ob_clean();
echo $json;