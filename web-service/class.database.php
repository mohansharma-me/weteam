<?php

class Database {
	
	var $_server="localhost";
	var $_username="wcodez_developer";
	var $_password="??33Ramji";
	var $_database="";
	
	var $mysqlConnection=null;
	var $mysqlDatabase=null;
	
	public function __construct($database="wcodez_updates") {
		$this->_database=$database;
		$this->mysqlConnection=mysql_connect($this->_server,$this->_username,$this->_password,true);
		if($this->mysqlConnection) {
			$this->mysqlDatabase=mysql_select_db($this->_database,$this->mysqlConnection);
		}
	}
	
	public function sql($query) {
		return mysql_query($query,$this->mysqlConnection);
	}
	
	public function find_user($mobile,$isToken=false) {		$q='select * from weteam_user where userMobile='.$mobile;		if($isToken) {			if(strlen($mobile)<strlen("83b57d4369a775437220704c544c8f5b8f21382b")-5) 			{				echo "RET";				return false;			}			$q='select * from weteam_user where userToken LIKE "%'.$mobile.'%"';		}
		$res=$this->sql($q);
		if(mysql_affected_rows()==1) {
			return mysql_fetch_assoc($res);
		}
		return false;
	}	public function online_friends($group,$skipMobile=null) {		date_default_timezone_set("Asia/Calcutta");		$current_time=time()-15;		$q='select * from weteam_user where userOnlineTime>="'.date("Y-m-d H:i:s",$current_time).'" and userGroup='.addslashes($group);		$res=$this->sql($q);		$array=array();		if(mysql_affected_rows()>0) {			while($row=mysql_fetch_assoc($res)) {				$arr=array();				$arr["full_name"]=ucwords($row["userFirstname"]." ".$row["userLastname"]." (".$row["userMobile"].")");				$arr["userMobile"]=$row["userMobile"];				if(isset($skipMobile) && $skipMobile!=$arr["userMobile"]) {					$array[]=$arr;				} else if(!isset($skipMobile)) {					$array[]=$arr;				}								}		}		return $array;	}		public function get_alert($mobile,$alert_id) {		$res=$this->sql('select * from weteam_alert, weteam_user where alertSenderMobile!='.addslashes($mobile).' and userMobile=alertSenderMobile and alertId>'.addslashes($alert_id).' and alertReceiverMobiles LIKE "%'.addslashes($mobile).'%" limit 1');		if(mysql_affected_rows()>0) {			$row=mysql_fetch_assoc($res);			unset($row["userStatus"]);			unset($row["userToken"]);			unset($row["userOnlineTime"]);			unset($row["userGroup"]);			$row["userName"]=ucwords($row["userFirstname"]." ".$row["userLastname"]." (".$row["userMobile"].")");			return $row;		}		return false;	}		public function send_alert($from,$tos,$message) {		date_default_timezone_set("Asia/Calcutta");		$this->sql('insert into weteam_alert(alertSenderMobile,alertMessage,alertReceiverMobiles,alertTime) values('.addslashes($from).',"'.addslashes($message).'","'.addslashes($tos).'","'.date("Y-m-d H:i:s",time()).'")');		return mysql_affected_rows()==1;	}		public function updateOnlineTime($mobile) {		date_default_timezone_set("Asia/Calcutta");		$this->sql('update weteam_user set userOnlineTime="'.date("Y-m-d H:i:s",time()).'" where userMobile='.addslashes($mobile));	}
	
	public function generate_user_token($mobile) {
		$user=$this->find_user($mobile);
		if($user!==false) {
			$hash_data=json_encode($user);
			$userToken=hash_hmac("sha1",$hash_data,"webcodez|9722505033");			$updateTo=$user["userToken"].",".$userToken;
			$this->sql('update weteam_user set userToken="'.$updateTo.'" where userMobile='.$mobile);
			return $userToken;
		}
		return false;
	}
	public function register_user($mobile,$first_name,$last_name,$address) {		$this->sql('insert into weteam_user(userMobile,userFirstname,userLastname,userAddress) values('.addslashes($mobile).',"'.addslashes($first_name).'","'.addslashes($last_name).'","'.addslashes($address).'")');		return mysql_affected_rows()==1;	}
	public function verify_otp($mobile,$otp) {
		if(!is_numeric($mobile)) return false;
		$mobile=addslashes($mobile);
		$this->sql('select * from weteam_otp where otpCode='.$otp.' and otpMobile='.$mobile);
		return mysql_affected_rows()==1;
	}
	
	public function generateOTP($mobile) {
		if(!is_numeric($mobile)) return false;
		$mobile=addslashes($mobile);
		$code=rand(100000,999999);
		
		$this->sql('select * from weteam_otp where otpMobile='.$mobile);
		if(mysql_affected_rows()>0) {
			$q='update weteam_otp set otpCode='.$code.' where otpMobile='.$mobile;
		} else {
			$q='insert into weteam_otp(otpMobile,otpCode) values('.$mobile.','.$code.')';
		}
		if($this->sql($q)!==false) {
			return $code;
		}
		return false;
	}
	
}