package com.webcodez.weteam.page;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.webcodez.weteam.R;
import com.webcodez.weteam.job;
import com.webcodez.weteam.page.interfaces.ProfileInterface;

import org.json.JSONObject;

import java.util.Hashtable;

public class Profile extends Fragment {

    private ProfileInterface profileInterface;

    public static Profile newInstance(ProfileInterface profileInterface) {
        Profile fragment = new Profile();
        fragment.setProfileInterface(profileInterface);
        return fragment;
    }

    public Profile() {

    }

    public void setProfileInterface(ProfileInterface profileInterface) {
        this.profileInterface = profileInterface;
    }

    public ProfileInterface getProfileInterface() {
        return profileInterface;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView=inflater.inflate(R.layout.fragment_profile, container, false);

        ((Button)mainView.findViewById(R.id.btnSaveProfile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    EditText txtFirstName=(EditText)mainView.findViewById(R.id.txtFirstName);
                    EditText txtLastName=(EditText)mainView.findViewById(R.id.txtLastName);
                    EditText txtAddress=(EditText)mainView.findViewById(R.id.txtAddress);

                    if(txtFirstName!=null && txtLastName!=null && txtAddress!=null) {
                        txtFirstName.setError(null);
                        txtLastName.setError(null);
                        txtAddress.setError(null);

                        final String firstName=txtFirstName.getText().toString().trim();
                        final String lastName=txtLastName.getText().toString().trim();
                        final String address=txtAddress.getText().toString().trim();

                        if(firstName.length()==0) {
                            txtFirstName.setError("Please enter valid firstname.");
                        } else if(lastName.length()==0) {
                            txtLastName.setError("Please enter valid lastname.");
                        } else if(address.length()==0) {
                            txtAddress.setError("Please enter valid address.");
                        } else {
                            final ProgressDialog progressDialog=ProgressDialog.show(getActivity(),"Setting up...","Please wait while we put your information into server...",true,false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String dialogTitle="";
                                    String dialogMessage="";
                                    try {

                                        String data= job.network.DownloadString("save_profile.php",new Hashtable<String, String>() {
                                            {
                                                put("mobile",job.getMobileNumber()+"");
                                                put("otp",job.getOTPCode()+"");
                                                put("first_name",firstName);
                                                put("last_name",lastName);
                                                put("address",address);
                                            }
                                        });

                                        JSONObject json=new JSONObject(data);

                                        if(!json.isNull("dialogTitle") && !json.isNull("dialogMessage")) {
                                            dialogTitle=json.getString("dialogTitle");
                                            dialogMessage=json.getString("dialogMessage");
                                        }

                                        if(!json.isNull("success") && !json.isNull("token") && !json.isNull("userData") && json.getBoolean("success")) {
                                            job.setUserToken(json.getString("token"));
                                            job.setUserData(json.getString("userData"));
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    profileInterface.reloadDashboard();
                                                }
                                            });
                                        } else {
                                            if(dialogTitle.length()==0) {
                                                dialogTitle="Failed";
                                                dialogMessage="Unable to setup your profile, please try again.";
                                            }
                                        }

                                    } catch (final Exception ex) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } finally {
                                        final String finalDialogTitle = dialogTitle;
                                        final String finalDialogMessage = dialogMessage;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                if(finalDialogTitle.length()>0) {
                                                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                                    builder.setTitle(finalDialogTitle);
                                                    builder.setMessage(finalDialogMessage);
                                                    builder.show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }).start();

                        }

                    } else {
                        throw new Exception("Layout mismatched.");
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(),"Error: "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });

        return mainView;
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
