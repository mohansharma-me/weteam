package com.webcodez.weteam;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

/**
 * Created by iAmMegamohan on 08-09-2015.
 */
public class job {

    static SharedPreferences sharedPreferences=null;

    public static boolean init(Context context) throws Exception{
        try {
            sharedPreferences=context.getSharedPreferences("WeTeamApp",Context.MODE_PRIVATE);
            return true;
        } catch(Exception ex) {
            throw new Exception("Unable to initialize application. ["+ex.getMessage()+"]");
        }
    }

    public static boolean isServiceRunning() throws Exception{
        try {
            return sharedPreferences.getBoolean("isService",false);
        } catch (Exception ex) {
            throw new Exception("Unable to fetch WeTeam service details. ["+ex.getMessage()+"]");
        }
    }

    public static boolean setServiceRunning(boolean serviceStatus) throws Exception{
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("isService", serviceStatus);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to start/stop service. ["+ex+"]");
        }
    }

    public static boolean removeKey(String name) throws Exception{
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove(name);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to remove key="+name+". ["+ex+"]");
        }
    }


    public static int getInt(String name, int default_value) throws Exception{
        try {
            return sharedPreferences.getInt(name,default_value);
        } catch (Exception ex) {
            throw new Exception("Unable to get integer. ["+ex+"]");
        }
    }

    public static boolean setInt(String name, int value) throws  Exception{
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt(name, value);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set integer value ["+name+"="+value+"]. ["+ex+"]");
        }
    }

    public static boolean getBoolean(String name, boolean default_value) throws Exception{
        try {
            return sharedPreferences.getBoolean(name,default_value);
        } catch (Exception ex) {
            throw new Exception("Unable to get boolean. ["+ex+"]");
        }
    }

    public static boolean setBoolean(String name, boolean value) throws  Exception{
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(name, value);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set boolean value ["+name+"="+value+"]. ["+ex+"]");
        }
    }

    public static String getString(String name, String default_value) throws Exception{
        try {
            return sharedPreferences.getString(name,default_value);
        } catch (Exception ex) {
            throw new Exception("Unable to get string. ["+ex+"]");
        }
    }

    public static boolean setString(String name, String value) throws  Exception{
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString(name, value);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set string value ["+name+"="+value+"]. ["+ex+"]");
        }
    }


    public static boolean setLastAlert(long id) throws Exception{
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putLong("lastAlert", id);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set last alert. ["+ex+"]");
        }
    }

    public static long getLastAlert() throws Exception {
        try {
            return sharedPreferences.getLong("lastAlert",0);
        } catch (Exception ex) {
            throw new Exception("Unable to get last alert. ["+ex+"]");
        }
    }

    public static boolean alertMode(boolean new_value, boolean set) throws Exception{
        try {
            if(set) {
                setBoolean("alertMode",new_value);
            }
            return getBoolean("alertMode",false);
        } catch (Exception ex) { throw ex; }
    }

    public static boolean alertMode() throws Exception {
        return alertMode(false,false);
    }

    public static boolean isLogged() throws Exception{
        try {
            return sharedPreferences.getBoolean("isLogged",false);
        } catch (Exception ex) {
            throw new Exception("Unable to fetch login session. ["+ex.getMessage()+"]");
        }
    }

    public static boolean setLogged(boolean isLogged) throws Exception{
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("isLogged", isLogged);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to start/stop session. ["+ex+"]");
        }
    }


    public static boolean logoutNow() throws Exception{
        try {
            return setLogged(false) &&
            setUserData(null) &&
            setUserToken(null) &&
            setOTPCode(0) &&
            setMobileNumber(0);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public static String getUserToken() throws Exception{
        try {
            return sharedPreferences.getString("userToken",null);
        } catch (Exception ex) {
            throw new Exception("Unable to get user token. ["+ex+"]");
        }
    }

    public static boolean setUserToken(String token) throws Exception {
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("userToken", token);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set user token. ["+ex+"]");
        }
    }

    public static boolean setOTPCode(int otp) throws Exception {
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("otpCode",otp);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set otp. ["+ex.getMessage()+"]");
        }
    }

    public static int getOTPCode() throws  Exception {
        try {
            return sharedPreferences.getInt("otpCode",0);
        } catch (Exception ex) {
            throw new Exception("Unable to get otp. ["+ex.getMessage()+"]");
        }
    }

    public static long getMobileNumber() throws Exception {
        try {
            return sharedPreferences.getLong("mobileNumber",0);
        } catch (Exception ex) {
            throw new Exception("Unable to get mobile number. ["+ex.getMessage()+"]");
        }
    }

    public static boolean setMobileNumber(long mobile) throws Exception {
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putLong("mobileNumber",mobile);
            return editor.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set mobile number. ["+ex.getMessage()+"]");
        }
    }

    public static boolean setUserData(String json) throws  Exception{
        try {
            SharedPreferences.Editor edit=sharedPreferences.edit();
            edit.putString("userData", json);
            edit.apply();
            return edit.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set user data. ["+ex+"]");
        }
    }

    public static String getUserData() throws Exception {
        try {
            return sharedPreferences.getString("userData",null);
        } catch (Exception ex) {
            throw new Exception("Unable to get user data. ["+ex+"]");
        }
    }

    public static int getUserStatus() throws Exception {
        try {

            if(sharedPreferences.contains("userStatus")) {
                return sharedPreferences.getInt("userStatus",0);
            }

            return 0;
        } catch (Exception ex) {
            throw new Exception("Unable to get user status. ["+ex+"]");
        }
    }

    public static boolean setUserStatus(int status) throws Exception {
        try {
            SharedPreferences.Editor edit=sharedPreferences.edit();
            edit.putInt("userStatus",status);
            edit.apply();
            return edit.commit();
        } catch (Exception ex) {
            throw new Exception("Unable to set user status. ["+ex+"]");
        }
    }

    public static class network {

        public static String SERVER="http://weteam.wcodez.com/";

        public static String DownloadString(String scriptName,Hashtable<String, String> parameters) throws Exception {
            try {

                try
                {
                    String userToken=null;
                    try {
                        userToken=job.getUserToken();
                    } catch (Exception ex) {}
                    String queryString=userToken==null?"":"token="+userToken;
                    Enumeration<String> keys=parameters.keys();
                    while(keys.hasMoreElements()) {
                        String key=keys.nextElement();
                        String value=parameters.get(key);
                        queryString+="&"+key+"="+ URLEncoder.encode(value, "UTF-8");
                    }
                    StringBuffer stringBuffer=new StringBuffer();
                    URL url = new URL(SERVER+scriptName+"?"+queryString);
                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    while ((str = in.readLine()) != null)
                    {
                        stringBuffer.append(str);
                    }
                    in.close();
                    str=null;
                    return stringBuffer.toString();
                } catch (MalformedURLException e) {
                    throw e;
                } catch (IOException e) {
                    throw e;
                }

            } catch (Exception ex) {
                throw new Exception("Unable to get data from network. ["+ex.getMessage()+"]");
            }
        }

    }

}
