package com.pelhamcourt.newportwalkingtour;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Elisabeth Huhn on 12/19/2017.
 *
 * Serves as a liaison between a list RecyclerView and the SiteScript
 * Adapted from the list adapter which uses an ArrayList of objects
 *
 */

class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.MyViewHolder>{

    private MainActivity mActivity;

    //implement the ViewHolder as an inner class
    class MyViewHolder extends RecyclerView.ViewHolder {
        //The views in each row to be displayed
        TextView siteOrdinal, siteTitleName;


        MyViewHolder(View v) {
            super(v);

            siteOrdinal    = v.findViewById(R.id.siteOrdinalOutput);
            siteTitleName  = v.findViewById(R.id.siteTitleOutput);

        }

    } //end inner class MyViewHolder

    //Constructor for SiteAdapter
    SiteAdapter(MainActivity activity){
        this.mActivity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.list_row_site, parent,  false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        //get the row indicated
        SiteScript site = new SiteScript(mActivity, position);

        holder.siteOrdinal.setText(String.valueOf(site.getOrdinal()));
        holder.siteTitleName. setText(site.getTitle());

        holder.siteOrdinal.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
        holder.siteTitleName.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
    }

    @Override
    public int getItemCount(){
        return SiteScript.getRawSitesSize();
    }

}
