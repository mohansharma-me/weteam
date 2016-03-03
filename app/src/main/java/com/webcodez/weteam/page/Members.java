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
import android.widget.ListView;
import android.widget.Toast;

import com.webcodez.weteam.R;
import com.webcodez.weteam.extra.Member;
import com.webcodez.weteam.extra.MyListAdapter;
import com.webcodez.weteam.job;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Members extends Fragment {

    private String script_name="members.php";

    public static Members newInstance() {
        return newInstance("members.php");
    }

    public static Members newInstance(String script) {
        Members fragment = new Members();
        fragment.script_name=script;
        return fragment;
    }

    public Members()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_members, container, false);
        final List<Member> memberList=new ArrayList<Member>();
        final ProgressDialog progressDialog=ProgressDialog.show(getActivity(),"Loading Members","Please wait while we fetch member list from server.",true,false);
        new Thread(new Runnable() {
            @Override
            public void run() {

                String dialogTitle="";
                String dialogMessage="";

                try {

                    String data= job.network.DownloadString(script_name,new Hashtable<String, String>());
                    JSONObject json=new JSONObject(data);

                    if(!json.isNull("dialogTitle") && !json.isNull("dialogMessage")) {
                        dialogTitle=json.getString("dialogTitle");
                        dialogMessage=json.getString("dialogMessage");
                    }

                    if(!json.isNull("success") && !json.isNull("members") && json.getBoolean("success")) {

                        JSONArray array=json.getJSONArray("members");

                        for(int i=0;i<array.length();i++) {
                            try {
                                JSONObject jsonMember=array.getJSONObject(i);
                                if(!jsonMember.isNull("name") && !jsonMember.isNull("mobile")) {
                                    Member member = new Member();
                                    member.Name=jsonMember.getString("name");
                                    member.Mobile=jsonMember.getString("mobile");
                                    if(!jsonMember.isNull("address")) {
                                        member.Address=jsonMember.getString("address");
                                    } else {
                                        member.Address="";
                                    }
                                    memberList.add(member);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        dialogTitle=dialogMessage="";

                        final int arrayCount=array.length();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(arrayCount==0) {
                                    Toast.makeText(getActivity(),"No members found!!",Toast.LENGTH_SHORT).show();
                                } else {
                                    ListView lvMembers=(ListView)view.findViewById(R.id.lvMembers);
                                    MyListAdapter myListAdapter=new MyListAdapter(getActivity(),R.layout.listview_member);
                                    myListAdapter.setAdapterFor(MyListAdapter.AdapterFor.MEMBERS,memberList);
                                    lvMembers.setAdapter(myListAdapter);
                                }
                            }
                        });

                    } else {
                        if(dialogTitle.length()==0) {
                            dialogTitle="Error";
                            dialogMessage="Unable to fetch member list from server, please try again";
                        }
                    }

                } catch (final Exception ex) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Error: "+ex,Toast.LENGTH_LONG).show();
                        }
                    });
                } finally {

                        final String finalDialogTitle = dialogTitle;
                        final String finalDialogMessage = dialogMessage;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    progressDialog.dismiss();
                                    if(finalDialogTitle.length()>0) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle(finalDialogTitle);
                                        builder.setMessage(finalDialogMessage);
                                        builder.show();
                                    }
                                }catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                }

            }
        }).start();
        return view;
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
