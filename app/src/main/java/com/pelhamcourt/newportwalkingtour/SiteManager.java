package com.pelhamcourt.newportwalkingtour;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Elisabeth Huhn on 12/15/2017.
 *
 * The class in charge of maintaining the set of instances of Site
 *  both in-memory and in the DB
 */

class SiteManager {
    // **********************************/
    // ******* Static Constants *********/
    // **********************************/
    private static final int SITE_NOT_FOUND = -1;


    // **********************************/
    // ******* Static Variables *********/
    // **********************************/
    private static SiteManager ourInstance ;

    // **********************************/
    // ******* Member Variables *********/
    // **********************************/
    private ArrayList<Site> mSiteList;


    // **********************************/
    // ******* Static Methods   *********/
    // **********************************/
    static SiteManager getInstance() {
        if (ourInstance == null){
            ourInstance = new SiteManager();
        }
        return ourInstance;
    }


    // **********************************/
    // ******* Constructors     *********/
    // **********************************/
    private SiteManager() {

        mSiteList = new ArrayList<>();

        //The DB isn't read until the first time a site is accessed

    }

    // **********************************/
    // ******* Setters/Getters  *********/
    // **********************************/


    // *****************************************/
    // ******* CRUD Methods            *********/
    // *****************************************/


    /// ****************  CREATE *******************************************

    //The routine that actually adds the instance to in memory list and
    // potentially (third boolean parameter) to the DB
    long addSite(Site newSite, boolean addToDBToo, boolean currentOnly){
        long returnCode = DatabaseManager.sDB_ERROR_CODE;

        //There may be more people in the DB than are in memory
        if ((mSiteList == null) || (mSiteList.size() == 0)){
            mSiteList = new ArrayList<>();
        }

        mSiteList.add(newSite);


        if (addToDBToo){

            DatabaseManager databaseManager = DatabaseManager.getInstance();
            returnCode = databaseManager.addSite(newSite);
            if (returnCode == DatabaseManager.sDB_ERROR_CODE)return returnCode;

        }
        return newSite.getSiteID();
    }



    /// ****************  READ *******************************************
    //return the cursor containing all the Concurrent Doses in the DB
    //that pertain to this siteID
    Cursor getAllSitesCursor (){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        return databaseManager.getAllSitesCursor();
    }


    //Return the list of all Sites
    ArrayList<Site> getSiteList() {
        //Assumption is that if any site is already in the list, it must be up to date
        if ((mSiteList == null) || (mSiteList.size() == 0)){
            //get the Sites from the DB
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            mSiteList = databaseManager.getAllSites();
        }
        return mSiteList;
    }


    //Return the site instance that matches the argument siteID
    //returns null if the site is not in the list or in the DB
    Site getSite(long siteID)  {
        if (siteID == Utilities.ID_DOES_NOT_EXIST)return null;
        //Assumption is that if any site is already in the list, it must be up to date
        if ((mSiteList == null) || (mSiteList.size() == 0)){
            //get all deleted people as well
            getSiteList();
        }
        int atPosition = getSitePosition(siteID);

        if (atPosition == SITE_NOT_FOUND) {

            //attempt to read the DB before giving up
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            Site site = databaseManager.getSite(siteID);
            if (site != null) {
                //if a matching site was in the DB, add it to RAM
                mSiteList.add(site);
                //Do not do the cascading get for the medications here

            }
            return site;
        }
        return (mSiteList.get(atPosition));
    }



    //returns the position of the site instance that matches the argument siteEmailAddr
    //returns constant = SITE_NOT_FOUND if the site is not in the list
    //NOTE it is a RunTimeException to call this routine if the list is null or empty
    private int getSitePosition(long siteID){
        Site site;
        int position = 0;
        int last     = mSiteList.size();

        //Determine whether an instance of the site is already in the list
        //NOTE that if list is empty, while doesn't loop even once
        while (position < last){
            site = mSiteList.get(position);

            if (site.getSiteID() == siteID){
                //Found the site in the list at this position
                return position;
            }
            position++;
        }
        return SITE_NOT_FOUND;
    }



    /// ****************  UPDATE *******************************************


    /// ****************  DELETE *******************************************






    // ******************************************/
    // ******* Private Member Methods   *********/
    // ******************************************/


    // ******************************************/
    // ******* Translation Utility Methods  *****/
    // ******************************************/

     //returns the ContentValues object needed to add/update the site to/in the DB
    ContentValues getCVFromSite(Site site){
        ContentValues values = new ContentValues();
        values.put(DataBaseSqlHelper.SITE_ID,           site.getSiteID());
        values.put(DataBaseSqlHelper.SITE_ORDINAL,      site.getOrdinal());
        values.put(DataBaseSqlHelper.SITE_NAME,         site.getName().toString());
        values.put(DataBaseSqlHelper.SITE_PREV_SITE_ID, site.getPrevSiteID());
        values.put(DataBaseSqlHelper.SITE_NEXT_SITE_ID, site.getNextSiteID());
        values.put(DataBaseSqlHelper.SITE_DIRECTIONS,   site.getDirections().toString());
        values.put(DataBaseSqlHelper.SITE_SHORT_DESC,   site.getShortDesc().toString());
        values.put(DataBaseSqlHelper.SITE_LONG_DESC,    site.getLongDesc().toString());

        int booleanValue = 0;
        if (site.isBonus())booleanValue = 1;
        values.put(DataBaseSqlHelper.SITE_BONUS,   booleanValue);

        return values;
    }


    //returns the Site characterized by the position within the Cursor
    //returns null if the position is larger than the size of the Cursor
    //NOTE    this routine does NOT add the site to the list maintained by this SiteManager
    //        The caller of this routine is responsible for that.
    //        This is only a translation utility
    //WARNING As the app is not multi-threaded, this routine is not synchronized.
    //        If the app becomes multi-threaded, this routine must be made thread safe
    //WARNING The cursor is NOT closed by this routine. It assumes the caller will close the
    //         cursor when it is done with it
    Site getSiteFromCursor(Cursor cursor, int position){

        int last = cursor.getCount();
        if (position >= last) return null;

        //filled with defaults, no ID assigned
        Site site = new Site();

        cursor.moveToPosition(position);
        site.setSiteID
                (cursor.getLong   (cursor.getColumnIndex(DataBaseSqlHelper.SITE_ID)));
        site.setOrdinal(
                (cursor.getInt    (cursor.getColumnIndex(DataBaseSqlHelper.SITE_ORDINAL))));
        site.setName(
                (cursor.getString (cursor.getColumnIndex(DataBaseSqlHelper.SITE_NAME))));
        site.setPrevSiteID(
                (cursor.getLong   (cursor.getColumnIndex(DataBaseSqlHelper.SITE_PREV_SITE_ID))));
        site.setNextSiteID(
                (cursor.getLong   (cursor.getColumnIndex(DataBaseSqlHelper.SITE_ID))));
        site.setDirections(
                (cursor.getString (cursor.getColumnIndex(DataBaseSqlHelper.SITE_NEXT_SITE_ID))));
        site.setLongDesc(
                (cursor.getString(cursor.getColumnIndex(DataBaseSqlHelper.SITE_LONG_DESC))));
        site.setShortDesc(
                (cursor.getString(cursor.getColumnIndex(DataBaseSqlHelper.SITE_SHORT_DESC))));

        int bonus = cursor.getInt(cursor.getColumnIndex(DataBaseSqlHelper.SITE_BONUS));
        boolean booleanValue = false;
        if (bonus == 1)booleanValue = true;
        site.setBonus(booleanValue);

        return site;
    }

}
