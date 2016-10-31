package com.scysun.app.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.scysun.app.persistence.DaoMaster.DevOpenHelper;

/**
 * Database Utils for GreeDAO
 * @author Phoenix
 */
public class DBUtils
{

    public static SQLiteDatabase openContactDB(Context context, boolean readonly)
    {
        SQLiteDatabase contactsDB = null;
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "contacts-db", null);
        if(!readonly) {
            contactsDB = helper.getWritableDatabase();
        }
        else{
            contactsDB = helper.getReadableDatabase();
        }
        return contactsDB;
    }

    /***
     * Create new session
     * @param contactsDB
     * @return
     */
    public static DaoSession getContactDBSession(SQLiteDatabase contactsDB)
    {
        DaoMaster contactsDaoMaster = new DaoMaster(contactsDB);
        return contactsDaoMaster.newSession();
    }

    /***
     * Close DB Session
     * @param daoSession
     * @param closeDB
     * If true, close DB connection too
     */
    public static void closeContactDBSession(DaoSession daoSession, boolean closeDB)
    {
        if(daoSession != null){
            daoSession.clear();
        }
        if(closeDB){
            SQLiteDatabase db = daoSession.getDatabase();
            if(db != null && db.isOpen()){
                db.close();
                db = null;
            }
        }
    }
}
