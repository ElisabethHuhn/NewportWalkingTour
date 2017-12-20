package com.pelhamcourt.newportwalkingtour;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import java.util.ArrayList;

/**
 * The Collect Fragment is the UI
 * when the user is making point measurements in the field
 * Created by Elisabeth Huhn on 4/13/2016.
 */
public class NewportMapFragment extends Fragment {

    //DEFINE constants / literals

    private static final String TAG = "NewportMapFragment";

    private static final float markerColorBonus    = BitmapDescriptorFactory.HUE_ORANGE;
    private static final float markerColorFirst    = BitmapDescriptorFactory.HUE_RED;
    private static final float markerColorDefault  = BitmapDescriptorFactory.HUE_BLUE;

    private static final int lineWidth = 10;
    private static final int lineColor = Color.BLUE;

    private static final int newportZoom = 17;



    //* ****************************************/
    //* *     variables for processing      ****/
    //* ****************************************/

    //* *************************************************************/
    //* *     variables to be saved on configuration change      ****/
    //* *************************************************************/

    //Screen Focus
    private double mLatitudeScreenFocus;
    private double mLongitudeScreenFocus;


    //variables for the map
    //only used at very beginning of app to mark end of initialization
    //private boolean      isMapInitialized = false;
    //indicates whether map should be automatically resized when a point is added
    private boolean      isAutoResizeOn = true;


    //Map data that doesn't need to survive a configuration change

    //The map will need to be reinitialized after each reconfiguration change
    //initializeMap()
    private MapView      mMapView;
    private GoogleMap    mMap;

    private LatLngBounds.Builder mZoomBuilder;
    private LatLngBounds         mZoomBounds;


    //redrawLineBetweenMarkers() recreates these variables
    // and draws the marker line from points in mMarkers
    private Polyline mPointsLine;
    private PolylineOptions mLineOptions;


    //Markers that are actually on the map
    private ArrayList<Marker> mMarkers     = new ArrayList<>();
    //Markers that are within the zoom boundaries
    private ArrayList<Marker> mZoomMarkers = new ArrayList<>();




    //* *******************************************************************/
    /*                Constructor                                         */
    //* *******************************************************************/
    public NewportMapFragment() {
        //for now, we don't need to initialize anything when the fragment
        //  is first created
    }


    //* *******************************************************************/
    /*          Lifecycle Methods                                         */
    //* *******************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_newport_map, container, false);

        //update the view with the mapsView
       // addMapsView(v);
        //and then initialize it
        initializeMaps(savedInstanceState, v);

        //Wire up the other UI widgets so they can handle events later
        wireWidgets(v);

        //Set the titlebar subtitle
        setSubtitle();

