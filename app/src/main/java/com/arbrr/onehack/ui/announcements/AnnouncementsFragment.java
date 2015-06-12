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
import com.arbrr.onehack.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Announcement> announcementList;

    public AnnouncementsFragment() {
        // Required empty public constructor.
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.announcement_rv);

        // use this setting to improve performance if changes in content do not change layout size
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //instantiate some dummy announcements
        announcementList = new ArrayList<Announcement>();
        Announcement a1 = new Announcement("Congratulations from Microsoft", "Awesome job at MHacks! Get free software and training" +
                "from Microsoft. Learn more at http://aka.ms/mhacksresources", "1/16/2015");
        Announcement a2 = new Announcement("Welcome to MHacks", "Welcome to MHacks VI!! We are very excited for you to " +
                "begin hacking away. Stay tuned for notifications.", "1/15/20115");
        Announcement a3 = new Announcement("Google", "This is a hyperlink (click me!): https://www.google.com", "6/10/2019");

        announcementList.add(a1);
        announcementList.add(a2);
        announcementList.add(a3);

        for(int i = 0; i < 15; ++i){
            Announcement a = new Announcement("Random Announcement " + i, "This is random annoucement #" + i, "1/10/10");
            announcementList.add(a);
        }

        // instantiate adapter
        mAdapter = new MyAdapter(announcementList);
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true); //programatically create action bar

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
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Animation.AnimationListener{
            private CardView cv;
            private TextView titleView;
            private TextView messageView;
            private TextView dateView;
            private TextView deleteView;
            private boolean isSlided;
            private Context context;

            public ViewHolder(View v, Context context) {
                super(v);
                cv = (CardView)itemView.findViewById(R.id.announcement_card);
                titleView = (TextView)itemView.findViewById(R.id.announcement_title);
                messageView = (TextView)itemView.findViewById(R.id.announcement_message);
                dateView = (TextView)itemView.findViewById(R.id.announcement_date);

                deleteView = (TextView)itemView.findViewById(R.id.announcement_delete);
                cv.setOnClickListener(this);
                deleteView.setOnClickListener(this);

                isSlided = false;
                this.context = context;
            }

            @Override
            public void onClick(View v){
                if(!isSlided){
                    //slide over the announcement to reveal a delete button
                    TranslateAnimation slideOver = new TranslateAnimation(0, -deleteView.getWidth(), 0, 0);
                    slideOver.setDuration(800);
                    slideOver.setFillAfter(true);
                    slideOver.setAnimationListener(this);
                    cv.startAnimation(slideOver);
                }
                else{
                    if(v.getId() == R.id.announcement_card) {
                        //slide back the announcement covering up the delete button
                        TranslateAnimation slideOver = new TranslateAnimation(0, deleteView.getWidth(), 0, 0);
                        slideOver.setDuration(800);
                        slideOver.setFillAfter(true);
                        slideOver.setAnimationListener(this);
                        cv.startAnimation(slideOver);
                    }
                    if(v.getId() == R.id.announcement_delete){
                        //delete the announcement
                        announcementList.remove(this.getAdapterPosition());
                        mAdapter.notifyDataSetChanged(); //refresh recyler view
                        Toast.makeText(context, "Delete Announcement!", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onAnimationEnd(Animation animation){
                //animation only moves around pixels. Now we have to actually move the view over
                if(!isSlided){
                    cv.clearAnimation();
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) cv.getLayoutParams();
                    mlp.rightMargin += deleteView.getWidth();
                    mlp.leftMargin = -deleteView.getWidth();
                    cv.setLayoutParams(mlp);
                }
                else{
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
            holder.titleView.setText(announcements.get(i).getTitle());
            holder.messageView.setText(announcements.get(i).getMessage());
            holder.dateView.setText(announcements.get(i).getDate());
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
