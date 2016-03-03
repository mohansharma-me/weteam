package com.webcodez.weteam.page;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webcodez.weteam.R;

public class UserStatus extends Fragment {

    public static UserStatus newInstance(String userStatusMessage) {
        UserStatus fragment = new UserStatus();
        fragment.userStatusMessage=userStatusMessage;
        return fragment;
    }

    private String userStatusMessage="";

    public UserStatus() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView= inflater.inflate(R.layout.fragment_pending_user_status, container, false);
        ((TextView)mainView.findViewById(R.id.lblRequestStatus)).setText(userStatusMessage);
        return mainView;
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
