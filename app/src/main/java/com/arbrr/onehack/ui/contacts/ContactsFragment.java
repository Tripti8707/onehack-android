package com.arbrr.onehack.ui.contacts;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;

import java.util.List;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class ContactsFragment extends Fragment {
    private static final String LOGTAG = "MD/ContactsFragment";

    List<User> allUsers;
    NetworkManager networkManager;

    // ListView in which to display all the contacts
    ListView mContactsListView;
    // Adapter to handle displaying all the data inside the listview
    ContactsListAdapter mContactsListAdapter;

    // TextView to show when the ListView is empty (temporary, spinners are nicer)
    TextView mEmptyListTextView;

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
        networkManager = NetworkManager.getInstance();
        networkManager.logUserIn("admin@admin.com", "admin", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(LOGTAG, "Logged in!");
                getData();
            }

            @Override
            public void failure(Throwable error) {
                Log.d(LOGTAG, "Couldn't log in :(");
            }
        });
    }

    private void getData() {
        networkManager.getContacts(new OneHackCallback<List<User>>() {
            @Override
            public void success(List<User> response) {
                // Response should be the list of users
                // TODO: Double check just in case it wasn't

                Log.d(LOGTAG, "Got all the data, with " + response.size() + " users");

                // Cache all the users internally
                allUsers = (List<User>) response;

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

    class ContactsListAdapter extends BaseAdapter {
        Context mContext;

        // List of all the contacts (all the data need to show is here)
        List<User> mUsers;

        public ContactsListAdapter(Context context, final List<User> allUsers) {
            // Cache the variables for later use
            mContext = context;
            mUsers = allUsers;
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public Object getItem(int position) {
            return mUsers.get(position);
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
            User thisUser = mUsers.get(position);
            holder.name.setText(thisUser.getFirstName() + " " + thisUser.getLastName());
            holder.company.setText(thisUser.getCompany());

            return row;
        }

        public class ViewHolder{

            public TextView name;
            public TextView company;

            public ImageView icon;

        }
    }
}