package com.arbrr.onehack.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.arbrr.onehack.R;
import com.arbrr.onehack.ui.announcements.AnnouncementsFragment;
import com.arbrr.onehack.ui.awards.AwardsFragment;
import com.arbrr.onehack.ui.events.EventsFragment;
import com.arbrr.onehack.ui.contacts.ContactsFragment;
import com.arbrr.onehack.ui.welcome.LoginFragment;
import com.arbrr.onehack.ui.welcome.SignupFragment;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class MainActivity extends ActionBarActivity {

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
    private String[]     mNavTitles;
    private DrawerLayout mDrawerLayout;
    private ListView     mNavDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        // Instantiate fragments
        announcementsFragment = new AnnouncementsFragment();
        eventsFragment = new EventsFragment();
        contactsFragment = new ContactsFragment();
        awardsFragment = new AwardsFragment();
        loginFragment = new LoginFragment();
        signupFragment = new SignupFragment();

        // Inflate LoginFragment
        updateFragment(loginFragment, LoginFragment.TITLE);
        setTitle(LoginFragment.TITLE);

        // Navigation Drawer set up
        mNavTitles = getResources().getStringArray(R.array.nav_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavDrawerList = (ListView) findViewById(R.id.left_drawer);
        mNavDrawerList.setAdapter(new ArrayAdapter<String>(this,
                                                           R.layout.drawer_list_item,
                                                           R.id.drawer_item_title,
                                                           mNavTitles));
        mNavDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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
     * Determines which fragment should populate the main_fragment_container based on the selected
     * position.
     * @param position position of the fragment in the list
     */
    private void selectFragment(int position) {
        switch (position) {
            case 0:
                updateFragment(announcementsFragment, mNavTitles[position]);
                break;
            case 1:
                updateFragment(eventsFragment, mNavTitles[position]);
                break;
            case 2:
                updateFragment(contactsFragment, mNavTitles[position]);
                break;
            case 3:
                updateFragment(awardsFragment, mNavTitles[position]);
                break;
        }

        // Update and/or close navigation drawer
        // TODO: find a better place for this?
        mNavDrawerList.setItemChecked(position, true);
        setTitle(mNavTitles[position]);
        mDrawerLayout.closeDrawer(mNavDrawerList);
    }

    /**
     * Updates the main_fragment_container with the given fragment.
     * @param fragment fragment to replace the main container with
     */
    private void updateFragment(Fragment fragment, String title) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.addToBackStack(title); // for back button navigation
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
        if(mDrawerLayout.isDrawerOpen(mNavDrawerList)) {
            mDrawerLayout.closeDrawer(mNavDrawerList); // close nav drawer if open
        } else if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String title = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                                                      .getName();
            getSupportFragmentManager().popBackStack(); // otherwise pop fragment back stack
            setTitle(title);
        } else {
            super.onBackPressed(); // let the android overlords handle that shit
        }
    }

    //////////////////// DrawerItemClickListener ////////////////////
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectFragment(position);
        }
    }
}
