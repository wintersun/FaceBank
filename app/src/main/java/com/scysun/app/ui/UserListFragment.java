package com.scysun.app.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.scysun.app.BootstrapServiceProvider;
import com.scysun.app.Injector;
import com.scysun.app.R;
import com.scysun.app.authenticator.LogoutService;
import com.scysun.app.core.Constants;
import com.scysun.app.core.User;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.scysun.app.service.ContactScanService;
import com.scysun.app.service.ContactService;
import com.scysun.app.util.Ln;
import com.scysun.app.util.PreferenceUtils;
import com.scysun.app.util.ServiceUtils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

import static com.scysun.app.core.Constants.Extra.USER;

public class UserListFragment extends ItemListFragment<User> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;

//    @InjectView(R.id.btn_scanContacts) protected Button btnScanContacts;

    ContactService contactService = null;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        if(contactService==null){
            contactService = new ContactService(this.getActivity().getContentResolver());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_users);
    }

    @Override
    protected void configureList(final Activity activity, final ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        View listLableView = activity.getLayoutInflater()
                .inflate(R.layout.user_list_item_labels, null);
        getListAdapter().addHeader(listLableView);

        // Inflate the layout for this fragment
        Views.inject(this, listLableView);

//        btnScanContacts.setVisibility(View.INVISIBLE);
//        Ln.d("btnScanContacts.getVisibility() = " + btnScanContacts.getVisibility());
//        btnScanContacts.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                scanContacts();
//            }
//        });
    }

    @Override
    protected LogoutService getLogoutService() {
        return logoutService;
    }

    @Override
    public Loader<List<User>> onCreateLoader(final int id, final Bundle args) {
        final List<User> initialItems = items;
        return new ThrowableLoader<List<User>>(getActivity(), items) {
            @Override
            public List<User> loadData() throws Exception {

                try {
                    List<User> latest = null;

                    if (getActivity() != null) {
                        //latest = serviceProvider.getService(getActivity()).getUsers();
                        latest = contactService.queryContacts(getActivity());
                    }

                    if (latest != null) {
//                        if(latest.isEmpty()){
//                            btnScanContacts.setVisibility(View.VISIBLE);
//                        }
                        return latest;
                    } else {
                        return Collections.emptyList();
                    }
//                } catch (final OperationCanceledException e) {
                }catch (final Exception e) {
                    final Activity activity = getActivity();
                    if (activity != null) {
                        activity.finish();
                    }
                    return initialItems;
                }
            }
        };

    }

    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final User user = ((User) l.getItemAtPosition(position));

        startActivity(new Intent(getActivity(), UserActivity.class).putExtra(USER, user));
    }

    @Override
    public void onLoadFinished(final Loader<List<User>> loader, final List<User> items) {
        super.onLoadFinished(loader, items);

    }

    @Override
    protected int getErrorMessage(final Exception exception) {
        return R.string.error_loading_users;
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(final List<User> items) {
        return new UserListAdapter(getActivity().getLayoutInflater(), items);
    }

    public void scanContacts()
    {
        Activity activity = this.getActivity();
        //TODO For Test
        PreferenceUtils.writeSharedPreferenceBooleanValue(activity, Constants.SharedPreferences_Contact.NAME, Constants.SharedPreferences_Contact.HAS_SCANNED, false, true);

        //Start to read contacts info
        //Has been scanned?
        if(!PreferenceUtils.readSharedPreferenceBooleanVariable(activity, Constants.SharedPreferences_Contact.NAME, Constants.SharedPreferences_Contact.HAS_SCANNED, false)) {
            //Service is not running?
            if (!ServiceUtils.isServiceRunning((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE), ContactScanService.class)) {
                final Intent i = new Intent(activity, ContactScanService.class);
                activity.startService(i);
            }
        }
    }
}
