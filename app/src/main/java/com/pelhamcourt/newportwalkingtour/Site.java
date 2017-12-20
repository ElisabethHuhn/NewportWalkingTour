package com.pelhamcourt.newportwalkingtour;



/**
 * Created by elisabethhuhn on 12/15/2016.
 * A site is a location with something to see
 */

class Site {
    //************************************/
    /*    Static (class) Constants       */
    //************************************/
    static final String sSiteIDTag         = "SITE_ID";


    //************************************/
    /*    Static (class) Variables       */
    //************************************/


    //************************************/
    /*    Member (instance) Variables    */
    //************************************/
    private long         mSiteID;
    private int          mOrdinal;
    private CharSequence mName;
    private long         mPrevSiteID;
    private long         mNextSiteID;
    private boolean      mBonus;
    private CharSequence mDirections;
    private CharSequence mShortDesc;
    private CharSequence mLongDesc;



    //************************************/
    /*         Static Methods            */
    //************************************/


    //************************************/
    /*         CONSTRUCTOR               */
    //************************************/

    Site( ) {
        initializeDefaultVariables();
    }
    Site( MainActivity activity) {
        initializeDefaultVariables();
        mName = activity.getString(R.string.site_default_name);
    }



    private void initializeDefaultVariables(){
        mSiteID     = Utilities.ID_DOES_NOT_EXIST;

        mOrdinal     = 1;
        mName        = "";
        mPrevSiteID  = Utilities.ID_DOES_NOT_EXIST;
        mNextSiteID  = Utilities.ID_DOES_NOT_EXIST;
        mBonus       = false;
        mDirections  = "";
        mShortDesc   = "";
        mLongDesc    = "";
    }




    //************************************/
    /*    Member setter/getter Methods   */
    //************************************/
    long getSiteID()                        {  return mSiteID; }
    void setSiteID(long siteID)             { mSiteID = siteID;}

    long getOrdinal()                       {  return mOrdinal; }
    void setOrdinal(int ordinal)            { mOrdinal = ordinal;}

    CharSequence getName()                  { return mName;  }
    void         setName(CharSequence name) { mName = name; }

    long getNextSiteID()                    {  return mNextSiteID; }
    void setNextSiteID(long siteID)         { mNextSiteID = siteID;}

    long getPrevSiteID()                    {  return mPrevSiteID; }
    void setPrevSiteID(long siteID)         { mPrevSiteID = siteID;}

    boolean isBonus()                       {return mBonus;}
    void    setBonus(boolean isBonus)       {mBonus = isBonus;}

    CharSequence getShortDesc()                       { return mShortDesc;  }
    void         setShortDesc(CharSequence shortDesc) { mShortDesc = shortDesc; }

    CharSequence getLongDesc()                        { return mLongDesc;  }
    void         setLongDesc(CharSequence longDesc)   { mLongDesc = longDesc; }

    CharSequence getDirections()                      { return mDirections;  }
    void         setDirections(CharSequence directions) { mDirections = directions; }





    //************************************/
    /*          Member Methods           */
    //************************************/


}
