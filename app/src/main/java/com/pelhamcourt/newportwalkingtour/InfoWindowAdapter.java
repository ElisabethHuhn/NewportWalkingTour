package com.pelhamcourt.newportwalkingtour;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


/**
 * Created by Elisabeth Huhn on 12/16/2017.
 *
 * Flesh this out if we ever need custom info windows with our markers
 */

class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View mInfoWindowView;
    private MainActivity mActivity;
    private int        mPrecisionDigits;

    InfoWindowAdapter(MainActivity activity){
        mActivity = activity;

        //Get an inflater
        LayoutInflater layoutInflater =  LayoutInflater.from(activity);
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        //mInfoWindowView = layoutInflater.inflate(R.layout.info_window, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        CharSequence msg;
        TextView iwTextField;

        return mInfoWindowView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Write method if ever needed
        return null;
    }


}
