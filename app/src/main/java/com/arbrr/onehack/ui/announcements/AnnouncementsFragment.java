package com.arbrr.onehack.ui.announcements;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Announcement;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;
import com.arbrr.onehack.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class AnnouncementsFragment extends Fragment {
    private static final String tag = "ONEHACK-AF";

    private NetworkManager networkManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final static List<Announcement> announcementList = new ArrayList<Announcement>();

    public AnnouncementsFragment() {
        // Required empty public constructor.
    }

    private void getOtherData() {
        networkManager.getAnnouncements(new OneHackCallback<List<Announcement>>() {
            @Override
            public void success(List<Announcement> announcements) {
                Log.d(tag, "Got " + announcements.size() + " announcements");
                for(int i = 0; i < announcements.size(); i++){
                    announcementList.add(announcements.get(i));
                }
                Log.d(tag, "List length after loop: " + announcementList.size());
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, ":(");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_announcements, container, false);
        //getActivity().setTitle(R.string.announcements_title); //set ActionBar title
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.announcements_title);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

        //announcementList = new ArrayList<Announcement>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.announcement_rv);

        // use this setting to improve performance if changes in content do not change layout size
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //announcementList = new ArrayList<Announcement>();

        //log user in
        networkManager = NetworkManager.getInstance();
        networkManager.logUserIn("admin@admin.com", "admin", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(tag, "Logged in!");
                getOtherData();
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, "Couldn't log in :(");
            }
        });

        //-----------------------------------NOTE--------------------------------------
        //this kinda works now. By declaring the ArrayList as "final static" the announcements
        //are loaded ONLY if the view is reloaded (e.g. the user navigates to the NewAnnouncement
        //Fragment and then goes back to the Announcements Fragment or if the user hits the back
        //button and reopens the app.

        //this always logs 0 for some reason?
        Log.d(tag, "List length after logging in: " + announcementList.size());

        // instantiate adapter
        mAdapter = new MyAdapter(announcementList);
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // create the menu items for use in the action bar
        inflater.inflate(R.menu.fragment_announcements, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_compose:
                //switch to NewAnnouncement Fragment
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                NewAnnouncementFragment newAnnouncementFragment = new NewAnnouncementFragment();
                fragmentTransaction.replace(R.id.main_fragment_container, newAnnouncementFragment);
                fragmentTransaction.addToBackStack(this.getTag());
                fragmentTransaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Announcement> announcements;

        //Container class for all the views in each "item" of the RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Animation.AnimationListener {
            private CardView cv;
            private TextView titleView;
            private TextView messageView;
            private TextView dateView;
            private TextView deleteView;
            private boolean isSlided;
            private Context context;

            public ViewHolder(View v, Context context) {
                super(v);
                cv = (CardView) itemView.findViewById(R.id.announcement_card);
                titleView = (TextView) itemView.findViewById(R.id.announcement_title);
                messageView = (TextView) itemView.findViewById(R.id.announcement_message);
                dateView = (TextView) itemView.findViewById(R.id.announcement_date);

                deleteView = (TextView) itemView.findViewById(R.id.announcement_delete);
                cv.setOnClickListener(this);
                deleteView.setOnClickListener(this);

                isSlided = false;
                this.context = context;
            }

            @Override
            public void onClick(View v) {
                if (!isSlided) {
                    //slide over the announcement to reveal a delete button
                    TranslateAnimation slideOver = new TranslateAnimation(0, -deleteView.getWidth(), 0, 0);
                    slideOver.setDuration(800);
                    slideOver.setFillAfter(true);
                    slideOver.setAnimationListener(this);
                    cv.startAnimation(slideOver);
                } else {
                    if (v.getId() == R.id.announcement_card) {
                        //slide back the announcement covering up the delete button
                        TranslateAnimation slideOver = new TranslateAnimation(0, deleteView.getWidth(), 0, 0);
                        slideOver.setDuration(800);
                        slideOver.setFillAfter(true);
                        slideOver.setAnimationListener(this);
                        cv.startAnimation(slideOver);
                    }
                    if (v.getId() == R.id.announcement_delete) {
                        //delete the announcement
                        announcementList.remove(this.getAdapterPosition());
                        mAdapter.notifyDataSetChanged(); //refresh recyler view
                        Toast.makeText(context, "Delete Announcement!", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onAnimationEnd(Animation animation) {
                //animation only moves around pixels. Now we have to actually move the view over
                if (!isSlided) {
                    cv.clearAnimation();
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cv.getLayoutParams();
                    mlp.rightMargin += deleteView.getWidth();
                    mlp.leftMargin = -deleteView.getWidth();
                    cv.setLayoutParams(mlp);
                } else {
                    cv.clearAnimation();
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cv.getLayoutParams();
                    mlp.rightMargin = 0;
                    mlp.leftMargin = 0;
                    cv.setLayoutParams(mlp);
                }

                isSlided = !isSlided;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        }

        public MyAdapter(List<Announcement> announcementsIn) {
            announcements = announcementsIn;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new announcement view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.announcement, parent, false);

            ViewHolder vh = new ViewHolder(v, parent.getContext());
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int i) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.titleView.setText(announcements.get(i).name);
            holder.messageView.setText(announcements.get(i).info);
            holder.dateView.setText(announcements.get(i).broadcastTime.toString());
            holder.titleView.setTextColor(Color.BLACK);
            holder.messageView.setTextColor(Color.GRAY);
            holder.dateView.setTextColor(Color.GRAY);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return announcements.size();
        }
    }
}
