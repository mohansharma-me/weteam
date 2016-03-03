package com.webcodez.weteam.extra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.webcodez.weteam.job;

public class SMSListener extends BroadcastReceiver {

    public static final String SENDER_ID="";

    public SMSListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction()==Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msgFrom;
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msgFrom = msgs[i].getOriginatingAddress();
                        String msg=msgs[i].getMessageBody();
                        //Toast.makeText(context,"MsgFrom:"+msgFrom,Toast.LENGTH_SHORT).show();
                        if(msgFrom.toLowerCase().contains("-wcodez") && msg.length()>=6) {
                            try {
                                int length="Dear, ".length();
                                int code = Integer.parseInt(msg.substring(length,length+6));
                                job.setOTPCode(code);
                                job.setBoolean("autoOTP",true);
                                //Toast.makeText(context, "WeTeam OTP Received : " + code, Toast.LENGTH_LONG).show();
                                break;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                //Toast.makeText(context,"Invalid OTP Code Received, Please try again. ["+ex.getMessage()+"]",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            System.out.println("MEssage Skiped:"+msg+" ("+msgFrom+")");
                            //Toast.makeText(context,"MsgSkiped:"+msg,Toast.LENGTH_LONG).show();
                        }
                    }
                }catch(Exception e){
                     Log.d("Exception caught in SMSListener: ", e.getMessage());
                }
            }
        }

    }
}
