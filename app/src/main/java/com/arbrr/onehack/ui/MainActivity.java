package com.arbrr.onehack.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.ui.announcements.AnnouncementsFragment;
import com.arbrr.onehack.ui.awards.AwardsFragment;
import com.arbrr.onehack.ui.events.EventsFragment;
import com.arbrr.onehack.ui.contacts.ContactsFragment;
import com.arbrr.onehack.ui.welcome.LoginFragment;
import com.arbrr.onehack.ui.welcome.SignupFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    // Fragments
    private AnnouncementsFragment announcementsFragment;
    private EventsFragment        eventsFragment;
    private ContactsFragment      contactsFragment;
    private AwardsFragment        awardsFragment;
    private LoginFragment         loginFragment;
    private SignupFragment        signupFragment;

    // Toolbar
    private Toolbar mToolbar;

    // Navigation Drawer
    private Drawer mDrawer;

    // Network Manager
    private NetworkManager mNetworkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        // Network Manager
        mNetworkManager = NetworkManager.getInstance();

        // Instantiate fragments
        announcementsFragment = new AnnouncementsFragment();
        eventsFragment = new EventsFragment();
        contactsFragment = new ContactsFragment();
        awardsFragment = new AwardsFragment();
        loginFragment = new LoginFragment();
        signupFragment = new SignupFragment();

        // Inflate LoginFragment
        updateFragment(loginFragment);
        setTitle(LoginFragment.TITLE);

        // Navigation Drawer set up
        buildNavigationDrawer();
    }

    private void buildNavigationDrawer() {
        // Drawer items
        PrimaryDrawerItem announcements = new PrimaryDrawerItem().withName("Announcements")
                                                                 .withIcon(R.drawable.ic_announcement)
                                                                 .withSelectedTextColorRes(R.color.primary_dark);
        PrimaryDrawerItem events = new PrimaryDrawerItem().withName("Events")
                                                          .withIcon(R.drawable.ic_event)
                                                          .withSelectedTextColorRes(R.color.primary_dark);
        PrimaryDrawerItem contacts = new PrimaryDrawerItem().withName("Contacts")
                                                            .withIcon(R.drawable.ic_contact)
                                                            .withSelectedTextColorRes(R.color.primary_dark);
        PrimaryDrawerItem awards = new PrimaryDrawerItem().withName("Awards")
                                                          .withIcon(R.drawable.ic_award)
                                                          .withSelectedTextColorRes(R.color.primary_dark);
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName("Settings")
                                                                .withIcon(R.drawable.ic_settings)
                                                                .withSelectedTextColorRes(R.color.primary_dark);

        // Account Hackathons
        ProfileDrawerItem mhacks = new ProfileDrawerItem().withName("MHacks 6")
                                                          .withIcon(
                                                                  "http://mhacks.org/images/mhacks_logo.svg")
                                                          .withTextColorRes(R.color.primary_text);
        mhacks.setSelectedColorRes(R.color.primary_dark);

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(mhacks)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view,
                                                    IProfile profile,
                                                    boolean currentProfile) {
                        return true;
                    }
                })
                .withTextColorRes(R.color.primary_text)
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(accountHeader)
                .addDrawerItems(announcements, events, contacts, awards,
                                new DividerDrawerItem(),
                                settings)
                .build();

        mDrawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(AdapterView<?> adapterView,
                                       View view,
                                       int i,
                                       long l,
                                       IDrawerItem iDrawerItem) {
                Log.d(TAG, "nav position: " + i);

                // switch 'i' aka position of item
                switch (i) {
                    case 0:
                        updateFragment(announcementsFragment);
                        break;
                    case 1:
                        updateFragment(eventsFragment);
                        break;
                    case 2:
                        updateFragment(contactsFragment);
                        break;
                    case 3:
                        updateFragment(awardsFragment);
                        break;
                    default:
                        return false;
                }

                mDrawer.closeDrawer();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the main_fragment_container with the given fragment.
     * @param fragment fragment to replace the main container with
     */
    private void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Sets the supportActionBar title to the given title.
     * @param title title of the action bar
     */
    public void setTitle(CharSequence title) {
        // null pointer check
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer(); // close nav drawer if open
        } else if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // otherwise pop fragment back stack
        } else {
            super.onBackPressed(); // let the android overlords handle that shit
        }
    }
}
