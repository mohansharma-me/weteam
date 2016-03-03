package com.webcodez.weteam.extra;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.webcodez.weteam.R;

import java.util.List;

/**
 * Created by iAmMegamohan on 19-09-2015.
 */
public class MyListAdapter implements ListAdapter {

    public final static int IMAGE_COLLEGES=0;
    public final static int IMAGE_TIMELINE=1;

    private Context context;
    private AdapterFor adapterFor=AdapterFor.NONE;
    private List<?> dataObjects=null;
    private int itemLayout;

    public enum AdapterFor {
        MEMBERS,
        TODAY,
        NONE
    }

    public MyListAdapter(Context context, int itemLayout) {
        this.context=context;
        this.itemLayout=itemLayout;
    }

    public void setAdapterFor(AdapterFor adapterFor, List<?> dataObjects) {
        this.adapterFor = adapterFor;
        this.dataObjects=dataObjects;
    }

    public List<?> getDataObjects() {
        return dataObjects;
    }

    public AdapterFor getAdapterFor() {
        return adapterFor;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        if(dataObjects!=null)
            return dataObjects.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(dataObjects!=null)
            return dataObjects.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(itemLayout, parent, false);


        if(adapterFor==AdapterFor.MEMBERS)
            return initMember(row, position);


        return row;
    }

    private View initMember(final View row, int position) {
        Member member=(Member)getItem(position);
        if(member==null) return null;

        ((TextView)row.findViewById(R.id.lblName)).setText(member.Name);
        ((TextView)row.findViewById(R.id.lblMobileAddress)).setText(member.Mobile+" - "+member.Address);

        return row;
    }



    @Override
    public int getItemViewType(int position) {
        return ListAdapter.IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        if(dataObjects!=null)
            return dataObjects.size()==0;
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
