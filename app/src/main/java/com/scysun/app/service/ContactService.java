package com.scysun.app.service;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.scysun.app.core.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for contact info
 */
public class ContactService {
    private static Uri contactUri = Uri.parse("content://com.android.contacts/raw_contacts");
    private static Uri dataUri = Uri.parse("content://com.android.contacts/data");

    private ContentResolver resolver;

    public ContactService(ContentResolver resolver)
    {
        this.resolver = resolver;
    }

    public List<User> readContacts() {
        List<User> result = new ArrayList<User>();

        //调用联系人内容提供者的方法
        Cursor cursor = resolver.query(contactUri, new String[]{ContactsContract.Contacts._ID}, null, null, null);

        //FIXME: Only read top 20 record for fast testing
        int count = 0;
        int max = 20;

        //判断Cursor是否为空
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                //FIXME: Only read top 20 record for fast testing
                count++;
                if(count > max){
                    break;
                }

                User user = new User();
                result.add(user);

                //从contacts表中获得联系人的id
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                user.setObjectId(String.valueOf(id));

                //根据从contacts表中获得的联系人的id，查询data表
                Cursor c = resolver.query(dataUri,
                        new String[]{"mimetype", ContactsContract.Data.DATA1},
                        "raw_contact_id=?",
                        new String[]{String.valueOf(id)},
                        null);
                if (c != null && c.getCount() > 0) {
                    while (c.moveToNext()) {
                        String mimetype = c.getString(0);
                        System.out.println("Current mimetype：" + mimetype);
                        String data1 = c.getString(1);
                        if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {
                            user.setEmail(data1);
//                            System.out.println("邮箱：" + data1);
                        } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            user.setPhone(data1);
//                            System.out.println("电话：" + data1);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            user.setFirstName(data1);
//                            System.out.println("姓名:" + data1);
                        } else if ("vnd.android.cursor.item/organization".equals(mimetype)) {
                            user.setOrganization(data1);
                        } else if ("vnd.android.cursor.item/postal-address_v2".equals(mimetype)) {
                            user.setAddress(data1);
                        }
                    }
                }
                c.close();
            }
        }

        return result;
    }
}
