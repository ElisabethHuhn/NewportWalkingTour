package com.pelhamcourt.newportwalkingtour;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by Elisabeth Huhn on 5/18/2016.
 * This manager hides the CRUD routines of the DB.
 * Originally this is a pass through layer, but if background threads are required to get
 * IO off the UI thread, this manager will maintain them.
 * This manager is a singleton that holds the one connection to the DB for the app.
 * This connection is opened when the app is first initialized, and never closed
 */
class DatabaseManager {

    private static final String TAG = "DatabaseManager";
    static final long   sDB_ERROR_CODE = -1;


    //***********************************************/
    /*         static variables                     */
    //***********************************************/

    private static DatabaseManager  sManagerInstance ;

    private static String sNoContextException = "Can not create database without a context";
    private static String sNotInitializedException =
            "Attempt to access the database before it has been initialized";


    //***********************************************/
    /*         Instance variables                   */
    //***********************************************/
    private DataBaseSqlHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;


    //***********************************************/
    /*         static methods                       */
    //***********************************************/


    /*********************
     * This method initializes the singleton Database Manager
     *
     * The database Manager holds onto a single instance of the helper connection
     *    to the database.
     *
     * The purpose of a singleton connection is to keep the app threadsafe
     *    in the case of attempted concurrent access to the database.
     *
     * There can be no concurrent access to the database from multiple threads,
     *    as there is only one connection, it can be accessed serially,
     *    from only one thread at a time.
     *
     * Thie lifetime of this singleton is the execution lifetime of the App,
     *    thus the application context is passed, not the activity context
     *
     * synchronized method to ensure only 1 instance exists
     *
     * @param context               The application context
     * @throws RuntimeException     Thrown if there is no context passed
     *
     * USAGE
     * DatabaseManager.initializeInstance(getApplicationContext());
     */
    private static synchronized DatabaseManager initializeInstance(Context context) throws RuntimeException {
         String sNoContextException = "Can not create database without a context";
        if (sManagerInstance == null){
            try {
                //create the singleton Database Manager
                sManagerInstance = new DatabaseManager();

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        if (sManagerInstance.getDatabaseHelper() == null){

            //Note the hard coded strings here.
            // todo: figure out how to access the string resources from the DatabaseManager without a context
            if (context == null) throw new RuntimeException(sNoContextException);

            try{
                //all the constructor does is save the context
                sManagerInstance.setDatabaseHelper( new DataBaseSqlHelper(context));
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        if (sManagerInstance.getDatabase() == null){

            if (context == null) throw new RuntimeException(sNoContextException);

            try{
                sManagerInstance.setDatabase(sManagerInstance.getDatabaseHelper().getWritableDatabase());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        return sManagerInstance;
    }


    //returns null if the Database Manager has not yet bee initialized
    //in that case, initializeInstance() must be called first
    // We can't just fix the problem, as we need the application context
    //because this is an error condition. Treat it as an error

    static synchronized DatabaseManager getInstance() throws RuntimeException {
        //The reason we can't just initialize it now is because we need a context to initialize
        if (sManagerInstance == null)  {
            throw new RuntimeException(sNotInitializedException);
        }
        return sManagerInstance;
    }


    //But if we do happen to have a context, we can initialize
    static synchronized DatabaseManager getInstance(Context context) throws RuntimeException {
        //The reason we can't just initialize it now is because we need a context to initialize
        if (sManagerInstance == null)  {
            if (context == null) throw new RuntimeException(sNotInitializedException);
            DatabaseManager.initializeInstance(context);
        }
        return sManagerInstance;
    }


    //***********************************************/
    /*         constructor                          */
    //***********************************************/

    //null constructor. It should never be called. But you have to have one
    //    initializeInstance() is the proper protocol
    private DatabaseManager() {}



    //***********************************************/
    /*         setters & getters                    */
    //***********************************************/

    //mDatabaseHelper
    private void setDatabaseHelper(DataBaseSqlHelper mDatabaseHelper) {
        this.mDatabaseHelper = mDatabaseHelper;
    }
    //Return null if the field has not yet been initialized
    private synchronized DataBaseSqlHelper getDatabaseHelper()  {
        return mDatabaseHelper;
    }


    //mDatabase
    void   setDatabase(SQLiteDatabase mDatabase) {this.mDatabase = mDatabase; }
    //return null if the field has not yet been initialized
    synchronized SQLiteDatabase getDatabase()       { return mDatabase; }


    //***********************************************/
    /*         Instance methods                     */
    //***********************************************/
    //The CRUD routines:


    //***********************************************/
    /*        Site CRUD methods                   */
    //***********************************************/

    ///*****************************    COUNT    ***********************
    //Get count of sites
    //int getSiteCount() {}

    ///*****************************    Create    ***********************
    long addSite(Site site){

        long returnCode = sDB_ERROR_CODE;
        SiteManager siteManager = SiteManager.getInstance();
        returnCode =  mDatabaseHelper.add(mDatabase,
                                         DataBaseSqlHelper.TABLE_SITE,
                                         siteManager.getCVFromSite(site),
                                         getSiteWhereClause(site.getSiteID()),
                                         DataBaseSqlHelper.SITE_ID);
        if (returnCode == sDB_ERROR_CODE)return returnCode;
        site.setSiteID(returnCode);
        return returnCode;
    }


    ///**********************  Read **********************************

    Cursor getAllSitesCursor(){

        return mDatabaseHelper.getObject(  mDatabase,
                                        DataBaseSqlHelper.TABLE_SITE,
                                        null,    //get the whole object
                                        null,
                                        null, null, null, null);

    }


    //Reads the Sites into memory
    //Returns the number of sites read in
    ArrayList<Site> getAllSites(){

        Cursor cursor = getAllSitesCursor();

       //convert the cursor into a list of Site instances

        //create a site object from the Cursor object
        SiteManager siteManager = SiteManager.getInstance();

        int position = 0;
        int last = cursor.getCount();
        Site site;
        ArrayList<Site> sites = new ArrayList<>();

        while (position < last) {
            site = siteManager.getSiteFromCursor(cursor, position);
            if (site != null) {
                sites.add(site);
            }
            position++;
        }
        cursor.close();
        return sites;
    }

    //NOTE this routine does NOT add the site to the RAM list maintained by SiteManager
    Site getSite(long siteID){

        //get the site row from the DB
        Cursor cursor = mDatabaseHelper.getObject(
                                                mDatabase,     //the db to access
                                                DataBaseSqlHelper.TABLE_SITE,  //table name
                                                null,          //get the whole site
                                                getSiteWhereClause(siteID), //where clause
                                                null, null, null, null);//args, group, row grouping, order

        //create a site object from the Cursor object
        SiteManager siteManager = SiteManager.getInstance();
        int row = 0; //get the first row in the cursor
        return siteManager.getSiteFromCursor(cursor, row);

    }



    //********************************    Update   *************************


    //*********************************     Delete    ***************************


    //***********************************************/
    /*        Site specific CRUD  utility         */
    //***********************************************/
    private String getSiteWhereClause(long siteID){
        return DataBaseSqlHelper.SITE_ID + " = " + String.valueOf(siteID);
    }







    //***********************************************/
    /*         Static inner classes                 */
    //***********************************************/



    //***********************************************/
    /*         inner classes                        */
    //***********************************************/







}
