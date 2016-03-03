package com.webcodez.weteam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webcodez.weteam.service.WeTeamService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;


public class BootLoader extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_boot_loader);

        try {
            if(job.init(this)) {
                try {
                    startService(new Intent(getBaseContext(), WeTeamService.class));
                } catch (Exception ex) {ex.printStackTrace();}

                try {
                    int currentVersionCode=job.getInt("versionCode",-1);
                    if(currentVersionCode>-1) {
                        PackageManager manager = getPackageManager();
                        PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                        if(info.versionCode==currentVersionCode) {
                            job.setBoolean("forceUpdate",false);
                            job.removeKey("versionCode");
                            Log.v("SERVICE","ForceUpdate completed, Version Code:"+info.versionCode);
                        } else {
                            Log.v("SERVICE","ForceUpdate isn't completed, Version Code:"+info.versionCode);
                        }
                    }
                } catch (Exception ex) { Toast.makeText(this,"Error: "+ex,Toast.LENGTH_SHORT).show(); }

                if(job.getBoolean("forceUpdate",false)) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("Update Required");
                    builder.setMessage(job.getString("forceUpdateMessage", "A newer version of application is available, please update now."));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.webcodez.weteam")));
                            finish();
                        }
                    });
                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    try {
                        job.setBoolean("forceUpdate",false);
                    } catch (Exception ex) {}
                    startUp();
                }

            }
        } catch (Exception ex) {
            Toast.makeText(this,"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void startUp() {
        try {
            if(job.isLogged()) { // panel
                startActivity(new Intent(this,Dashboard.class));
                finish();
            } else { // login-side
                showLoginScreen();
            }
        } catch(Exception ex) {
            Toast.makeText(this,"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void showLoginScreen() {
        setContentView(R.layout.layout_login);
        try {
            if(job.getMobileNumber()>0) {
                ((TextView)findViewById(R.id.lblTitle)).setText("Verify WeTeam OTP");
                ((TextView)findViewById(R.id.lblSubtitle)).setText("Please confirm your received one-time password here.");
                ((LinearLayout)findViewById(R.id.llAskMobileNumber)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.llOTPCode)).setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Toast.makeText(this,"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    ////  write code for main/profile layout

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void resendOTP(final View view) {
        ((TextView)findViewById(R.id.lblTitle)).setText("Your Phone Number");
        ((TextView)findViewById(R.id.lblSubtitle)).setText("Please confirm your mobile number via OTP.");
        ((LinearLayout)findViewById(R.id.llAskMobileNumber)).setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.llOTPCode)).setVisibility(View.GONE);
    }

    public void verifyOTP(final View view) {
        try {

            final EditText txtOTPCode=(EditText)findViewById(R.id.txtOTPCode);

            if(txtOTPCode!=null) {
                final String otpCode=txtOTPCode.getText().toString().trim();
                //txtOTPCode.setText(null);

                if(otpCode.length()!=6) {
                    txtOTPCode.setError("Please enter valid OTP code...");
                    txtOTPCode.requestFocus();
                } else {
                    final ProgressDialog progressDialog=ProgressDialog.show(view.getContext(),"Verifying...","Please wait while we connect to our server and verify your one-time password.",true,false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String dialogTitle="";
                            String dialogMessage="";

                            try {

                                String result=job.network.DownloadString("verify_otp.php", new Hashtable<String, String>() {
                                    {
                                        put("mobile",job.getMobileNumber()+"");
                                        put("otp",otpCode);
                                    }
                                });

                                JSONObject json=new JSONObject(result);
                                if(!json.isNull("dialogTitle") && !json.isNull("dialogMessage")) {
                                    dialogTitle=json.getString("dialogTitle");
                                    dialogMessage=json.getString("dialogMessage");
                                }

                                if(!json.isNull("success") && json.getBoolean("success") && !json.isNull("new_user")) {
                                    try {
                                        job.setOTPCode(Integer.parseInt(otpCode));
                                    } catch (Exception ex) {}
                                    boolean isNewUser=json.getBoolean("new_user");
                                    String userData=null;
                                    job.setUserData(null);
                                    job.setUserToken(null);
                                    if(!isNewUser && !json.isNull("userData") && !json.isNull("token")) {
                                        userData=json.getString("userData");
                                        String token=json.getString("token");
                                        job.setUserToken(token);
                                        job.setUserData(userData);
                                    }
                                    job.setLogged(true);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startUp();
                                        }
                                    });
                                }

                            } catch (final Exception ex) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(view.getContext(),"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } finally {
                                final String finalDialogTitle = dialogTitle;
                                final String finalDialogMessage = dialogMessage;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            progressDialog.dismiss();
                                            if(finalDialogTitle.length()>0) {
                                                AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
                                                builder.setTitle(finalDialogTitle);
                                                builder.setMessage(finalDialogMessage);
                                                builder.show();
                                            }
                                        } catch (Exception ex) {}
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }

        } catch (Exception ex) {
            Toast.makeText(this,"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void validatePhoneNumber(final View view) {
        try {
            final LinearLayout llAskMobileNumber=(LinearLayout)findViewById(R.id.llAskMobileNumber);
            final LinearLayout llOTPCode=(LinearLayout)findViewById(R.id.llOTPCode);
            EditText txtMobileNumber=(EditText)findViewById(R.id.txtMobileNumber);
            final EditText txtOTPCode=(EditText)findViewById(R.id.txtOTPCode);

            final TextView lblTitle=(TextView)findViewById(R.id.lblTitle);
            final TextView lblSubTitle=(TextView)findViewById(R.id.lblSubtitle);

            final Button btnVerify=(Button)findViewById(R.id.btnVerify);

            if(txtMobileNumber!=null && llAskMobileNumber!=null && txtOTPCode!=null) {
                txtMobileNumber.setError(null);
                final String mobileNumber=txtMobileNumber.getText().toString().trim();
                if(mobileNumber.length()==0) {
                    txtMobileNumber.setError("Invalid Mobile Number");
                } else if(mobileNumber.length()<10) {
                    txtMobileNumber.setError("Mobile Number should be at least of length 10.");
                } else {

                    final ProgressDialog progressDialog=ProgressDialog.show(this,"Sending OTP", "Please wait while we send one time password on your mobile.", true, false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String title="";
                            String message="";
                            try {

                                String downloadedData=job.network.DownloadString("send_otp.php",new Hashtable<String, String>() {
                                    {
                                        put("mobile",mobileNumber);
                                    }
                                });

                                JSONObject jsonObject=new JSONObject(downloadedData);

                                if(!jsonObject.isNull("dialogTitle") && !jsonObject.isNull("dialogMessage")) {
                                    title=jsonObject.getString("dialogTitle");
                                    message=jsonObject.getString("dialogMessage");
                                }

                                if(jsonObject.isNull("success")) {
                                    if(title.length()==0) {
                                        title = "Error";
                                        message = "Unable to send one-time password, please try again.";
                                    }
                                } else {
                                    if(jsonObject.getBoolean("success")) {
                                        job.setOTPCode(-1);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    job.setMobileNumber(Long.parseLong(mobileNumber));
                                                    llAskMobileNumber.setVisibility(View.GONE);
                                                    lblTitle.setText("Verify OTP Code");
                                                    lblSubTitle.setText("Please wait while you got one-time password.");
                                                    llOTPCode.setVisibility(View.VISIBLE);

                                                    // looking for OTP change into sharedArea for one-minute
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                int waitMinutes = 3;
                                                                int seconds = 0;
                                                                int code = -1;
                                                                while (seconds < waitMinutes * 60) {
                                                                    code = job.getOTPCode();
                                                                    if (code != -1) {
                                                                        final int finalCode = code;
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                try {
                                                                                    if(job.getBoolean("autoOTP",false)) {
                                                                                        lblSubTitle.setText("Please wait we proceed you to next");
                                                                                        txtOTPCode.setText(finalCode + "");
                                                                                        verifyOTP(btnVerify);
                                                                                    }
                                                                                } catch (Exception ex) { ex.printStackTrace(); }
                                                                            }
                                                                        });
                                                                        break;
                                                                    }
                                                                    seconds++;
                                                                    final int finalSeconds = seconds;
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            lblSubTitle.setText("Please wait 3 minutes while you got one-time password.\n\nSeconds: " + finalSeconds);
                                                                        }
                                                                    });
                                                                    Thread.sleep(1000);
                                                                }
                                                                if (code == -1) {
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(view.getContext(), "Please re-send otp, we've waited for 1 minute.", Toast.LENGTH_SHORT).show();
                                                                            lblSubTitle.setText("Please re-send otp, we've waited for 3 minute.");
                                                                        }
                                                                    });
                                                                }
                                                            } catch (Exception ex) {
                                                                ex.printStackTrace();
                                                            }
                                                        }
                                                    }).start();
                                                } catch (Exception ex) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(view.getContext(),"Error: unable to set sms listener.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }

                            } catch (Exception ex) {
                                Toast.makeText(view.getContext(),"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                            } finally {
                                final String finalMessage = message;
                                final String finalTitle = title;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        if(finalTitle.length()>0 && finalMessage.length()>0) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                            builder.setTitle(finalTitle);
                                            builder.setMessage(finalMessage);
                                            builder.setPositiveButton("Okay",new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                }
            } else {
                Toast.makeText(this,"Error: Unexpected error occurred.",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this,"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_boot_loader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
