package com.scysun.app.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.scysun.app.BootstrapServiceProvider;
import com.scysun.app.Injector;
import com.scysun.app.R;
import com.scysun.app.authenticator.LogoutService;
import com.scysun.app.core.User;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.scysun.app.service.ContactService;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.scysun.app.core.Constants.Extra.USER;

public class UserListFragment extends ItemListFragment<User> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;

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
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_users);
    }

    @Override
    protected void configureList(final Activity activity, final ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        getListAdapter().addHeader(activity.getLayoutInflater()
                .inflate(R.layout.user_list_item_labels, null));
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
                        latest = contactService.readContacts();
                    }

                    if (latest != null) {
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
}
