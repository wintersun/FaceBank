package com.scysun.app.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scysun.app.R;
import com.scysun.app.core.User;
import com.scysun.app.service.ContactService;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import butterknife.InjectView;

import static com.scysun.app.core.Constants.Extra.USER;

public class UserActivity extends BootstrapActivity {

    @InjectView(R.id.iv_avatar) protected ImageView avatar;
    @InjectView(R.id.tv_name) protected TextView name;
    @InjectView(R.id.tv_editDetail) protected Button btnEditDetail;

    private User user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_view);

        if (getIntent() != null && getIntent().getExtras() != null) {
            user = (User) getIntent().getExtras().getSerializable(USER);
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.with(this).load(user.getAvatarUrl())
                .placeholder(R.drawable.gravatar_icon)
                .into(avatar);


        name.setText(user.getFormatedRealUserName());

        btnEditDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Query the Contact_ID
                String contactId = user.getContactId();
                if(StringUtils.isEmpty(contactId)) {
                    ContentResolver resolver = getContentResolver();
                    Cursor cursor = resolver.query(ContactService.RAW_CONTACTS_URI,
                            new String[]{"contact_id"}, "_id=?", new String[]{user.getObjectId()}, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        if (cursor.moveToNext()) {
                            contactId = cursor.getString(0);
                        }
                        cursor.close();
                    }
                }

                Intent intent = new Intent(Intent.ACTION_EDIT,
                        Uri.parse("content://com.android.contacts/contacts/" + contactId));

                startActivity(intent);
            }
        });

    }

}
