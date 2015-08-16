package com.arbrr.onehack.ui.contacts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;
import com.arbrr.onehack.ui.other.BackpressListener;
import com.arbrr.onehack.ui.other.SearchEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class ContactsFragment extends Fragment implements BackpressListener {
    private static final String LOGTAG = "MD/ContactsFragment";

    ArrayList<User> allUsers;
    NetworkManager mNetworkManager;

    // ListView in which to display all the contacts
    ListView mContactsListView;
    // Adapter to handle displaying all the data inside the listview
    ContactsListAdapter mContactsListAdapter;

    // TextView to show when the ListView is empty (temporary, spinners are nicer)
    TextView mEmptyListTextView;

    // Search related variables
    private MenuItem mSearchAction; // Reference to the search menu item itself
    private boolean mIsSearchOpened = false;
    private SearchEditText mSearchEditText;

    public static final String TITLE = "Contacts";

    public ContactsFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Set this fragment to handle options/actionbar/toolbar menu actions (ie, search)
        setHasOptionsMenu(true);

        // Cache the listView to set it up later
        mContactsListView = (ListView) view.findViewById(R.id.contactsList);

        // Get the textview to display while the listview is empty
        mEmptyListTextView = (TextView) view.findViewById(R.id.emptyListText);

        // Set the listView to actually use the placeholder textView
        mContactsListView.setEmptyView(mEmptyListTextView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Test: Getting the Network Manager to give some User info
        mNetworkManager = NetworkManager.getInstance();
        getData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Create the menu items for use in the action bar (specifically for searching)
        inflater.inflate(R.menu.menu_contacts, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Cache the search menu item so the icon can be dynamically changed later
        mSearchAction = menu.findItem(R.id.action_search);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            // If search menu item was selected...
            case R.id.action_search:
                handleMenuSearchClick();

                return true; // Handled this menu item click

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onBackpressCallback() {
        // If the "search bar" in the toolbar is showing, close it
        if(mIsSearchOpened) {
            // Act as if the user tapped the search menu item again to close it
            handleMenuSearchClick();

            return true; // Handled this event successfully
        }

        // Return false as this event wasn't fully handled
        return false;
    }

    /**
     * Handle the search menu item being clicked
     */
    protected void handleMenuSearchClick(){
        // ORIGINAL: https://bytedebugger.wordpress.com/2015/03/30/tutorial-android-actionbar-with-material-design-and-search-field/

        ActionBar action = ((AppCompatActivity) getActivity()).getSupportActionBar(); //get the actionbar

        if(mIsSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));

            // Clear out any possible previous searches/filters by simply doing an empty search
            searchUsers("");

            mIsSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            mSearchEditText = (SearchEditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            mSearchEditText.registerBackpressCallback(this); // Register this fragment to be called when back is pressed

            //this is a listener to do a search when the user clicks on search button
            mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchUsers(v.getText());
                        return true;
                    }
                    return false;
                }
            });

            mSearchEditText.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close));

            mIsSearchOpened = true;
        }
    }

    /**
     * Search through the full list of users and filter the list of users
     * @param searchConstraint The string to filter down the list of users (by name or company)
     */
    private void searchUsers(CharSequence searchConstraint) {
        // Simply let the adapter handle filtering and changing the display of users
        mContactsListAdapter.getFilter().filter(searchConstraint);
    }

    private void getData() {
        mNetworkManager.getContacts(new OneHackCallback<List<User>>() {
            @Override
            public void success(List<User> response) {
                // Response should be the list of users
                // TODO: Double check just in case it wasn't

                Log.d(LOGTAG, "Got all the data, with " + response.size() + " users");

                // Cache all the users internally
                allUsers = (ArrayList<User>) response;

                // Set up the ListView to display all the contacts, now that data is here
                initContactsList();
            }

            @Override
            public void failure(Throwable error) {
                // Make a toast to throw the error into the face of the debugging user
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                // And log the error, of course
                Log.e(LOGTAG, "Error: " + error.getMessage());
            }
        });
    }

    private void initContactsList() {
        // Create and setup the adapter that'll handle displaying all the data
        mContactsListAdapter = new ContactsListAdapter(getActivity(), allUsers);

        // Attach that adapter to the list
        mContactsListView.setAdapter(mContactsListAdapter);
    }

    class ContactsListAdapter extends BaseAdapter implements Filterable {
        Context mContext;

        // List of all the contacts (all the data need to show is here)
        ArrayList<User> mAllUsersList;

        // List of actual users that will be shown
        ArrayList<User> mShownUsersList;

        // Cached instance of the filter for this adapter
        ContactFilter mFilter;

        public ContactsListAdapter(Context context, final ArrayList<User> allUsers) {
            // Cache the variables for later use
            mContext = context;
            mAllUsersList = allUsers;

            // By default there is no filter, so show all the users
            mShownUsersList = allUsers;
        }

        @Override
        public int getCount() {
            return mShownUsersList.size();
        }

        @Override
        public Object getItem(int position) {
            return mShownUsersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = null;
            ViewHolder holder;

            if(convertView == null) {
                // Then gotta set up this row for the first time
                LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.listitem_contacts, parent, false);

                // Create a ViewHolder to save all the different parts of the row
                holder = new ViewHolder();
                holder.name = (TextView) row.findViewById(R.id.nameText);
                holder.company = (TextView) row.findViewById(R.id.companyText);
                holder.icon = (ImageView) row.findViewById(R.id.userIcon);

                // Make the row reuse the ViewHolder
                row.setTag(holder);
            }
            else { // Otherwise, use the recycled view
                row = convertView;
                holder = (ViewHolder) row.getTag();
            }

            // Set the current row's data
            User thisUser = mShownUsersList.get(position);
            holder.name.setText(thisUser.getFirstName() + " " + thisUser.getLastName());
            holder.company.setText(thisUser.getCompany());

            return row;
        }

        public class ViewHolder{
            public TextView name;
            public TextView company;

            public ImageView icon;
        }

        @Override
        public Filter getFilter() {
            // If the fitler hasn't yet been instantiated, create it
            if(mFilter == null) {
                mFilter = new ContactFilter();
            }

            return mFilter;
        }

        private class ContactFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Create the results that will ultimately be returned
                FilterResults results = new FilterResults();

                // If the constraint is actually a valid constraint to compare with...
                if(constraint != null && constraint.toString().length() > 0) {
                    // Only check the constraint in its lowercased form, for simplicity
                    constraint = constraint.toString().toLowerCase();

                    // Initialize the filtered list of users
                    ArrayList<User> filteredUsers = new ArrayList<User>();

                    // Find all the users in the full list of users that match the constraint
                    for(User curUser : mAllUsersList) {
                        // If the current user's name or company matches the filter, than it's good
                        boolean isFirstNameMatch = curUser.getFirstName().toLowerCase().contains(constraint);
                        boolean isLastNameMatch = curUser.getLastName().toLowerCase().contains(constraint);
                        boolean isCompanyMatch = curUser.getCompany().toLowerCase().contains(constraint);

                        if(isFirstNameMatch || isLastNameMatch || isCompanyMatch) {
                            // Add this valid user to the filtered list
                            filteredUsers.add(curUser);
                        }

                        // Update the final results
                        results.count = filteredUsers.size();
                        results.values = filteredUsers;
                    }
                }
                // If no valid constraint, then all the users are technically valid
                else {
                    synchronized (this) {
                        // Ultimately, the results contain all the users
                        results.count = mAllUsersList.size();
                        results.values = mAllUsersList;
                    }
                }

                // Finally, return the results
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Save the filtered list of users to show
                mShownUsersList = (ArrayList<User>) results.values;

                notifyDataSetChanged();
            }
        }

    }
}
