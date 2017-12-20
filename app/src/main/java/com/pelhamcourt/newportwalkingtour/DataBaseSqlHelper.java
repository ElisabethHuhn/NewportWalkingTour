package com.pelhamcourt.newportwalkingtour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.pelhamcourt.newportwalkingtour.DatabaseManager.sDB_ERROR_CODE;

/**
 * Created by Elisabeth Huhn on 7/9/2016.
 * This class makes all the actual calls to the DB
 * Thus, if there is a need to put such calls on a background thread, that
 * can be managed by the DB Manager.
 * But if it touches the DB directly, this class does it
 */
class DataBaseSqlHelper extends SQLiteOpenHelper {

    //****************************************************/
    //****************************************************/
    //****************************************************/

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    private static final String DATABASE_NAME = "NewportWalkingTour";

    //****************************************************/
    //****************************************************/
    //****************************************************/
    //Common Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    //****************************************************/
    //****    Person Table     ***************************/
    //****************************************************/

    //Table Name
    static final String TABLE_SITE          = "Site";

      //Person Column Names
    static final String SITE_ID           = "site_id";
    static final String SITE_ORDINAL      = "ordinal";
    static final String SITE_NAME         = "site_name";
    static final String SITE_PREV_SITE_ID = "site_prev_site";
    static final String SITE_NEXT_SITE_ID = "site_next_site";
    static final String SITE_BONUS        = "site_bonus";
    static final String SITE_DIRECTIONS   = "site_directions";
    static final String SITE_SHORT_DESC   = "site_short_desc";
    static final String SITE_LONG_DESC    = "site_long_desc";



    //create site table
    private static final String CREATE_TABLE_SITE = "CREATE TABLE " + TABLE_SITE +"(" +
            KEY_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SITE_ID            + " INTEGER, "   +
            SITE_ORDINAL       + " INTEGER, "   +
            SITE_NAME          + " TEXT, "      +
            SITE_PREV_SITE_ID  + " TEXT, "      +
            SITE_NEXT_SITE_ID  + " TEXT, "      +
            SITE_BONUS         + " INTEGER, "   +
            SITE_DIRECTIONS    + " TEXT, "      +
            SITE_SHORT_DESC    + " TEXT, "      +
            SITE_LONG_DESC     + " TEXT, "   +
            KEY_CREATED_AT  + " DATETIME "   + ")";




    //****************************************************/
    //****************************************************/
    //****************************************************/
    private Context mContext;



    //****************************************************/
    //******  Constructor               ******************/
    //****************************************************/

    //This should be called with the APPLICATION context
    DataBaseSqlHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.mContext = context;
    }

    //****************************************************/
    //******  Lifecycle Methods         ******************/
    //****************************************************/

    /*****************
     * onCreate()
     * when the helper constructor is executed with a name (2nd param),
     * the platform checks if the database (second parameter) exists or not and
     * if the database exists, it gets the version information from the database file header and
     * triggers the right call back (e.g. onUpdate())
     * if the database with the name doesn't exist, the platform triggers onCreate().
     *
     * @param db  The instance of the database that is being created
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        //create the tables using the pre-defined SQL
        db.execSQL(CREATE_TABLE_SITE);

    }

    /*****************
     * This default version of the onUpgrade() method just
     * deletes any data in the database file, and recreates the
     * database from scratch.
     *
     * Obviously, in the production version, this method will have
     * to migrate data in the old version table layout
     * to the new version table layout.
     * Renaming tables,
     * creating new tables,
     * writing data from renamed table to the new table,
     * then dropping the renamed table.
     * And doing this in a cascading fashion so the tables can
     * be brought up to date over several versions.
     * @param db         The instance of the db to be upgraded
     * @param OldVersion The old version number
     * @param newVersion The new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int OldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITE);


        //Create new tables
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }





    //***********************************************/
    /*         Generic CRUD routines                */
    /* The same routine will do for all data types  */
    //***********************************************/


    ///***************************** Create ****************************

    /***************************************
     * Add is actually a complex function.
     * Add checks whether the object already exists within the DB.
     * If it does, the row is updated with the new values.
     * Else, the object is inserted into the DB, returning the new ID
     *   (which is unique within the table).
     *   The function then re-updates the row with the new ID.
     *
     * @param db            - The database to be updated
     * @param table         - The table within the database
     * @param values        - A content values structure describing the row
     * @param where_clause  - Uniquely describes the row to be updated
     * @param id_key        - The key of the ID column within the row
     * @return              - The databaseID of the object added/updated.
     *                        sDB_ERROR_CODE if an error occurred
     */
    long add(SQLiteDatabase db,
             String table,
             ContentValues values,          //Column names and new values
             String where_clause,
             String id_key){//null updates all rows

        long returnCode = 0;
        long returnKey = 0;

        long id_value = (long) values.get(id_key);
        if (id_value == Utilities.ID_DOES_NOT_EXIST){
            //Add it to the DB
            //need to insert
            returnCode = db.insert(table, null, values);
            if (returnCode == sDB_ERROR_CODE)return sDB_ERROR_CODE;

            //get ready to update the DB row with the new ID
            values.put(id_key, returnCode);

            //get ready to pass back the new ID
            returnKey = returnCode;

            returnCode = db.update(table, values, where_clause, null);
        } else {
            //Update the existing DB row

            //get ready to pass back the instance ID
            returnKey = (long) values.get(id_key);

            //update the row in the DB
            returnCode = db.update(table, values, where_clause, null);
        }

        //db.close(); //never close the db instance. Just leave the connection open

        if (returnCode == sDB_ERROR_CODE)return sDB_ERROR_CODE;

        //return the instance/row ID
        return returnKey;
    }

    //**************************** READ *******************************
    Cursor getObject(SQLiteDatabase db,
                     String table,
                     String[] columns,
                     String where_clause,
                     String[] selectionArgs,
                     String groupBy,
                     String having,
                     String orderBy){
        /* ******************************
         Cursor query (String table, //Table Name
                         String[] columns,   //Columns to return, null for all columns
                         String where_clause,
                         String[] selectionArgs, //replaces ? in the where_clause with these arguments
                         String groupBy, //null meanas no grouping
                         String having,   //row grouping
                         String orderBy)  //null means the default sort order
         *********************************/
            return (db.query(table, columns, where_clause, selectionArgs, groupBy, having, orderBy));
    }


    //********************* UPDATE *************************
    //use add, it attempts an insert. If that fails, it tries an update


    //***************** DELETE ***************************************
    //returns the number of rows affected
    int remove (SQLiteDatabase db,
                String table,
                String where_clause,//null updates all rows
                String[]       where_args ){ //values that replace ? in where clause

        return (delete(db, table, where_clause, where_args));
    }


    //returns the number of rows affected
    int delete (SQLiteDatabase db,
                String table,
                String where_clause,//null updates all rows
                String[]       where_args ){ //values that replace ? in where clause

        return (db.delete(table, where_clause, where_args));
    }


    //***********************************************/
    /*      Object Specific CRUD routines           */
    /*     Each Class has it's own routine          */
    //***********************************************/

    //********************* PERSON ****************************************************88




}
