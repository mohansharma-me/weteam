package com.webcodez.weteam.page;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.webcodez.weteam.R;
import com.webcodez.weteam.job;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class PanelFragment extends Fragment {
    public static PanelFragment newInstance() {
        PanelFragment fragment = new PanelFragment();
        return fragment;
    }

    public PanelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View panelView= inflater.inflate(R.layout.fragment_panel, container, false);

        try {
            ((SwitchCompat)panelView.findViewById(R.id.sAlertMode)).setChecked(job.alertMode());
        } catch (Exception ex) {}

        ((SwitchCompat)panelView.findViewById(R.id.sAlertMode)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    boolean setTo=job.alertMode(isChecked,true);
                    if(setTo) {
                        Toast.makeText(getActivity(),"Alert mode turned on!!",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),"Alert mode turned off, you'll not receive any alerts!!",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(),"Error: "+ex,Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((Button)panelView.findViewById(R.id.btnAlertNow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    EditText txtAlertMessage=(EditText)panelView.findViewById(R.id.txtAlertMessage);

                    if(txtAlertMessage!=null) {
                        txtAlertMessage.setError(null);
                        final String message=txtAlertMessage.getText().toString().trim();

                        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        View linearLayout=inflater.inflate(R.layout.alert_loadingfriends, container, false);
                        builder.setView(linearLayout);
                        builder.setCancelable(false);
                        final AlertDialog alertDialog=builder.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String dialogTitle="";
                                String dialogMessage="";
                                try {

                                    String data=job.network.DownloadString("online_friends.php",new Hashtable<String, String>());

                                    final JSONObject json=new JSONObject(data);

                                    if(!json.isNull("dialogTitle") && !json.isNull("dialogMessage")) {
                                        dialogMessage=json.getString("dialogMessage");
                                        dialogTitle=json.getString("dialogTitle");
                                    }

                                    if(!json.isNull("success") && json.getBoolean("success")) {
                                        dialogTitle=dialogMessage="";
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                alertDialog.dismiss();
                                                sendAlerts(message, json);
                                            }
                                        });

                                    } else {
                                        if(dialogTitle.length()==0) {
                                            dialogTitle="Error";
                                            dialogMessage="Unable to find online friends, please try again.";
                                        }
                                    }

                                } catch (final Exception ex) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(),"Error: "+ex,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } finally {
                                    final String finalDialogTitle = dialogTitle;
                                    final String finalDialogMessage = dialogMessage;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                            if(finalDialogTitle.length()>0) {
                                                AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                                                builder1.setTitle(finalDialogTitle);
                                                builder1.setMessage(finalDialogMessage);
                                                builder1.show();
                                            }
                                        }
                                    });
                                }
                            }
                        }).start();

                    } else {
                        throw new Exception("Layout mismatched");
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(),"Error: "+ex,Toast.LENGTH_SHORT).show();
                }

            }
        });

        return panelView;
    }

    private void sendAlerts(final String message, final JSONObject json) {
        try {
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setTitle("Alert Confirmation");
            builder.setMessage("This alert action will send to following users:\n" + json.getString("friend_list") + "\n\nMessage:\n" + message);
            builder.setPositiveButton("Yes, Send >", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    try {
                        final String userArray = json.getJSONArray("friends").toString();

                        final ProgressDialog progressDialog=ProgressDialog.show(getActivity(),"Alerting...","Please wait while we send alert to your friends.",true,false);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                String dialogTitle="";
                                String dialogMessage="";

                                try {

                                    String data=job.network.DownloadString("send_alert.php",new Hashtable<String, String>() {
                                        {
                                            put("userJSON",userArray);
                                            put("message",message);
                                        }
                                    });

                                    JSONObject json=new JSONObject(data);

                                    if(!json.isNull("dialogTitle") && !json.isNull("dialogMessage")) {
                                        dialogTitle=json.getString("dialogTitle");
                                        dialogMessage=json.getString("dialogMessage");
                                    }

                                    if(!json.isNull("success") && json.getBoolean("success")) {

                                    } else {
                                        if(dialogTitle.length()==0) {
                                            dialogTitle="Error";
                                            dialogMessage="Unable to send alerts, please try again.";
                                        }
                                    }

                                } catch (final Exception ex) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(),"Error: "+ex,Toast.LENGTH_SHORT).show();
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
                                                AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                                                builder1.setTitle(finalDialogTitle);
                                                builder1.setMessage(finalDialogMessage);
                                                builder1.show();
                                            }
                                        }
                                    });
                                }

                            }
                        }).start();

                    } catch (Exception ex) {
                        Toast.makeText(getActivity(),"Error: "+ex,Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception ex) {
            Toast.makeText(getActivity(),"Error: "+ex,Toast.LENGTH_SHORT).show();
        }
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
