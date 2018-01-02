package com.pelhamcourt.newportwalkingtour;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //DEFINE constants / literals
    static final int MY_PERMISSIONS_REQUEST_COURSE_LOCATIONS = 1;
    static final int MY_PERMISSIONS_REQUEST_FINE_LOCATIONS = 2;

    static final String sMapTag       = "MAP";
    static final String sSettingsTag  = "SETTINGS";
    static final String sSiteListTag  = "SITE_LIST";


    //local variables
    private long mSiteID = Utilities.ID_DOES_NOT_EXIST;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeFAB();
        initializeGps();
        initializeFragment();

        setSubtitle(R.string.title_splash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        if (id == R.id.action_settings) {
            switchToSettingsScreen();
            return true;
        } else */if (id == R.id.action_sites) {
            switchToSiteListScreen();
            return true;
        } else if (id == R.id.action_map) {
            switchToNewportMapScreen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //**************************************************************/
    //********** Methods dealing with the Layout       *************/
    //**************************************************************/
    public void setSubtitle(int subtitle){

        //Put the name of the fragment on the title bar

        if (getSupportActionBar() != null){
            getSupportActionBar().setSubtitle(subtitle);
        }


    }


    //**************************************************************/
    //********** Methods dealing with the FAB          *************/
    //**************************************************************/
    private void initializeFAB(){
        final MainActivity activity = this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Settings.getInstance().isFabVisible(activity)) {
                    handleFAB(view);
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Settings.getInstance().setFabVisible(activity,false);
                hideFAB();
                return true;
            }
        });
        handleFabVisibility();
    }

    void handleFabVisibility(){
        if (Settings.getInstance().isFabVisible(this)){
            showFAB();
        } else {
            hideFAB();
        }
    }
    private void handleFAB(View view){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if ( fragment instanceof SplashFragment)  {
            hideFAB();

        }  else {
            //do nothing
            Snackbar.make(view, getString(R.string.add_nothing), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
    }

    public void showFAB(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }

    public void hideFAB(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
    }

    //* *********************************************************************/
    //* ********    Location Methods & Callbacks ****************************/
    //* *********************************************************************/

    private void initializeGps() {

        // TODO: 12/16/2017 Don't really need location, so clean up this request

        //make sure we have GPS permissions
        //check for permission to continue
        int permissionCheckCourse = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheckFine = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If we don't currently have permission, we have to ask for it
        if (permissionCheckCourse != PackageManager.PERMISSION_GRANTED){
            //find out if we need to explain to the user why we need GPS
/*
 if (
 //shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
 false){
 //
 //tell the user why GPS is required
 // Show an expanation to the user *asynchronously* -- don't block
 // this thread waiting for the user's response! After the user
 // sees the explanation, try again to request the permission.
 } else {
*/
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.MY_PERMISSIONS_REQUEST_COURSE_LOCATIONS);

            // MY_PERMISSIONS_REQUEST_COURSE_LOCATIONS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            //}
        }

        //If we don't currently have permission, we have to ask for it
        if (permissionCheckFine != PackageManager.PERMISSION_GRANTED) {
            //find out if we need to explain to the user why we need GPS
/*
 if (false) {
 //shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){


 //tell the user why GPS is required
 // Show an expanation to the user *asynchronously* -- don't block
 // this thread waiting for the user's response! After the user
 // sees the explanation, try again to request the permission.
 } else {
 *******/
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATIONS);

            // MY_PERMISSIONS_REQUEST_FINE_LOCATIONS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

            //}
            //So now signup for the GpsStatus.NmeaListener

        }
    }

    //Callbacks for permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COURSE_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                // TODO: 9/5/2016 Build in this functionality
/*
 if (grantResults.length > 0
 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

 // permission was granted, yay! Do the
 // contacts-related task you need to do.


 } else {

 // permission denied, boo! Disable the
 // functionality that depends on this permission.
 }
 *****/
            }
            case MY_PERMISSIONS_REQUEST_FINE_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                // TODO: 9/5/2016  fill this in
/*
 if (grantResults.length > 0
 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

 // permission was granted, yay! Do the
 // contacts-related task you need to do.


 } else {

 // permission denied, boo! Disable the
 // functionality that depends on this permission.
 }
 ********/
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    //**************************************************************/
    //********** Methods dealing with Fragments        *************/
    //**************************************************************/
    private void initializeFragment() {

        //Set the fragment to Home screen
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            //when we first create the activity, the fragment needs to be the home screen
            fragment = new SplashFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }



    //**************************************************************/
    //********** Methods for switching Fragments       *************/
    //**************************************************************/
    private void clearBackStack(){
        //Need the Fragment Manager to do the swap for us
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        //clear the back stack

        while (fm.getBackStackEntryCount() > 0){
            fm.popBackStackImmediate();
        }
    }

    //***** Routine to actually switch the screens *******/
    private void switchScreen(Fragment fragment, String tag) {
        //clear the back stack
        clearBackStack();

        //Need the Fragment Manager to do the swap for us
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        //Are any fragments already being displayed?
        Fragment oldFragment = fm.findFragmentById(R.id.fragment_container);

        if (oldFragment == null) {
            //It shouldn't ever be the case that we got this far with no fragments on the screen,
            // but code defensively. Who knows how the app will evolve
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, tag)
                    .commit();
        } else {
            fm.beginTransaction()
                    //replace whatever is being displayed with the Home fragment
                    .replace(R.id.fragment_container, fragment, tag)
                    //and add the transaction to the back stack
                    .addToBackStack(tag)
                    .commit();
        }
    }


    /****
     * Method to switch fragment to Newport Map screen
     * EMH 12/16/2017
     */
    public void switchToNewportMapScreen(){
        //replace the fragment with the Home UI

        Fragment fragment    = new NewportMapFragment();
        String   tag         = sMapTag;

        switchScreen(fragment, tag);

    }


    /****
     * Method to switch fragment to home screen
     * EMH 12/19/2017
     */
    public void switchToSettingsScreen(){
        //replace the fragment with the Home UI

        Fragment fragment    = new SettingsFragment();
        String   tag         = sSettingsTag;

        switchScreen(fragment, tag);
    }

    /****
     * Method to switch fragment to List Sites screen
     * EMH 12/19/17
     */

    public void switchToSiteListScreen(){
        //replace the fragment with the list of persons already defined

        Fragment fragment    = new SiteListFragment();
        String   tag         = sSiteListTag;

        switchScreen(fragment, tag);

    }



}



