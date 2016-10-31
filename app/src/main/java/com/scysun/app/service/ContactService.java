package com.scysun.app.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import static android.provider.ContactsContract.CommonDataKinds;

import com.scysun.app.core.User;
import com.scysun.app.persistence.ContactDao;
import com.scysun.app.persistence.DBUtils;
import com.scysun.app.persistence.DaoSession;
import com.scysun.app.persistence.entity.Contact;
import static com.scysun.app.core.Constants.Separator;
import com.scysun.app.util.Ln;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Biz Service class for contact info
 */
public class ContactService {
    public static Uri RAW_CONTACTS_URI = Uri.parse("content://com.android.contacts/raw_contacts");
    public static Uri CONTACTS_DATA_URI = Uri.parse("content://com.android.contacts/data");

    private ContentResolver resolver;
    private SQLiteDatabase contactsDB = null;
    private DaoSession daoSession = null;

    public ContactService(ContentResolver resolver)
    {
        this.resolver = resolver;
    }

    public List<Contact> scanAllContacts() {
        List<Contact> result = new ArrayList<Contact>();

        Cursor cursor = null;
        try {
            //调用联系人内容提供者的方法
            //Get the contact_id together?
            cursor = resolver.query(RAW_CONTACTS_URI, new String[]{ContactsContract.RawContacts._ID, "contact_id"}, null, null, null);
//            cursor = resolver.query(RAW_CONTACTS_URI, new String[]{ContactsContract.RawContacts._ID}, null, null, null);

            //FIXME: Only read top 20 record for fast testing
//            int count = 0;
//            int max = 20;

            //判断Cursor是否为空
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Contact contact = new Contact();

                    //从contacts表中获得联系人的id
                    long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
                    contact.setObjectId(String.valueOf(id));
                    long contactId = cursor.getLong(cursor.getColumnIndex("contact_id"));
                    contact.setContactId(String.valueOf(contactId));

                    //根据从contacts表中获得的联系人的id，查询data表
                    Cursor c = resolver.query(CONTACTS_DATA_URI,
                            new String[]{ContactsContract.Data.MIMETYPE,
                                    ContactsContract.Data.DATA1, ContactsContract.Data.DATA4},
                            "raw_contact_id=?",
                            new String[]{String.valueOf(id)},
                            null);
                    if (c != null && c.getCount() > 0) {
                        while (c.moveToNext()) {
                            String mimetype = c.getString(0);
//                            Ln.d("Current mimetype：" + mimetype);

                            String data1 = c.getString(1);
                            if (CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                                contact.setEmail(combineDataString(contact.getEmail(), data1));
                            } else if (CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                                contact.setPhone(combineDataString(contact.getPhone(), data1));
                            } else if (CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                                contact.setFirstName(data1);
                            } else if (CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(mimetype)) {
                                contact.setOrganization(data1);
                                contact.setJobTitle(c.getString(2));
                            } else if (CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) {
                                contact.setAddress(data1);
                            }
                        }
                    }
                    if(c!=null && !c.isClosed()) c.close();

                    if(StringUtils.isEmpty(contact.getPhone())){
//                        Ln.d("No phone number can be found in this contact, ignored.");
                        continue;
                    }
                    else{
                        //FIXME: Only read top 20 record for fast testing
//                        count++;
//                        if (count > max) {
//                            break;
//                        }

                        result.add(contact);
                    }
                }
            }
            Ln.d("Scanned contacts successfully and found total " + result.size() + " people!");
        }
        finally{
            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
        }

        return result;
    }

    @Deprecated
    public List<User> queryContacts_backup(Context context) {
        List<User> result = new ArrayList<User>();

        //调用联系人内容提供者的方法
        Cursor cursor = resolver.query(RAW_CONTACTS_URI, new String[]{ContactsContract.RawContacts._ID}, null, null, null);

        //FIXME: Only read top 20 record for fast testing
        int count = 0;
        int max = 20;

        //判断Cursor是否为空
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                User user = new User();

                //从contacts表中获得联系人的id
                long id = cursor.getLong(0);
                user.setObjectId(String.valueOf(id));
//                Ln.d("_id：" + id);

                //根据从contacts表中获得的联系人的id，查询data表
                Cursor c = resolver.query(CONTACTS_DATA_URI,
                        new String[]{"mimetype", ContactsContract.Data.DATA1},
                        "raw_contact_id=?",
                        new String[]{String.valueOf(id)},
                        null);
                if (c != null && c.getCount() > 0) {
                    while (c.moveToNext()) {
                        String mimetype = c.getString(0);
                        Ln.d("Current mimetype：" + mimetype);
                        String data1 = c.getString(1);
                        if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {
                            user.setEmail(data1);
                           Ln.d("邮箱：" + data1);
                        } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            user.setPhone(data1);
                           Ln.d("电话：" + data1);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            user.setFirstName(data1);
                           Ln.d("姓名:" + data1);
                        } else if ("vnd.android.cursor.item/organization".equals(mimetype)) {
                            user.setOrganization(data1);
                        } else if ("vnd.android.cursor.item/postal-address_v2".equals(mimetype)) {
                            user.setAddress(data1);
                        }
//                        else if(CommonDataKinds.Identity.CONTENT_ITEM_TYPE.equals(mimetype)){
//                            user.setObjectId(data1);
//                            Ln.d("Identity_id：" + data1);
//                        }
                    }
                }
                if(c!=null && !c.isClosed()) c.close();

                if(StringUtils.isEmpty(user.getPhone())){
                    Ln.d("No phone number can be found in this contact, ignored.");
                    continue;
                }
                else{
                    //FIXME: Only read top 20 record for fast testing
                    count++;
                    if (count > max) {
                        break;
                    }

                    result.add(user);
                }
            }
        }

        return result;
    }

    /***
     * Find contacts
     * @param context
     * @return
     */
    public List<User> queryContacts(Context context)
    {
        //TODO return Contact list but no User....
        List<User> result = new ArrayList<User>();

        contactsDB = DBUtils.openContactDB(context, true);
        daoSession = DBUtils.getContactDBSession(contactsDB);
        ContactDao contactDao = daoSession.getContactDao();

        List<Contact> contacts = contactDao.queryBuilder().orderAsc(ContactDao.Properties.FirstName).list();
        if(contacts!=null && !contacts.isEmpty()){
            for(Contact contact : contacts){
                User user = new User();
                user.setFirstName(contact.getFirstName());
                user.setObjectId(contact.getObjectId());
                user.setContactId(contact.getContactId());
                user.setOrganization(contact.getOrganization());
                user.setLastName(contact.getLastName());
                user.setAddress(contact.getAddress());
                user.setEmail(contact.getEmail());
                user.setPhone(contact.getPhone());

                result.add(user);
            }
        }

        DBUtils.closeContactDBSession(daoSession, true);

        return result;

    }
    /***
     * Combine multiple data into a long string with comma as separator
     * @param currVal
     * @param newVal
     * @return
     */
    private static String combineDataString(final String currVal, final String newVal)
    {
        StringBuffer result = new StringBuffer("");
        if(StringUtils.isNotEmpty(currVal)){
            result.append(currVal).append(Separator.COMMA).append(newVal);
        }
        else {
            return newVal;
        }
        return result.toString();
    }
}
