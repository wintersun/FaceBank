package com.scysun.app;

import android.accounts.AccountManager;
import android.content.Context;

import com.scysun.app.authenticator.BootstrapAuthenticatorActivity;
import com.scysun.app.authenticator.LogoutService;
import com.scysun.app.core.TimerService;
import com.scysun.app.ui.BootstrapTimerActivity;
import com.scysun.app.ui.MainActivity;
import com.scysun.app.ui.CheckInsListFragment;
import com.scysun.app.ui.NavigationDrawerFragment;
import com.scysun.app.ui.NewsActivity;
import com.scysun.app.ui.NewsListFragment;
import com.scysun.app.ui.UserActivity;
import com.scysun.app.ui.UserListFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                BootstrapApplication.class,
                BootstrapAuthenticatorActivity.class,
                MainActivity.class,
                BootstrapTimerActivity.class,
                CheckInsListFragment.class,
                NavigationDrawerFragment.class,
                NewsActivity.class,
                NewsListFragment.class,
                UserActivity.class,
                UserListFragment.class,
                TimerService.class
        }
)
public class BootstrapModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

}