        setRetainInstance(true);
        return v;
    }

    //Ask for location events to start
    @Override
    public void onStart(){
        if (mMapView != null){
            mMapView.onStart();
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        if (mMapView != null){
            mMapView.onResume();
        }

        super.onResume();

        setSubtitle();

     }

    //Ask for location events to stop
    @Override
    public void onPause() {

        if (mMapView != null){
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop(){
        if (mMapView != null){
            mMapView.onStop();
        }
        super.onStop();
    }


    @Override
    public void onDestroy(){
        try {
            if (mMapView != null){
                mMapView.onDestroy();
            }
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.destroy_error), e);
        }
        super.onDestroy();

    }

    @Override
    public void onLowMemory(){

        if (mMapView != null){
            mMapView.onLowMemory();
        }
        super.onLowMemory();

    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        if (mMapView != null){
            mMapView.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
        //saveState(outState);
    }


    //* *********************************************************//
    //* *********  Maps Callback and other Maps routines   ******//
    //* *********************************************************//

    private void initializeMaps(Bundle savedInstanceState, View v){

        // Gets the MapView from the XML layout and creates it
        mMapView = v.findViewById(R.id.map_view);
        //we need to be the one stepping the map through it's lifecycle, not the system
        //  so pass the creation event on to the map
        mMapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff

        //onMapReadyCallback is triggered when the map is ready to be used
        //                   is called when the map is ready for initialization
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                initializeMapsReady();
                //put the markers on the map
                initializeMarkers();
                centerMapOnNewport();
            }
        });
    }

    private void initializeMapsReady(){
        //current location control depends upon permissions
        boolean locEnabled = false;
        //This check doesn't do anything other than allow us to disable locationEnabled
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED) {

            locEnabled = true;

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(locEnabled);
        mMap.setMyLocationEnabled(locEnabled);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);


        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        int minZoomLevel = 3;
        //Make sure we don't zoom in too close
        mMap.setMinZoomPreference(minZoomLevel);


        //mMap.setInfoWindowAdapter(new InfoWindowAdapter((MainActivity)getActivity()));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //Handle the map touch event
                handleMapTouch(latLng);
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MainActivity activity = (MainActivity)getActivity();
                if (activity == null)return false;//false tells system to respond to event also

                SiteScript site = (SiteScript)marker.getTag();
                if (site == null)return false;

                String title     = site.getTitle();
                String shortDesc = site.getShortDesc();
                String longDesc  = site.getLongDesc();
                Utilities.getInstance().showMessageDialog(activity, title, shortDesc, longDesc);
                return true;//true suppresses default marker behavior,
            }
        });

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeMarkers(){

        //Step 0 clear any old markers and reset variables

        //step 2 For each site in the script
        //step 3 Create a Marker
        //Step 4 Add the site to the marker as a Tag
        //step 5 Add the marker to poly line, draw lines and do zooming or panning necessary

        //step 7 change screen focus to last point's location

        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        //Step 0 - clear any old markers

        mLineOptions  = null; //clear any old lines
        mZoomBounds   = null;
        mZoomBuilder  = null;

        isAutoResizeOn = true;
        //clear the map of old markers
        mMap.clear();
        mMarkers.clear();
        mZoomMarkers.clear();

        //step 2 For each site in the script
        int position = 0;
        int last     = SiteScript.getRawSitesSize();

        LatLng       markerLocation = null;
        SiteScript   site;
        Marker       lastMarkerAdded;
        float        markerColor;
        //position 0 is the introduction
        for (position = 1; position < last; position++){
            site = new SiteScript(activity, position);
            if (site.getLatitude() != Utilities.ID_DOES_NOT_EXIST) {

                //step 3 Create a Marker

                markerLocation = new LatLng(site.getLatitude(), site.getLongitude());

                String markerName = site.getTitle();

                markerColor = markerColorDefault;
                if (site.isBonus())markerColor = markerColorBonus;
                if (site.isStart())markerColor = markerColorFirst;
                lastMarkerAdded = makeNewMarker(markerLocation, markerName, markerColor);

                //Step 4 add the site to the marker as a tag
                lastMarkerAdded.setTag(site);

                //step 5 Add to poly line, draw lines and do zooming or panning necessary
                //But don't add Bonus sites to the line
                if (!site.isBonus()) {
                    addPointToLine(markerLocation);
                }
            }

        }
        //step 8 change screen focus to last point's location
        if (markerLocation != null) {
            setFocus(markerLocation);
            zoomTo(markerLocation);
        }


    }



    //* *************************************************************//
    //* *******   Other Initialization Methods   ********************//
    //* *************************************************************//
    private void setSubtitle() {
        ((MainActivity)getActivity()).setSubtitle(R.string.title_newport_map);
    }

    private void wireWidgets(View v){
/*
        //ZOOMIN Button
        ImageButton zoomInButton = (ImageButton) v.findViewById(R.id.zoomInButton);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Utilities.getInstance().showStatus(getActivity(),  R.string.zoom_in_button_label);

                updateCamera(CameraUpdateFactory.zoomIn());
                isAutoResizeOn = false;

            }
        });

        //ZOOM OUT Button
        ImageButton zoomOutButton = (ImageButton) v.findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Utilities.getInstance().showStatus(getActivity(),  R.string.zoom_out_button_label);

                updateCamera(CameraUpdateFactory.zoomOut());
                isAutoResizeOn = false;
            }
        });

        //ZOOM ext Button
        ImageButton zoomExtButton = (ImageButton) v.findViewById(R.id.zoomExtButton);
        zoomExtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Utilities.getInstance().showStatus(getActivity(),  R.string.zoom_ext_button_label);

                boolean doZoom = true;
                buildNewZoom(mZoomMarkers, doZoom);
                isAutoResizeOn = true;

            }
        });
*/


    }

    private void handleMapTouch(LatLng latLng){
        if (latLng == null) return;
        setFocus(latLng);

        double latitude  = latLng.latitude;
        double longitude = latLng.longitude;

        // TODO: 12/15/2017 get rid of this hard coding
        int locPrecision = 5;

        String msg = "Map touched at latitude = " +
                //getString(R.string.map_touched) +
                //" " + getString(R.string.latitude_label)+ " " +
                Utilities.truncatePrecisionString(latitude, locPrecision) +
                ", longitude = " +
                //", " + getString(R.string.longitude_label)+ " " +
                Utilities.truncatePrecisionString(longitude,locPrecision) ;

        Utilities.getInstance().showStatus(getActivity(), msg);

        isAutoResizeOn = false;
    }



    //* ****************************************************/
    //* ********      Map Utilities          ***************/
    //* ****************************************************/

    private void zoomToFit(){
        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;
        int markerPadding  = Settings.getInstance().getMarkerPadding(activity);
        int forceZoomAfter = Settings.getInstance().getForceZoomAfter(activity);

        //only zoom after the first couple of points have been plotted
        if (mMarkers.size() > forceZoomAfter) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mZoomBounds, markerPadding);
            updateCamera(cu);
        }

    }

    private Marker makeNewMarker(LatLng newPoint, String markerName, float markerColor){

        //update the maps
        MarkerOptions newPointMarkerOptions = new MarkerOptions().position(newPoint)
                                        .title(markerName)
                                        .draggable(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor));
        Marker newMarker = mMap.addMarker(newPointMarkerOptions);
        mMarkers.add(newMarker);
        mZoomMarkers.add(newMarker);

        return newMarker;
    }

    private void zoomTo(LatLng newPoint){

        //get zoom level from the mpa
        float mapZoom = mMap.getCameraPosition().zoom;

        CameraUpdate myZoom = CameraUpdateFactory.newLatLngZoom(newPoint, mapZoom);
        updateCamera(myZoom);

    }

    private void addPointToLine(LatLng newPoint){
        //add the new point to the line
        if (mLineOptions == null){
            mLineOptions = new PolylineOptions();
        }
        // TODO: 12/17/2017 make these attributes part of settings
        mLineOptions.add(newPoint).width(lineWidth).color(lineColor).geodesic(true);
        mPointsLine = mMap.addPolyline(mLineOptions);
        //The way to find out how many points we've processed so far
        //mPointsLine.getPoints().size();

        //now update the zoom boundary around the set of points
        if (mZoomBuilder == null){
            mZoomBuilder = new LatLngBounds.Builder();
        }

        mZoomBuilder = mZoomBuilder.include(newPoint);
        //update the zoom to fit bounds
        mZoomBounds = mZoomBuilder.build();


        if (isAutoResizeOn) {
            zoomToFit();
        }
    }


    //* ****************************************************/
    //* ********      Map Utilities          ***************/
    //* ****************************************************/
    private void centerMap(double latitude, double longitude, int zoomLevel){
        //update the maps
        LatLng newPoint = new LatLng(latitude, longitude);

        CameraUpdate myZoom = CameraUpdateFactory.newLatLngZoom(newPoint, zoomLevel);
        //CameraUpdate myZoom = CameraUpdateFactory.newLatLng(newPoint);
        updateCamera(myZoom);

    }

    private void centerMapOnNewport(){

        //Center the map on the Pelham Court Hotel - Stop 1
        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;

        //zoom to introduction location
        SiteScript script = new SiteScript(activity, 0);
        centerMap(script.getLatitude(), script.getLongitude(), newportZoom);

        //addTourStops();
    }

    private void updateCamera(CameraUpdate cu){
        mMap.animateCamera(cu);

    }

    private void addTourStops(){
        MainActivity activity = (MainActivity)getActivity();
        if (activity == null)return;
        try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.stops_on_tour, activity);
            layer.addLayerToMap();
        } catch (Exception e){
            Utilities.getInstance().errorHandler(activity, e.getMessage());
        }
    }

    //* ****************************************************/
    //* ********      Focus Utilities        ***************/
    //* ****************************************************/
    private void setFocus(LatLng latLng) {

        if (!(mLatitudeScreenFocus  == latLng.latitude)   ||
                !(mLongitudeScreenFocus == latLng.longitude)) {

            mLatitudeScreenFocus  = latLng.latitude;
            mLongitudeScreenFocus = latLng.longitude;

            //only actually change the focus if the auto resize is off
            if (isAutoResizeOn) {
                //update the maps zoom focus
                CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                updateCamera(center);
            }
        }
    }

}


