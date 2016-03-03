package com.webcodez.weteam.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.webcodez.weteam.AlertActivity;
import com.webcodez.weteam.BootLoader;
import com.webcodez.weteam.job;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;

public class WeTeamService extends Service {

    public boolean allowThread=true;

    public static boolean isRunning=false;

    public WeTeamService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isRunning) {
            Log.v("SERVICE","Service Already Running!!");
            return START_NOT_STICKY;
        }
        Log.v("SERVICE","Service Started!!");
        //Toast.makeText(this,"WeTeam Service Started",Toast.LENGTH_SHORT).show();
        isRunning=true;
        final Intent alertIntent=new Intent(this, AlertActivity.class);
        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(true) {

            try {
                job.setServiceRunning(true);
            }catch (Exception ex) {
                Log.v("SERVICE","ServiceException:"+ex);
            }

            PackageManager manager = getPackageManager();
            PackageInfo info = null;
            try {
                info = manager.getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            final int currentVersionCode=info==null?0:info.versionCode;

            final int finalCurrentVersionCode=currentVersionCode;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v("SERVICE","Started!! ("+allowThread+")");
                    while(allowThread) {
                        try {
                            if(job.isLogged()) {

                                String serviceData = job.network.DownloadString("service.weteam.php",new Hashtable<String, String>() {
                                    {
                                        put("updateTime",job.alertMode()?"true":"false");
                                        put("lastAlert",job.getLastAlert()+"");
                                        put("appVersionCode",finalCurrentVersionCode+"");
                                    }
                                });

                                JSONObject json=new JSONObject(serviceData);

                                if(!json.isNull("success") && json.getBoolean("success")) {

                                    // Force Update
                                    try {

                                        if(!json.isNull("forceUpdate")) {
                                            boolean forceUpdate=json.getBoolean("forceUpdate");
                                            int versionCode=json.getInt("versionCode");
                                            Log.v("SERVICE","Update:"+forceUpdate+", Version Code:"+versionCode);
                                            job.setBoolean("forceUpdate",forceUpdate);
                                            job.setInt("versionCode",versionCode);
                                            if(forceUpdate)
                                            job.alertMode(!forceUpdate,true);
                                            if(!json.isNull("forceUpdateMessage")) {
                                                job.setString("forceUpdateMessage",json.getString("forceUpdateMessage"));
                                            } else {
                                                job.setString("forceUpdateMessage","A newer version of application is available, please update now.");
                                            }
                                        } else {
                                            Log.v("SERVICE","Force update skiped");
                                        }

                                    } catch (Exception ex) {}

                                    // UserStatus update
                                    try {
                                        if (!json.isNull("userStatus")) {
                                            job.setUserStatus(json.getInt("userStatus"));
                                        }
                                    } catch (Exception ex) { Log.v("SERVICE","UserStatusException::"+ex); }

                                    // Alert update
                                    try {
                                        if (job.alertMode() && !json.isNull("alert")) {
                                            JSONObject alert=json.getJSONObject("alert");
                                            job.setLastAlert(alert.getLong("alertId"));
                                            alertIntent.putExtra("alertJson",alert.toString());
                                            startActivity(alertIntent);
                                        }
                                    } catch (Exception ex) { Log.v("SERVICE","AlertUpdateException::"+ex); }
                                }
                            }
                        } catch (Exception ex) {ex.printStackTrace();}

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.v("SERVICE","Thread Ended!! ("+allowThread+")");
                }
            }).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allowThread=false;
        isRunning=false;
        try {
            job.setServiceRunning(false);
        }catch (Exception ex) {
            Log.v("SERVICE","ServiceException:"+ex);
        }
        //Toast.makeText(this,"WeTeam Service Stopped",Toast.LENGTH_SHORT).show();
        Log.v("SERVICE","Service Ended!!");
    }
}
