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
	
	public function find_user($mobile,$isToken=false) {
		$res=$this->sql($q);
		if(mysql_affected_rows()==1) {
			return mysql_fetch_assoc($res);
		}
		return false;
	}
	
	public function generate_user_token($mobile) {
		$user=$this->find_user($mobile);
		if($user!==false) {
			$hash_data=json_encode($user);
			$userToken=hash_hmac("sha1",$hash_data,"webcodez|9722505033");
			$this->sql('update weteam_user set userToken="'.$updateTo.'" where userMobile='.$mobile);
			return $userToken;
		}
		return false;
	}
	public function register_user($mobile,$first_name,$last_name,$address) {
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