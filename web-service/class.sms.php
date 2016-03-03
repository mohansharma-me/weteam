<?php

class SMS {
	
	//http://59.162.167.52/api/MessageCompose?admin=mail@wcodez.com&user=finnecle@wcodez.com:9724777773&senderID=TEST SMS&receipientno=9724777773&msgtxt=hello&state=4
	var $_admin="";
	var $_user="";
	var $_sender_id="";
	var $_state="";
	
	var $mobile=null;
	
	var $_result=null;
	
	public function __construct($mobile, $admin="mail@wcodez.com", $user="weteam@wcodez.com:9724777773", $sender_id="TEST SMS", $state=4) {
		$this->mobile=$mobile;
		
		$this->_admin=urlencode($admin);
		$this->_user=urlencode($user);
		$this->_sender_id=urlencode($sender_id);
		$this->_state=urlencode($state);
		
	}
	
	public function send($message) {
		$message=urlencode($message);
		$link='http://59.162.167.52/api/MessageCompose?admin='.$this->_admin.'&user='.$this->_user.'&senderID='.$this->_sender_id.'&receipientno='.$this->mobile.'&msgtxt='.$message.'&state='.$this->_state;
		$_result=file($link);
		return $this->_result;
	}

	public function send_otp() {
		require_once "class.database.php";
		$db=new Database();
		$code=$db->generateOTP($this->mobile);
		if($code!==false) {
			$this->send("Your One-Time Password for WeTeam Application is : ".$code);
			return true;
		}
		return false;
	}
	
}