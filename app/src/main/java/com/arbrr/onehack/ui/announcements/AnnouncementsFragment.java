package com.arbrr.onehack.ui.announcements;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Announcement;
import com.arbrr.onehack.data.network.GenericResponse;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;
import com.arbrr.onehack.ui.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class AnnouncementsFragment extends Fragment {

    private static final String TAG   = "Announcements";
    public static final String TITLE = "Announcements";

    private NetworkManager             mNetworkManager;
    private RecyclerView               mRecyclerView;
    private MyAdapter                  mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public AnnouncementsFragment() {
        // Required empty public constructor.
    }

    private void getAnnouncements() {
        final Context context = this.getActivity().getApplicationContext();

        //checking user role stuff
        if (mNetworkManager.isUserHacker()) {
            Log.d(TAG, "HEY THE USER IS A HACKER");
        }
        if (mNetworkManager.isUserOrganizer()) {
            Log.d(TAG, "HEY THE USER IS AN ORGANIZER");
        }
        if (mNetworkManager.isUserSponsor()) {
            Log.d(TAG, "HEY THE USER IS A SPONSOR");
        }
        if (mNetworkManager.isUserVolunteer()) {
            Log.d(TAG, "HEY THE USER IS A VOLUNTEER");
        }

        mNetworkManager.getAnnouncements(new OneHackCallback<List<Announcement>>() {
            @Override
            public void success(List<Announcement> announcements) {
                // instantiate adapter
                mAdapter = new MyAdapter(announcements, context);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void failure(Throwable error) {
                Log.e(TAG, ":(", error);
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
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.announcements_title);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.announcement_rv);

        // use this setting to improve performance if changes in content do not change layout size
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //log user in - just temporary code until full app is ready
        mNetworkManager = NetworkManager.getInstance();
        getAnnouncements();
//        mNetworkManager.logUserIn("tom_erdmann@mac.com", "test", new OneHackCallback<User>() {
//            @Override
//            public void success(User response) {
//                Log.d(TAG, "Logged in!");
//                getAnnouncements();
//            }
//
//            @Override
//            public void failure(Throwable error) {
//                Log.d(TAG, "Couldn't log in :(");
//            }
//        });

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
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
        private Context context;

        //Container class for all the views in each "item" of the RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Animation.AnimationListener, View.OnTouchListener {
            private CardView cv;
            private TextView titleView;
            private TextView messageView;
            private TextView dateView;
            private TextView deleteView;
            private boolean isSlided;

            float x1 = 0;
            float y1 = 0;
            float x2 = 0;
            float y2 = 0;

            public ViewHolder(View v) {
                super(v);
                cv = (CardView) itemView.findViewById(R.id.announcement_card);
                titleView = (TextView) itemView.findViewById(R.id.announcement_title);
                messageView = (TextView) itemView.findViewById(R.id.announcement_message);
                dateView = (TextView) itemView.findViewById(R.id.announcement_date);
                deleteView = (TextView) itemView.findViewById(R.id.announcement_delete);

                //cv.setOnClickListener(this);
                cv.setOnTouchListener(this);
                deleteView.setOnClickListener(this);

                //CardView's work differently on pre-Lollipop devices. They use padding to create
                //shadow. Thus, on pre-L devices use margins to negate the padding. Otherwise
                //weird gaps appear between the cards
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cv.getLayoutParams();
                    mlp.topMargin -= (int)(cv.getMaxCardElevation() * 1.5 + (1 - Math.cos(Math.PI / 4)) * cv.getRadius());
                    mlp.rightMargin -= (int)(cv.getMaxCardElevation() + (1 - Math.cos(Math.PI / 4)) * cv.getRadius());
                    mlp.bottomMargin = 0;
                    cv.setLayoutParams(mlp);
                }

                isSlided = false;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event){
                int action = MotionEventCompat.getActionMasked(event);

                switch(action) {
                    case (MotionEvent.ACTION_DOWN) :
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case (MotionEvent.ACTION_MOVE) :
                        return true;
                    case (MotionEvent.ACTION_UP) :
                        x2 = event.getX();
                        y2 = event.getY();

                        //swipe right - return the card to its original position only if it was slided
                        //over in the first place
                        if(x1 < x2 && isSlided){
                            TranslateAnimation slideOver = new TranslateAnimation(0, deleteView.getWidth(), 0, 0);
                            slideOver.setDuration(800);
                            slideOver.setFillAfter(true);
                            slideOver.setAnimationListener(this);
                            cv.startAnimation(slideOver);
                        }
                        //swipe left - slide the card over only if it wasn't slided over already
                        else if (x2 < x1 && !isSlided){
                            TranslateAnimation slideOver = new TranslateAnimation(0, -deleteView.getWidth(), 0, 0);
                            slideOver.setDuration(800);
                            slideOver.setFillAfter(true);
                            slideOver.setAnimationListener(this);
                            cv.startAnimation(slideOver);
                        }

                        x1 = 0;
                        x2 = 0;

                        return true;
                    case (MotionEvent.ACTION_CANCEL) :
                        return true;
                    default :
                        return getActivity().onTouchEvent(event);
                }

                return true;
            }

            @Override
            public void onClick(View v) {
                //delete the announcement
                mNetworkManager.deleteAnnouncement(mAdapter.getAnnouncementAt(this.getAdapterPosition()), new OneHackCallback<GenericResponse>() {
                    @Override
                    public void success(GenericResponse response) {
                        Log.d(TAG, "Successfully deleted announcmenet!");
                    }

                    @Override
                    public void failure(Throwable error) {
                        Log.d(TAG, "Could not delete announcmenet ");
                        Toast.makeText(context, R.string.delete_announcement_failure, Toast.LENGTH_SHORT).show();
                    }
                });

                mAdapter.deleteAnnouncementAt(this.getAdapterPosition());
                mAdapter.notifyDataSetChanged(); //refresh recyler view
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
                }
                else {
                    //difference in CardView between Lollipop and pre-Lollipop devices. See ViewHolder
                    //constructor for more details
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                        cv.clearAnimation();
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cv.getLayoutParams();
                        mlp.leftMargin = 0;
                        mlp.rightMargin = -(int)(cv.getMaxCardElevation() + (1 - Math.cos(Math.PI / 4)) * cv.getRadius());
                        cv.setLayoutParams(mlp);
                    }
                    else {
                        cv.clearAnimation();
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cv.getLayoutParams();
                        mlp.rightMargin = 0;
                        mlp.leftMargin = 0;
                        cv.setLayoutParams(mlp);
                    }
                }

                isSlided = !isSlided;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //do nothing - required override
            }

            @Override
            public void onAnimationStart(Animation animation) {
                //do nothing - required override
            }
        }

        public MyAdapter(List<Announcement> announcementsIn, Context contextIn) {
            announcements = new ArrayList<Announcement>(announcementsIn);
            context = contextIn;
        }

        public Announcement getAnnouncementAt(int position){
            return announcements.get(position);
        }

        public void deleteAnnouncementAt(int position){
            announcements.remove(position);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new announcement view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.announcement, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int i) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.titleView.setText(announcements.get(i).getName());
            holder.messageView.setText(announcements.get(i).getInfo());

            //check when the announcement was created and display a date/time depending on that
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String dateToday = dateFormat.format(date);
            String dateAnnouncement = dateFormat.format(announcements.get(i).getBroadcastTime());
            if(dateToday.equals(dateAnnouncement)){
                //if the announcement was created today, display exact time it was created
                DateFormat timeFormat = new SimpleDateFormat("h:mm a");
                holder.dateView.setText(timeFormat.format(announcements.get(i).getBroadcastTime()));
            }
            else{
                //if the announcement was created yesterday or before, display the date it was created
                holder.dateView.setText(dateFormat.format(announcements.get(i).getBroadcastTime()));
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return announcements.size();
        }
    }
}
