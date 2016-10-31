package com.scysun.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.scysun.app.Injector;
import com.scysun.app.R;
import com.scysun.app.persistence.ContactDao;
import com.scysun.app.persistence.DBUtils;
import com.scysun.app.persistence.DaoMaster;
import com.scysun.app.persistence.DaoSession;
import com.scysun.app.persistence.entity.Contact;
import com.scysun.app.ui.MainActivity;
import com.scysun.app.util.Ln;
import com.scysun.app.util.PreferenceUtils;

import java.util.List;

import javax.inject.Inject;

import static com.scysun.app.core.Constants.Notification.CONTACT_SCANNING_NOTIFICATION_ID;
import static com.scysun.app.core.Constants.Notification.CONTACT_SCANNING_RESULT_NOTIFICATION_ID;
import static com.scysun.app.core.Constants.SharedPreferences_Contact;

public class ContactScanService extends Service
{
    private ContactService contactService = null;

    private SQLiteDatabase contactsDB = null;
    private DaoSession daoSession = null;

    @Inject
    NotificationManager notificationManager;

    public ContactScanService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.inject(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Ln.d("Service has been started");

        super.onStartCommand(intent,flags,startId);

        new ScanContactTask().execute();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(CONTACT_SCANNING_NOTIFICATION_ID);

        Ln.d("Service has been destroyed");

        super.onDestroy();

        DBUtils.closeContactDBSession(this.daoSession, true);
    }

    /**
     * Creates a notification to show in the notification bar
     *
     * @param message the message to display in the notification bar
     * @return a new {@link android.app.Notification}
     */
    private Notification getNotification(String message) {
        final Intent i = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_stat_ab_notification)
                .setContentText(message)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .getNotification();
    }

    class ScanContactTask extends AsyncTask<Integer ,Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            startForeground(CONTACT_SCANNING_NOTIFICATION_ID, getNotification(getString(R.string.start_scan)));

            if(contactService==null){
                contactService = new ContactService(ContactScanService.this.getContentResolver());
            }
            List<Contact> contacts = contactService.scanAllContacts();

            if(contacts!=null && contacts.size()>0){
                contactsDB = DBUtils.openContactDB(ContactScanService.this, false);
                daoSession = DBUtils.getContactDBSession(contactsDB);
                ContactDao contactDao = daoSession.getContactDao();

                //TODO need check existing or not
                contactDao.deleteAll();

                for(Contact contact : contacts){
//                contactDao.q
                    //TODO need check existing or not
                    contactDao.insert(contact);
                }

                Ln.d("Saved all contacts to Database!");
                DBUtils.closeContactDBSession(daoSession, true);

                PreferenceUtils.writeSharedPreferenceBooleanValue(ContactScanService.this, SharedPreferences_Contact.NAME,
                        SharedPreferences_Contact.HAS_SCANNED, true, true);
            }

            return contacts.size();
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            startForeground(CONTACT_SCANNING_RESULT_NOTIFICATION_ID, getNotification(
                    getString(R.string.scan_result, result)));

            ContactScanService.this.stopSelf();
        }
    };
}
