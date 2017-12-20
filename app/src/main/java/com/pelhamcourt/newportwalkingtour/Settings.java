package com.pelhamcourt.newportwalkingtour;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by elisabethhuhn on 12/15/17.
 *
 * This class contains application settings.
 * The values are stored in the shared preferences structure
 * This class contains the setters and getters for all such settings
 */

 class Settings {
    //************************************/
    /*    Static (class) Constants       */
    //************************************/


    private static final String sDefaultTimeDueTag   = "timeDue"; //in minutes since midnight
    private static final String sClock24FormatTag    = "clock24" ;

    private static final String sFabVisibleTag       = "fabVisible";
    private static final String sMinZoomTag          = "minZoomLevel";
    private static final String sForceZoomTag        = "forceZoom";
    private static final String sMarkerPaddingTag    = "markerPadding";


    //************************************/
    /*    Static (class) Variables       */
    //************************************/
    private static Settings ourInstance ;

    //************************************/
    /*    Member (instance) Variables    */
    //************************************/



    //************************************/
    /*         Static Methods            */
    //************************************/
    static Settings getInstance() {
        if (ourInstance == null){
            ourInstance = new Settings();
        }
        return ourInstance;
    }



    //************************************/
    /*         CONSTRUCTOR               */
    //************************************/
    private Settings() {
    }


    //************************************/
    /*    Member setter/getter Methods   */
    //************************************/


    //*********************************************************/
    //               Preferences setters and getters         //
    //*********************************************************/

    long getSiteID (MainActivity activity)  {
        return getLongSetting(activity, Site.sSiteIDTag, Utilities.ID_DOES_NOT_EXIST);
    }
    void setSiteID (MainActivity activity, long patientID){
        //Store the SiteID for the next time
        setLongSetting(activity, Site.sSiteIDTag, patientID);
    }

    boolean isClock24Format(MainActivity activity)  {
        return getBooleanSetting(activity, Settings.sClock24FormatTag, false);
    }
    void    setClock24Format(MainActivity activity, boolean is24Format){
        setBooleanSetting(activity, Settings.sClock24FormatTag, is24Format);
    }

    boolean isFabVisible(MainActivity activity)  {
        return getBooleanSetting(activity, Settings.sFabVisibleTag, true);
    }
    void    setFabVisible(MainActivity activity, boolean isFabVisible){
        setBooleanSetting(activity, Settings.sFabVisibleTag, isFabVisible);
    }


    // TODO: 12/17/2017 put these in the settings fragment UI
    int getMinZoomLevel(MainActivity activity){
        return getIntSetting(activity, sMinZoomTag, 8);
    }
    int getMarkerPadding(MainActivity activity){
        return getIntSetting(activity, sMarkerPaddingTag,  50);
    }
    int getForceZoomAfter(MainActivity activity){

        return getIntSetting(activity, sForceZoomTag,  2);
    }




    private int getIntSetting (MainActivity activity, String tag, int defaultValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(tag, defaultValue);
    }
    private void setIntSetting (MainActivity activity, String tag, int putValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(tag, putValue);
        editor.apply();
    }

    private long getLongSetting (MainActivity activity, String tag, long defaultValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getLong(tag, defaultValue);
    }
    private void setLongSetting (MainActivity activity, String tag, long putValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(tag, putValue);
        editor.apply();
    }

    private boolean getBooleanSetting (MainActivity activity, String tag, boolean defaultValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(tag, defaultValue);
    }
    private void setBooleanSetting (MainActivity activity, String tag, boolean putValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(tag, putValue);
        editor.apply();
    }


}
