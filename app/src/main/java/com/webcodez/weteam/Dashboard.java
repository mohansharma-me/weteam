package com.webcodez.weteam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.webcodez.weteam.page.Members;
import com.webcodez.weteam.page.PanelFragment;
import com.webcodez.weteam.page.Profile;
import com.webcodez.weteam.page.UserStatus;
import com.webcodez.weteam.page.interfaces.ProfileInterface;


public class Dashboard extends ActionBarActivity {

    FrameLayout frmPage=null;
    LinearLayout frmLoader=null;

    ProfileInterface profileInterface=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        frmPage=(FrameLayout)findViewById(R.id.frmPage);
        frmLoader=(LinearLayout)findViewById(R.id.frmLoader);

        initInterfaces();

        startUp();

    }

    public void initInterfaces()
    {
        profileInterface=new ProfileInterface() {
            @Override
            public void reloadDashboard() {
                startUp();
            }
        };
    }

    public void startUp() {
        try {
            if(job.getMobileNumber()>0 && job.getOTPCode()>0) {

                boolean isNewUser=job.getUserData()==null && job.getUserToken()==null;

                if(isNewUser) {
                    setFragment(Profile.newInstance(profileInterface));
                    Toast.makeText(this,"NEW USER",Toast.LENGTH_SHORT).show();
                } else {

                    switch (job.getUserStatus()) {
                        case 0: //pending
                            setFragment(UserStatus.newInstance("Your Request Status Is Pending"));
                            break;
                        case 1: //verified
                            setFragment(PanelFragment.newInstance());
                            break;
                        case -1: //rejected
                            setFragment(UserStatus.newInstance("Your Request Is Rejected"));
                            break;
                    }

                }

            } else {
                Toast.makeText(this,"User session expired, please authenticate device.",Toast.LENGTH_SHORT).show();
                logout();
            }
        } catch (Exception ex) {
           Toast.makeText(this,"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void setFragment(Fragment fragment) {
        try {
            showLoader();
            getSupportFragmentManager().beginTransaction().replace(R.id.frmPage, fragment).commit();
            showPage();
        } catch (Exception ex) {
            Toast.makeText(this,"Error: "+ex,Toast.LENGTH_SHORT).show();
        }
    }

    public void showPage() {
        frmLoader.setVisibility(View.GONE);
        frmPage.setVisibility(View.VISIBLE);
    }

    public void showLoader() {
        frmLoader.setVisibility(View.VISIBLE);
        frmPage.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        try {
            if(job.getUserStatus()!=1) return false;
        } catch (Exception ex) {}
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        try {
            if(job.getUserStatus()!=1) return false;
        } catch (Exception ex) {}

        //noinspection SimplifiableIfStatement
        if(id==R.id.action_todayList) {
            setFragment(Members.newInstance("today.php"));
            return true;
        } else if(id==R.id.action_memberList) {
            setFragment(Members.newInstance());
            return true;
        } else if(id==R.id.action_dashboard) {
            setFragment(PanelFragment.newInstance());
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void logout() {
        try {
            job.logoutNow();
        } catch (Exception ex) {
            Toast.makeText(this,"Unable to logout. ["+ex.getMessage()+"]",Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(this,BootLoader.class));
        finish();
    }
}
